package cn.kurisu.bean.timedTask;

import cn.kurisu.bean.timePeriod.TimePeriod;

import java.util.Date;

/**
 * Created by ym on 2017/2/4 0004.
 */
public abstract class AbstractTimedTask implements TimedTask {
    /**
     * 任务开始时间
     * */
    protected volatile Date startTime;

    /**
     * 时间周期
     * */
    protected volatile TimePeriod timePeriod;

    /**
     * 执行时间
     * */
    protected volatile Date executionTime;

    public AbstractTimedTask(Date startTime, TimePeriod timePeriod) {
        this.startTime = startTime;
        this.timePeriod = timePeriod;
        calculateExecutionTime();
    }

    /**
     * 计算实际执行时间
     * */
    protected void calculateExecutionTime() {
        // 执行时间初始为开始时间
        long initExecutionTime = startTime.getTime();
        long curTime = System.currentTimeMillis();

        // 每次增加周期长的时间直到执行时间大于当前时间，如果周期时间为0，则退出循环
        while(initExecutionTime < curTime) {
            // 获取当前时间的周期
            long timePeriodMillis = timePeriod.getTimePeriod();
            if(timePeriodMillis <= 0L)
                break;
            initExecutionTime += timePeriodMillis;
            // 模拟一次执行
            timePeriod.afterExecuteTask();
        }

        executionTime = new Date(initExecutionTime);
    }

    @Override
    public synchronized void setStartTime(Date startTime) {
        this.startTime = startTime;
        calculateExecutionTime();
    }

    @Override
    public synchronized Date getStartTime() {
        return startTime;
    }

    @Override
    public synchronized void setTimePeriod(TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
        calculateExecutionTime();
    }

    @Override
    public synchronized TimePeriod getTimePeriod() {
        return timePeriod;
    }

    @Override
    public synchronized Date getExecutionTime() {
        return executionTime;
    }

    /**
     * 同时更新开始时间以及时间周期
     * */
    public synchronized void setStartTimeAndTimePeriod(Date startTime, TimePeriod timePeriod) {
        this.startTime = startTime;
        this.timePeriod = timePeriod;
        calculateExecutionTime();
    }
}
