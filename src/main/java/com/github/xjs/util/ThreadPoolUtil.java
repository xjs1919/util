package com.github.xjs.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;
/**
 * 关于ThreadLocal参数传递问题可以参考：https://github.com/alibaba/transmittable-thread-local
 * */
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
		private Consumer<T> paramConsumer;
		public ParamRunnable(Supplier<T> paramSupplier, Consumer<T> paramConsumer) {
			if(paramSupplier != null) {
				this.param = paramSupplier.get();
			}
			this.paramConsumer = paramConsumer;
		}
		@Override
		public void run() {
			paramConsumer.accept(param);
			run(param);
		}
		public abstract void run(T param);
	}
	
	public static abstract class ParamCallable<R,P> implements Callable<R>{
		private P param;
		private Consumer<P> paramConsumer;
		public ParamCallable(Supplier<P> paramSupplier,  Consumer<P> paramConsumer) {
			if(paramSupplier != null) {
				this.param = paramSupplier.get();
			}
			this.paramConsumer = paramConsumer;
		}
		@Override
		public R call() {
			paramConsumer.accept(param);
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
		System.out.println("1"+UserProvider.getUser());
		ThreadPoolUtil.execute(new ParamCallable<Void,User>(UserProvider::getUser, UserProvider::setUser) {
			@Override
			public Void call(User user) {
				System.out.println("1"+user);
				System.out.println("1"+UserProvider.getUser());
				ThreadPoolUtil.execute(new ParamCallable<Void, User>(UserProvider::getUser, UserProvider::setUser) {
					@Override
					public Void call(User user) {
						System.out.println("3"+user);
						System.out.println("3"+UserProvider.getUser());
						return null;
					}
				});
				return null;
			}
		});
		UserProvider.setUser(new User(2));
		System.out.println("2"+UserProvider.getUser());
		ThreadPoolUtil.execute(new ParamCallable<Void, User>(UserProvider::getUser, UserProvider::setUser) {
			@Override
			public Void call(User user) {
				System.out.println("2"+user);
				System.out.println("2"+UserProvider.getUser());
				return null;
			}
		});
		System.in.read();
	}
}
