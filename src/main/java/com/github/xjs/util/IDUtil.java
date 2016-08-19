/**
 * 
 */
package com.github.xjs.util;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月19日 下午2:39:56
 * 参考：http://chuansong.me/n/2459549
 */
public class IDUtil {
	/**
	 * 
	 * birthday of Cathy
	 */
	private static final long BIRTHDAY_OF_CATHY = 1441313880000L;
	/**
	 * 序列号
	 */
	private static final AtomicInteger UNIQUE_ID = new AtomicInteger(0);
	/**
	 * 1毫秒以内最大的并发数 = 序列号的最大值
	 */
	private static final int MAX_ID = 2 << 7;
	/**
	 * 随机
	 */
	private static final Random RND = new Random();

	public static long getId(int biz, int server) {
		long id = System.currentTimeMillis() - BIRTHDAY_OF_CATHY;// 毫秒数,30年
		id = id << 5 | biz;         // 业务线             31
		id = id << 5;               // 预留
		id = id << 7 | server;      // 机器号               127
		id = id << 7 | getSN();     // 毫秒内的并发   127
		return id;
	}

	private static int getSN() {
		int num = UNIQUE_ID.incrementAndGet();
		if (num == MAX_ID) {
			num = RND.nextInt(10);
			UNIQUE_ID.set(num);
		}
		return num;
	}

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
		int threadCount = 10000;//单机1万并发没有问题，再大没有做尝试
		final CountDownLatch endLatch = new CountDownLatch(threadCount);
		final CountDownLatch startLatch = new CountDownLatch(1);
		Thread[] arr = new Thread[threadCount];
		for (int i = 0; i < threadCount; i++) {
			arr[i] = new Thread(new Runnable() {
				public void run() {
					try {
						startLatch.await();
					} catch (Exception e) {
						e.printStackTrace();
					}
					String num = "" + getId(1, 1);
					// System.out.println(Thread.currentThread().getName()+":"+num);
					map.put(num, num);
					endLatch.countDown();
				}
			});
		}
		for (int i = 0; i < threadCount; i++) {
			arr[i].start();
		}
		startLatch.countDown();
		endLatch.await();
		System.out.println(map.size());
		long end = System.currentTimeMillis();
		System.out.println("use:" + (end - start));
	}

	public static void main2(String[] args) {
		// 30年
		long thirtyYears = 30L * 365 * 24 * 3600 * 1000;
		System.out.println(Long.toBinaryString(thirtyYears));// 1101110001000110110000110010100000000000，
																// 40位
		int biz = 31; // 业务线
		System.out.println(Integer.toBinaryString(biz)); // 11111， 5位

		// int reserved = 31;//5位

		int servers = 127;// 最大127台机器
		System.out.println(Integer.toBinaryString(servers)); // 1111111，7位

		// 一秒最大并大10万，1毫秒是100
		int concurrent = 100;
		System.out.println(Integer.toBinaryString(concurrent)); // 1100100,7位

		System.out.println("----------------------------------------");
	}

}
