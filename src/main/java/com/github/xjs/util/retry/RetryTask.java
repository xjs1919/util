/**
 * 
 */
package com.github.xjs.util.retry;

import java.util.UUID;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.github.xjs.util.retry.RetryAble;
import com.github.xjs.util.retry.RetryTask;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月15日 上午10:22:48
 */
public class RetryTask implements Delayed {
	private Class<? extends RetryAble> task;
    private int interval;
    private long startTime;
    private String uuid;

    public RetryTask(Class<? extends RetryAble> task, int interval){
        this.task = task;
        this.interval = interval;
        this.startTime = System.currentTimeMillis() + interval*1000L;
        this.uuid = UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public int compareTo(Delayed other) {
        if (other == this){
            return 0;
        }
        if(other instanceof RetryTask){
        	RetryTask otherTask = (RetryTask)other;
            long otherStartTime = otherTask.getStartTime();
            return (int)(this.startTime - otherStartTime);
        }
        return 0;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

	public Class<? extends RetryAble> getTask() {
		return task;
	}

	public int getInterval() {
		return interval;
	}

	public long getStartTime() {
		return startTime;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + interval;
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RetryTask other = (RetryTask) obj;
		if (interval != other.interval)
			return false;
		if (startTime != other.startTime)
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RetryTask [task=" + task + ", interval=" + interval + ", startTime=" + startTime + ", uuid=" + uuid
				+ "]";
	}
}
