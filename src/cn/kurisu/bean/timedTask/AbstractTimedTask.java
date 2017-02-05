package cn.kurisu.bean.timedTask;

import cn.kurisu.bean.timePeriod.TimePeriod;

import java.util.Date;

/**
 * Created by ym on 2017/2/4 0004.
 */
public abstract class AbstractTimedTask implements TimedTask {
    /**
     * 任务编号
     * */
    protected volatile Integer taskNo;

    /**
     * 任务开始时间
     * */
    protected volatile long startTime;

    /**
     * 时间周期
     * */
    protected volatile TimePeriod timePeriod;

    /**
     * 执行时间
     * */
    protected volatile long executionTime;

    public AbstractTimedTask(Integer taskNo, long startTime, TimePeriod timePeriod) {
        this.taskNo = taskNo;
        this.startTime = startTime;
        this.timePeriod = timePeriod;
        calculateExecutionTime(startTime);
    }

    /**
     * 计算实际执行时间
     * */
    protected void calculateExecutionTime(long initTime) {
        // 执行时间初始为开始时间
        long initExecutionTime = initTime;
        long curTime = System.currentTimeMillis();

        // 每次增加周期长的时间直到执行时间大于当前时间，如果周期时间为0，则退出循环
        while(initExecutionTime < curTime) {
            // 获取当前时间的周期
            long timePeriodMillis = timePeriod.getTimePeriod();
            if(timePeriodMillis <= 0L)
                break;
            initExecutionTime += timePeriodMillis;
            // 同时通知时间周期已经执行过一次
            timePeriod.afterExecuteTask();
        }

        executionTime = initExecutionTime;
    }


    @Override
    public Integer getTaskNo() {
        return taskNo;
    }

    @Override
    public synchronized void setStartTime(long startTime) {
        this.startTime = startTime;
        calculateExecutionTime(this.startTime);
    }

    @Override
    public synchronized long getStartTime() {
        return startTime;
    }

    @Override
    public synchronized void setTimePeriod(TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
        calculateExecutionTime(this.startTime);
    }

    @Override
    public synchronized TimePeriod getTimePeriod() {
        return timePeriod;
    }

    @Override
    public synchronized long getExecutionTime() {
        return executionTime;
    }

    @Override
    public synchronized void afterExecuteTask() {
        calculateExecutionTime(this.executionTime);
    }

    /**
     * 同时更新开始时间以及时间周期
     * */
    public synchronized void setStartTimeAndTimePeriod(long startTime, TimePeriod timePeriod) {
        this.startTime = startTime;
        this.timePeriod = timePeriod;
        calculateExecutionTime(this.startTime);
    }

    @Override
    public int hashCode() {
        return this.getTaskNo().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(obj == null)
            return false;

        if(!(obj instanceof  YTask))
            return false;

        return this.getTaskNo() == ((YTask) obj).getTaskNo();
    }
}
