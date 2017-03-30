/**
 * 
 */
package com.github.xjs.util;

import java.util.ArrayList;
import java.util.List;
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
private IDUtil(){}
	
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
	private static final int MAX_ID = 2 << 7 - 1;
	/**
	 * 随机
	 */
	private static final Random RND = new Random();

	/**
	 * 表名
	 * */
	public static enum Table{
		User(1),
		Role(2),
		Menu(3),
		;
		private int id;
		private Table(int id){
			this.id = id;
		}
		public int getId(){
			return this.id;
		}
	}
	
	/**
	 * 通过这个方法向外暴漏
	 * */
	public static long getId(Table table, int server) {
		return getId(table.getId(), server);
	}
	
	private static long getId(int table, int server) {
		int sn = getSN();
		long id = System.currentTimeMillis() - BIRTHDAY_OF_CATHY;// 毫秒数,30年
		id = id << 2;               // 预留
		id = id << 7 | server;      // 机器号               127
		id = id << 7 | table;       // 业务线              127
		id = id << 7 | sn;          // 毫秒内的并发   127
		//40 + 2 + 7 + 7 + 7 = 63，     最高位留出来，否则就是负数了
		return id;
	}

	private static int getSN() {
		int num = UNIQUE_ID.incrementAndGet();
		if (num >= MAX_ID) {//单线程一个毫秒可能会生成超过127个，时间部分增加下，保证严格递增
			if(UNIQUE_ID.compareAndSet(MAX_ID, 1)){
				try{
					Thread.sleep(1);
				}catch(Exception e){
					e.printStackTrace();
				}
				return num;
			}else{
				UNIQUE_ID.set(1);
				try{
					Thread.sleep(1);
				}catch(Exception e){
					e.printStackTrace();
				}
				return getSN();
			}
		}
		return num;
	}

	public static void main(String[] args) throws Exception {
		//1.多线程测试
//		long start = System.currentTimeMillis();
//		final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
//		int threadCount = 10000;//单机1万并发没有问题
//		final CountDownLatch endLatch = new CountDownLatch(threadCount);
//		final CountDownLatch startLatch = new CountDownLatch(1);
//		Thread[] arr = new Thread[threadCount];
//		for (int i = 0; i < threadCount; i++) {
//			arr[i] = new Thread(new Runnable() {
//				public void run() {
//					try {
//						startLatch.await();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					String num = "" + getId(1, 1);
//					// System.out.println(Thread.currentThread().getName()+":"+num);
//					map.put(num, num);
//					endLatch.countDown();
//				}
//			});
//		}
//		for (int i = 0; i < threadCount; i++) {
//			arr[i].start();
//		}
//		startLatch.countDown();
//		endLatch.await();
//		System.out.println(map.size());
//		long end = System.currentTimeMillis();
//		System.out.println("use:" + (end - start));
		//2.单线程测试
		List<Long> list = new ArrayList<Long>();
		for(int i=0;i<5000;i++){
			long id = getId(1, 1);
			list.add(id);
		}
		for(int i=0;i<list.size()-1;i++){
			long front = list.get(i);
			long back = list.get(i+1);
			if(front > back){
				System.out.println(front+","+back);
			}
		}
		System.out.println("over");
	}

}
