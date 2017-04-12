package com.github.xjs.util.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkingQueue<T extends BaseRequest> {

	private static Logger log = LoggerFactory.getLogger(WorkingQueue.class);
	private BlockingQueue<T> queue;
	private final ThreadFactory threadFactory;
	private Thread thread;
	private AtomicBoolean started = new AtomicBoolean(false);
	private volatile boolean shouldContinue = false;

	public WorkingQueue() {
		this(null);
	}

	public WorkingQueue(final ThreadFactory tf) {
		this.queue = new LinkedBlockingQueue<T>();
		this.threadFactory = tf == null ? Executors.defaultThreadFactory() : tf;
		this.thread = null;
	}

	public void start() {
		if (started.getAndSet(true)) {
			// I prefer if we throw a runtime IllegalStateException here,
			// but I want to maintain semantic backward compatibility.
			// So it is returning immediately here
			return;
		}
		shouldContinue = true;
		thread = threadFactory.newThread(new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				while (shouldContinue) {
					try {
						T req = queue.take();
						req.getLazyExecutor().lazyExecute(req);
					} catch (Exception e) {
						log.error("Unexpected message caught... Shouldn't be here", e);
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

	public void execute(T request) {
		if (!started.get()) {
			start();
		}
		queue.add(request);
	}
}