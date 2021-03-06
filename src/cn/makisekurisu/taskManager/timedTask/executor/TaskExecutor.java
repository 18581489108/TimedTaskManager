package cn.makisekurisu.taskManager.timedTask.executor;

import cn.makisekurisu.taskManager.bean.timedTask.YTask;

import java.util.Collection;

/**
 * Created by ym on 2017/2/6 0006.
 *
 * 任务执行器
 * 加入执行器的任务不允许撤销
 */
public interface TaskExecutor {
    /**
     * 加入任务
     * */
    void addTask(YTask task);

    /**
     * 加入一系列任务
     * */
    void addTasks(Collection<? extends YTask> tasks);

    /**
     * 执行器正式开始执行任务
     * */
    void start();
}
