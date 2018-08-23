package com.github.xjs.util.queue;

public interface Callback<T extends QueueAble> {
	public void callback(T queueAble);
}