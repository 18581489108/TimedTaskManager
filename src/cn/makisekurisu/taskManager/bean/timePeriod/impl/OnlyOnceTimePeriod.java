package cn.makisekurisu.taskManager.bean.timePeriod.impl;

import cn.makisekurisu.taskManager.bean.timePeriod.AbstractTimePeriod;

/**
 * Created by ym on 2017/2/4 0004.
 *
 * 只执行一次
 */
public class OnlyOnceTimePeriod extends AbstractTimePeriod {

    @Override
    public long getTimePeriod() {
        return 0;
    }
}
