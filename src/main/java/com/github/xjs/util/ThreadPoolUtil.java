package com.github.xjs.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class ThreadPoolUtil {

	private final ExecutorService executor;

	private static ThreadPoolUtil instance = new ThreadPoolUtil();

	private ThreadPoolUtil() {
		this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
	}

	public static ThreadPoolUtil getInstance() {
		return instance;
	}

	public static <T> Future<T> execute(final Callable<T> runnable) {
		return getInstance().executor.submit(runnable);
	}

	public static Future<?> execute(final Runnable runnable) {
		return getInstance().executor.submit(runnable);
	}
	
	public static abstract class ParamRunnable<T> implements Runnable{
		private T param;
		public ParamRunnable(Supplier<T> paramSupplier) {
			if(paramSupplier != null) {
				this.param = paramSupplier.get();
			}
		}
		@Override
		public void run() {
			run(param);
		}
		public abstract void run(T param);
	}
	
	public static abstract class ParamCallable<R,P> implements Callable<R>{
		private P param;
		public ParamCallable(Supplier<P> paramSupplier) {
			if(paramSupplier != null) {
				this.param = paramSupplier.get();
			}
		}
		@Override
		public R call() {
			return call(param);
		}
		public abstract R call(P param);
	}
	/********************test*********************/
	public static class User{
		private int id;
		public User() {}
		public User(int id) {
			this.id= id;
		}
		public String toString() {
			return "User [id=" + id + "]";
		}
	}
	public static class UserProvider{
		private static ThreadLocal<User> userHolder = new  ThreadLocal<User>();
		public static void setUser(User user) {
			userHolder.set(user);
		}
		public static User getUser() {
			return userHolder.get();
		}
	}
	public static void main(String[] args)throws Exception {
		UserProvider.setUser(new User(1));
		System.out.println(UserProvider.getUser());
		ThreadPoolUtil.execute(new ParamRunnable<User>(UserProvider::getUser) {
			@Override
			public void run(User user) {
				System.out.println(user);
			}
		});
		System.in.read();
	}
}
