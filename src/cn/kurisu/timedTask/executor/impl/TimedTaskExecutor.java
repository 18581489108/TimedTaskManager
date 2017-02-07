package cn.kurisu.timedTask.executor.impl;

import cn.kurisu.bean.timedTask.YTask;
import cn.kurisu.timedTask.TaskManager;
import cn.kurisu.timedTask.executor.TaskExecutor;

import java.util.Collection;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ym on 2017/2/6 0006.
 *
 * 定时任务执行者
 */
public class TimedTaskExecutor implements TaskExecutor {
    /**
     * 队列的默认大小
     * */
    private static final int DEFAULT_INIT_CAPACITY = 50;

    /**
     * 并发执行任务的默认个数
     * */
    private static final int DEAFULT_COUNT_OF_CONCURRENT = 3;

    /**
     * 使用优先队列来存储任务，执行时间小的优先级高
     * */
    private PriorityQueue<YTask> queue = null;

    /**
     * 执行者线程
     * */
    private Thread executorThread = null;

    /**
     * 持有该任务执行者的管理者
     * */
    private TaskManager taskManager = null;

    /**
     * 并发执行任务的个数
     * */
    private int countOfConcurrent;

    public TimedTaskExecutor(TaskManager taskManager) {
        this(taskManager, DEFAULT_INIT_CAPACITY);

    }

    public TimedTaskExecutor(TaskManager taskManager, int capacity) {
        this(taskManager, capacity, DEAFULT_COUNT_OF_CONCURRENT);
    }

    public TimedTaskExecutor(TaskManager taskManager, int capacity, int countOfConcurrent) {
        if(taskManager == null)
            throw new NullPointerException("TaskManager不应该为null");
        this.taskManager = taskManager;
        this.countOfConcurrent = countOfConcurrent;
        init(capacity);
    }

    private void init(int capacity) {
        initQueue(capacity);
        initExecutor();
    }

    /**
     * 执行器开始工作
     * */
    @Override
    public void start() {
        executorThread.start();
    }

    /**
     * 初始化执行线程
     * */
    private void initExecutor() {
        executorThread = new Thread(new TaskHandler(countOfConcurrent));
    }

    /**
     * 初始化队列
     * */
    private void initQueue(int capacity) {
        queue = new PriorityQueue<YTask>(capacity,
                (YTask task1, YTask task2) -> (int) (task1.getExecutionTime() - task2.getExecutionTime()));
    }


    @Override
    public void addTask(YTask task) {
        synchronized (queue) {
            queue.add(task);
            // 同时唤醒执行线程
            queue.notify();
        }
    }

    @Override
    public void addTasks(Collection<YTask> tasks) {
        synchronized (queue) {
            queue.addAll(tasks);
            // 同时唤醒执行线程
            queue.notify();
        }
    }

    /**
     * 任务处理者
     * */
    private class TaskHandler implements Runnable {
        /**
         * 任务执行线程池
         * */
        private ExecutorService threadPool = null;

        public TaskHandler(int poolSize) {
            threadPool = Executors.newFixedThreadPool(poolSize);
        }

        @Override
        public void run() {
            while(true) {
                synchronized (queue) {
                    // 如果队列为空则阻塞当前线程
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    while (!queue.isEmpty() && queue.peek().getExecutionTime() <= System.currentTimeMillis()) {
                        YTask curTask = queue.poll();
                        // 托管给线程池去执行任务
                        threadPool.execute(() -> {
                            curTask.executeTask();
                            curTask.afterExecuteTask(taskManager);
                        });
                    }


                    // 如果队列不为空，则将线阻塞眠到下一个任务执行的时间
                    if(!queue.isEmpty()) {
                        YTask curTask = queue.peek();
                        try {
                            queue.wait(curTask.getExecutionTime() - System.currentTimeMillis());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }
}













