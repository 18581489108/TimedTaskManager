package cn.kurisu.bean.timedTask;

import java.util.Date;

/**
 * Created by ym on 2017/2/4 0004.
 *
 * 任务的基本数据结构
 */
public interface YTask {
    /**
     * 执行任务
     * */
    void executeTask();

    /**
     * 执行任务的时间
     * */
    Date getExecutionTime();
}
