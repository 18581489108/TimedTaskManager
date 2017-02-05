package cn.kurisu.bean.timedTask.impl;

import cn.kurisu.bean.timePeriod.TimePeriod;
import cn.kurisu.bean.timedTask.AbstractTimedTask;

import java.util.Date;

/**
 * Created by ym on 2017/2/4 0004.
 *
 * 定时发送消息任务
 */
public class SendMessageTimedTask extends AbstractTimedTask {

    public SendMessageTimedTask(Integer taskNo, long startTime, TimePeriod timePeriod) {
        super(taskNo, startTime, timePeriod);
    }

    @Override
    public void executeTask() {
        System.out.println("滑稽");
    }


}
