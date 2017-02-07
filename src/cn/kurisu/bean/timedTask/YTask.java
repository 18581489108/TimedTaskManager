package cn.kurisu.bean.timedTask;

import cn.kurisu.timedTask.TaskManager;

/**
 * Created by ym on 2017/2/4 0004.
 *
 * 任务的基本数据结构
 */
public interface YTask {
    /**
     * 任务编号
     * */
    Integer getTaskNo();

    /**
     * 执行任务
     * */
    void executeTask();

    /**
     * 执行任务的时间
     * */
    long getExecutionTime();

    /**
     * 执行完任务后回调，并传入任务管理器
     * */
    void afterExecuteTask(TaskManager taskManager);
}
