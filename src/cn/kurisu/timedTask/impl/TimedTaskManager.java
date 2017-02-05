package cn.kurisu.timedTask.impl;

import cn.kurisu.bean.timedTask.YTask;
import cn.kurisu.timedTask.TaskManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ym on 2017/2/5 0005.
 *
 * 定时任务管理器
 *
 * 实现思路：1.使用map存储需要定时执行的任务
 *          2.使用优先队列存储临近执行的任务，同时从map中移除这些任务
 *          3.进入队列中的任务不可取消与修改
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
    private static final long DEFAULT_EXCUTE_TIME_ERROR = 2000;

    /**
     * 存储任务
     * */
    private ConcurrentMap<Integer, YTask> tasks = null;

    /**
     * 执行误差时间
     * */
    private volatile long excuteTimeError;

    public TimedTaskManager() {
        this(DEFAULT_MAP_INIT_CAPACITY, DEFAULT_EXCUTE_TIME_ERROR);
    }

    public TimedTaskManager(int initCapacity) {
        this(initCapacity, DEFAULT_EXCUTE_TIME_ERROR);
    }

    public TimedTaskManager(long excuteTimeError) {
        this(DEFAULT_MAP_INIT_CAPACITY, excuteTimeError);
    }

    public TimedTaskManager(int initCapacity, long excuteTimeError) {
        this.tasks = new ConcurrentHashMap<Integer, YTask>(initCapacity);
        if(!validateExcuteTimeError(excuteTimeError))
            throw new RuntimeException("执行误差时间必须大于等于0");
        this.excuteTimeError = excuteTimeError;
    }


    @Override
    public boolean addTask(YTask task) {
        if (!validateTask(task))
            return false;

        tasks.put(task.getTaskNo(), task);
        return true;
    }

    @Override
    public YTask removeTask(Integer taskNo) {
        return tasks.remove(taskNo);
    }

    @Override
    public void removeTask(YTask task) {
        tasks.remove(task.getTaskNo());
    }

    @Override
    public YTask getTask(Integer taskNo) {
        return tasks.get(taskNo);
    }

    // TODO 进行加锁
    public long getExcuteTimeError() {
        return excuteTimeError;
    }

    public void setExcuteTimeError(long excuteTimeError) {
        this.excuteTimeError = excuteTimeError;
    }

    /**
     * 校验执行误差时间
     * */
    private boolean validateExcuteTimeError(long excuteTimeError) {
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

        return task.getExecutionTime() + excuteTimeError >= System.currentTimeMillis();
    }
}
