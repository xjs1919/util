package com.github.xjs.util.queue;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年8月23日 下午1:21:59<br/>
 */
public class CallbackQueueAble<T extends QueueAble> {
	private T queueAble;
	private Callback<T> callback;
	public CallbackQueueAble(T queueAble, Callback<T> callback) {
		this.queueAble = queueAble;
		this.callback = callback;
	}
	public T getQueueAble() {
		return queueAble;
	}
	public Callback<T> getCallback() {
		return callback;
	}
}
