/**
 * 
 */
package com.github.xjs.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月19日 下午2:39:56 
 * 参考：
 * http://www.cnblogs.com/ppli/p/6542915.html
 * http://chuansong.me/n/2459549
 * 1位最高位
 * 8位机器号，256个节点
 * 14位序列号，毫秒内并发16383
 * 41位时间戳，可用69年
 */
public class IDUtil {

	/** 开始时间截 ，birthday of Cathy */
	private final long start = 1441313880000L;

	/** 生成序列的掩码 */
	private final long sequenceMax = (1 << 14) - 1;

	/** 毫秒内序列(0~sequenceMax) */
	private long sequence = 0L;

	/** 上次生成ID的时间截 */
	private long lastTimestamp = -1L;

	private static IDUtil instance = new IDUtil();

	private IDUtil(){
		
	}

	public static long getId(int serverId) {
		return instance.nextId(serverId);
	}

	/**
	 * 获得下一个ID (该方法是线程安全的)
	 * 
	 * @return SnowflakeId
	 */
	public synchronized long nextId(int serverId) {
		long timestamp = timeGen();
		if (timestamp < lastTimestamp) {// 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过
			long offset = lastTimestamp - timestamp;
			if (offset <= 10) {//如果发生时钟回退在可接受的范围以内
				try {
					Thread.sleep(offset + 1);
				}catch(Exception e) {
					e.printStackTrace();
				}
				timestamp = timeGen();//再次获取时间戳
			}
			if (timestamp < lastTimestamp) {//如果仍然小于
				throw new RuntimeException(String.format(
						"Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
			}
		}else if (lastTimestamp == timestamp) {// 如果是同一毫秒生成的，则进行毫秒内序列
			sequence = (sequence + 1) & sequenceMax;
			// 毫秒内序列溢出
			if (sequence == 0) {
				// 阻塞到下一个毫秒,获得新的时间戳
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {// 时间戳改变，毫秒内序列重置
			sequence = 0L;
		}
		// 上次生成ID的时间截
		lastTimestamp = timestamp;
		// 移位并通过或运算拼到一起组成64位的ID
		return ((timestamp - start) << 22) | (serverId << 14) | sequence;
	}

	/**
	 * 阻塞到下一个毫秒，直到获得新的时间戳
	 * 
	 * @param lastTimestamp
	 *            上次生成ID的时间截
	 * @return 当前时间戳
	 */
	protected long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	/**
	 * 返回以毫秒为单位的当前时间
	 * 
	 * @return 当前时间(毫秒)
	 */
	protected long timeGen() {
		return System.currentTimeMillis();
	}
	
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
		int threadCount = 1000;//单机1万并发没有问题
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
					for(int i=0;i<200;i++){
						String num = "" + getId(1);
						map.put(num, num);
					}
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
}
