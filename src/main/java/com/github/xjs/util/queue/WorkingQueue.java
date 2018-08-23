package com.github.xjs.util.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.xjs.util.LogUtil;
import com.github.xjs.util.ThreadPoolUtil;

public class WorkingQueue<T extends QueueAble> {

	private BlockingQueue<CallbackQueueAble<T>> queue;
	private final ThreadFactory threadFactory;
	private Thread thread;
	private AtomicBoolean started = new AtomicBoolean(false);
	private volatile boolean shouldContinue = false;

	public WorkingQueue() {
		this(null);
	}

	public WorkingQueue(final ThreadFactory tf) {
		this.queue = new LinkedBlockingQueue<CallbackQueueAble<T>>();
		this.threadFactory = tf == null ? Executors.defaultThreadFactory() : tf;
		this.thread = null;
	}

	public void start() {
		if (started.getAndSet(true)) {
			return;
		}
		shouldContinue = true;
		thread = threadFactory.newThread(new Runnable() {
			public void run() {
				while (shouldContinue) {
					try {
						CallbackQueueAble<T> callableRequest = queue.take();
						T baseRequest = callableRequest.getQueueAble();
						Callback<T> callback = callableRequest.getCallback();
						boolean isCallbackExecuteParallel = callableRequest.isCallbackExecuteParallel();
						if(isCallbackExecuteParallel) {
							ThreadPoolUtil.execute(()->{callback.callback(baseRequest);});
						}else {
							callback.callback(baseRequest);
						}
					} catch (Exception e) {
						LogUtil.error(WorkingQueue.class, ()->"Unexpected message caught... Shouldn't be here", ()->e);
					}
				}
			}
		});
		thread.start();
	}

	public void stop() {
		started.set(false);
		shouldContinue = false;
		thread.interrupt();
	}

	public void execute(T request, Callback<T> callback, boolean callbackExecuteParallel) {
		if (!started.get()) {
			start();
		}
		queue.add(new CallbackQueueAble<T>(request, callback, callbackExecuteParallel));
	}
}