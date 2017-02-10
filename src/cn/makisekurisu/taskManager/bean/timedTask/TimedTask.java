package cn.kurisu.bean.timedTask;

import cn.kurisu.bean.timePeriod.TimePeriod;

/**
 * Created by ym on 2017/2/4 0004.
 *
 * 定时任务的基础数据结构
 */
public interface TimedTask extends YTask {
    /**
     * 定时任务的开始时间
     * */
    void setStartTime(long startTime);

    long getStartTime();

    /**
     * 周期类型
     * */
    void setTimePeriod(TimePeriod timePeriod);

    TimePeriod getTimePeriod();

}
