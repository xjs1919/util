package com.github.xjs.util.queue;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年8月23日 下午1:21:59<br/>
 */
public class CallbackQueueAble<T extends QueueAble> {
	private T queueAble;
	private Callback<T> callback;
	private boolean callbackExecuteParallel;
	public CallbackQueueAble(T queueAble, Callback<T> callback, boolean callbackExecuteParallel) {
		this.queueAble = queueAble;
		this.callback = callback;
		this.callbackExecuteParallel = callbackExecuteParallel;
	}
	public T getQueueAble() {
		return queueAble;
	}
	public Callback<T> getCallback() {
		return callback;
	}
	public boolean isCallbackExecuteParallel() {
		return callbackExecuteParallel;
	}
}
