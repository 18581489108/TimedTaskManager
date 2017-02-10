package cn.kurisu.timedTask.impl;

import cn.kurisu.bean.timedTask.YTask;
import cn.kurisu.timedTask.TaskManager;
import cn.kurisu.timedTask.executor.TaskExecutor;
import cn.kurisu.timedTask.executor.impl.TimedTaskExecutor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;

/**
 * Created by ym on 2017/2/5 0005.
 *
 * 定时任务管理器
 *
 * 实现思路：1.使用map存储需要定时执行的任务
 *          2.使用优先队列存储临近执行的任务，同时从map中移除这些任务
 *          3.进入队列中的任务不可取消与修改
 *          4.遍历一次map，取出即将执行的任务，周期为2 * 时间范围
 *
 */
public class TimedTaskManager implements TaskManager {
    /**
     * 任务池默认大小
     * */
    private static final int DEFAULT_MAP_INIT_CAPACITY = 100;

    /**
     * 临近执行的默认误差时间 2000ms
     * */
    private static final long DEFAULT_EXECUTE_TIME_ERROR = 2000;

    /**
     * 存储任务
     * */
    private ConcurrentMap<Integer, YTask> tasks = null;

    /**
     * 执行时间范围
     * */
    private volatile long executeTimeRange;

    /**
     * 任务池信号量，如果没有任务则阻塞扫描线程
     * */
    private Semaphore tasksSemaphore = new Semaphore(0);

    /**
     * 用于标识是否处于扫描状态
     * */
    private AtomicBoolean isScanning = new AtomicBoolean(false);

    /**
     * 扫描锁
     * */
    private Lock scanLock = new ReentrantLock();

    /**
     * 定时扫描任务池线程
     * */
    private Thread scanThread = new Thread(new TasksScanner());

    /**
     * 任务执行器
     * */
    private TaskExecutor taskExecutor = null;

    public TimedTaskManager() {
        this(DEFAULT_MAP_INIT_CAPACITY, DEFAULT_EXECUTE_TIME_ERROR);
    }

    public TimedTaskManager(int initCapacity) {
        this(initCapacity, DEFAULT_EXECUTE_TIME_ERROR);
    }

    public TimedTaskManager(long excuteTimeError) {
        this(DEFAULT_MAP_INIT_CAPACITY, excuteTimeError);
    }

    public TimedTaskManager(int initCapacity, long executeTimeRange) {
        this.tasks = new ConcurrentHashMap<Integer, YTask>(initCapacity);
        if(!validateExecuteTimeRange(executeTimeRange))
            throw new RuntimeException("执行误差时间必须大于等于0");
        this.executeTimeRange = executeTimeRange;

        initManager();

    }

    private void initManager() {
        taskExecutor = new TimedTaskExecutor(this);
        scanThread.start();
        taskExecutor.start();
    }


    @Override
    public boolean addTask(YTask task) {
        // 如果处于扫描状态，则等待扫描结束
        while (isScanning.get());

        if (!validateTask(task) || tasks.containsKey(task.getTaskNo()))
            return false;
        tasks.put(task.getTaskNo(), task);
        // 释放一个信号量
        tasksSemaphore.release();
        return true;

    }

    @Override
    public YTask removeTask(Integer taskNo) {
        // 如果处于扫描状态，则等待扫描结束
        while (isScanning.get());

        YTask task = tasks.remove(taskNo);
        if(task != null)
            tasksSemaphore.tryAcquire();
        return task;
    }

    @Override
    public void removeTask(YTask task) {
        // 如果处于扫描状态，则等待扫描结束
        while (isScanning.get());

        YTask curTask = tasks.remove(task.getTaskNo());
        if(curTask != null)
            tasksSemaphore.tryAcquire();
    }

    @Override
    public YTask getTask(Integer taskNo) {
        return tasks.get(taskNo);
    }

    public long getExecuteTimeRange() {
        return executeTimeRange;
    }

    public void setExecuteTimeRange(long executeTimeRange) {
        scanLock.lock();
        try {
            this.executeTimeRange = executeTimeRange;
        } finally {
            scanLock.unlock();
        }
    }

    /**
     * 校验执行误差时间
     * */
    private boolean validateExecuteTimeRange(long excuteTimeError) {
        return excuteTimeError >= 0L;
    }

    /**
     * 判断该任务是否可以加入等待中
     *
     * 如果该任务的执行时间 + 误差时间比当前时间大，则认为可以加入等待
     * */
    private boolean validateTask(YTask task) {
        if(task == null || task.getTaskNo() == null)
            return false;

        return task.getExecutionTime() + executeTimeRange >= System.currentTimeMillis();
    }

    /**
     * 定时扫描任务池
     * */
    private class TasksScanner implements Runnable {

        @Override
        public void run() {
            while(true) {
                try {
                    tasksSemaphore.acquire();
                    if(isScanning.compareAndSet(false, true))
                        handleTask();
                    isScanning.set(false);
                    tasksSemaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(getSleepTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * 处理任务，把即将执行的任务加入执行队列中
         * */
        private void handleTask() {
            scanLock.lock();
            try {
                List<YTask> willExecuteOfTasks = new ArrayList<YTask>();

                long curTime = System.currentTimeMillis();

                // 遍历任务池，把即将执行的任务加入到执行队列中，并将其从任务池中删除
                Iterator<Map.Entry<Integer, YTask>> iterator = tasks.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, YTask> entry = iterator.next();
                    YTask task = entry.getValue();
                    if(isTaskInTimeRange(curTime, task, executeTimeRange)) {
                        willExecuteOfTasks.add(task);
                        iterator.remove();
                        // 从信号量中移除一个可用信号量
                        tasksSemaphore.tryAcquire();
                    }
                }

                taskExecutor.addTasks(willExecuteOfTasks);
            } finally {
                scanLock.unlock();
            }
        }

        /**
         * 获取休眠时间
         * */
        private long getSleepTime() {
            return executeTimeRange;
        }

        /**
         * 判断任务是否处于执行时间范围内
         *
         * 执行时间与当前时间的差的绝对值处于执行时间范围内时，返回true，反之false
         * */
        private boolean isTaskInTimeRange(long curTime, YTask task, long executeTimeRange) {
            return Math.abs(curTime - task.getExecutionTime()) <= executeTimeRange;
        }
    }
}
