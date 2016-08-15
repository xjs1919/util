/**
 * 
 */
package com.github.xjs.util;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月15日 上午10:38:15
 */
public class SerialNumberUtil {
	/**
	 * Cathy's birthday
	 * */
	private static final long BIRTHDAY_OF_CATHY = 1441313880000L;
	private static final int SERVER_ID = getServerId();
	private static int SERIALNUMBER = 0;
	private static Random RND = new Random();
	
	/**
	 * 生成主键， 1秒最多1000个
	 */
	public static synchronized String getSN(String head){
		if(SERIALNUMBER == 9) {
			SERIALNUMBER = 1;
		}
		int uid = ++SERIALNUMBER;
		int rnd = RND.nextInt(10);
		try{Thread.sleep(1);}catch(Exception e){}
		long datastr = System.currentTimeMillis() - BIRTHDAY_OF_CATHY;
		if(head == null){
			head = "";
		}
		return head+ datastr + SERVER_ID + uid + rnd;
	}
	
	/**
	 * 生成主键， 1秒最多1000个
	 */
	public static synchronized long getSN(){
		String pk = getSN("");
		return Long.parseLong(pk);
	}

	/**
	 * 集群一般是同一个网段
	 * */
	private static int getServerId(){
		try{
			InetAddress inet = InetAddress.getLocalHost();
			String localIp = inet.getHostAddress();
			int lastDot = localIp.lastIndexOf(".");
			return Integer.parseInt(localIp.substring(lastDot+1).trim());
		}catch(Exception e){
			e.printStackTrace();
			return RND.nextInt(255);
		}
	}

	public static void main(String[] args) throws Exception{
		final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
		int length = 1000;
		final CountDownLatch latch = new CountDownLatch(length);
		Thread[] arr = new Thread[length];
		for(int i=0;i<length;i++){
			arr[i] = new Thread(new Runnable(){
				public void run(){
					HashSet<String> subset = new HashSet<String>();
					for(int j=0; j<10; j++){
						String num = ""+getSN();
						map.put(num, num);
						subset.add(num);
					}
					System.out.println(Thread.currentThread().getName()+" over!size:"+subset.size()+","+subset.toString());
					latch.countDown();
				}
			});
		}
		for(int i=0;i<length;i++){
			arr[i].start();
		}
		latch.await();
		System.out.println("size:"+ map.size());	
	}
}
