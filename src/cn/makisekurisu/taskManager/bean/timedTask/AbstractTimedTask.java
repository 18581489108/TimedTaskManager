package cn.makisekurisu.taskManager.bean.timedTask;

import cn.makisekurisu.taskManager.bean.timePeriod.TimePeriod;
import cn.makisekurisu.taskManager.timedTask.TaskManager;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    /**
     * 读写锁
     * */
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

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
        rwLock.readLock().lock();
        try {
            return taskNo;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public void setStartTime(long startTime) {
        rwLock.writeLock().lock();
        try {
            this.startTime = startTime;
            calculateExecutionTime(this.startTime);
        } finally {
          rwLock.writeLock().unlock();
        }
    }

    @Override
    public long getStartTime() {
        rwLock.readLock().lock();
        try {
            return startTime;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public void setTimePeriod(TimePeriod timePeriod) {
        rwLock.writeLock().lock();
        try {
            this.timePeriod = timePeriod;
            calculateExecutionTime(this.startTime);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public TimePeriod getTimePeriod() {
        rwLock.readLock().lock();
        try {
            return timePeriod;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public long getExecutionTime() {
        rwLock.readLock().lock();
        try {
            return executionTime;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public void afterExecuteTask(TaskManager taskManager) {
        rwLock.writeLock().lock();
        try {
            calculateExecutionTime(this.startTime);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * 同时更新开始时间以及时间周期
     * */
    public synchronized void setStartTimeAndTimePeriod(long startTime, TimePeriod timePeriod) {
        rwLock.writeLock().lock();
        try {
            this.startTime = startTime;
            this.timePeriod = timePeriod;
            calculateExecutionTime(this.startTime);
        } finally {
            rwLock.writeLock().unlock();
        }
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

    @Override
    public String toString() {
        return "taskNo:" + taskNo + ", executionTime:" + executionTime;
    }
}
