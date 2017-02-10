package cn.makisekurisu.taskManager.bean.timePeriod;

/**
 * Created by ym on 2017/2/4 0004.
 *
 * 时间周期
 */
public interface TimePeriod {
    /**
     * 获取下一次执行的时间周期
     * */
    long getTimePeriod();

    /**
     * 执行完任务后回调
     * */
    void afterExecuteTask();
}
