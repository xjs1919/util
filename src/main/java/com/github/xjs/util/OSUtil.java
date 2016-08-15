/**
 * 
 */
package com.github.xjs.util;

/**
 * @author 605162215@qq.com
 *
 *         2016年8月15日 上午10:07:33
 */
public class OSUtil {
	
	private static final String WINDOWS = "WINDOWS";
	private static final double FREE_TOTALL_PERCENT = 0.05;

	/**
	 * 判断当前操作系统的类型
	 *
	 * @return false means window system ,true means unix like system
	 */
	public static boolean isUnixLikeSystem() {
		String name = System.getProperty("os.name");
		if (name != null) {
			if (name.toUpperCase().indexOf(WINDOWS) == -1) { // it means it's unix like system
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取当前JVM可用内存大小
	 *
	 * @return
	 */
	public static long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	/**
	 * 检测JVN当前是否会内存泄露
	 * 
	 * @return
	 */
	public static boolean checkJVMHealth() {
		double freeMem = Runtime.getRuntime().freeMemory();
		double totlMem = Runtime.getRuntime().totalMemory();
		double percent = freeMem / totlMem;
		if (percent <= FREE_TOTALL_PERCENT) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println("total size is :" + Runtime.getRuntime().totalMemory());
		System.out.println("max mem (meauser in bytes)" + Runtime.getRuntime().maxMemory());
		System.out.println("current mem size is : " + getFreeMemory());
	}
}
