package cn.makisekurisu.taskManager.bean.timePeriod.impl;

import cn.makisekurisu.taskManager.bean.timePeriod.AbstractTimePeriod;

/**
 * Created by ym on 2017/2/4 0004.
 *
 * 固定间隔类型的时间周期
 */
public class FixedIntervalTimedPeriod extends AbstractTimePeriod {
    /**
     * 固定间隔
     * */
    private volatile long timePeriod;

    public FixedIntervalTimedPeriod(long timePeriod) {
        if(timePeriod < 0)
            throw new RuntimeException("间隔不允许小于0");

        this.timePeriod = timePeriod;
    }

    @Override
    public long getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(long timePeriod) {
        if(timePeriod < 0)
            throw new RuntimeException("间隔不允许小于0");

        this.timePeriod = timePeriod;
    }
}
