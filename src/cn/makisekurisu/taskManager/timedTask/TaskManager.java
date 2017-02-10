package cn.kurisu.timedTask;

import cn.kurisu.bean.timedTask.YTask;

/**
 * Created by ym on 2017/2/5 0005.
 *
 * 任务管理器
 */
public interface TaskManager {
    /**
     * 将任务加入等待队列
     *
     * 如果成功加入等待队列返回true，加入失败返回false
     * */
    boolean addTask(YTask task);

    /**
     * 通过任务编号将任务移除等待队列
     *
     * 移除成功返回任务对象，失败返回null
     * */
    YTask removeTask(Integer taskNo);

    /**
     * 将任务移除等待队列
     * */
    void removeTask(YTask task);

    /**
     * 通过任务编号获取任务
     * */
    YTask getTask(Integer taskNo);

}
