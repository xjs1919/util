/**
 * 
 */
package com.github.xjs.util;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月15日 上午10:36:35
 */
public class MeasureUtil {
	
	public static long parseSize(String size) {
        if(size == null || size.length() <= 0){
            return -1;
        }
        size = size.toUpperCase();
        if (size.endsWith("KB")) {
            return Long.valueOf(size.substring(0, size.length() - 2)) * 1024;
        }else if (size.endsWith("K")) {
            return Long.valueOf(size.substring(0, size.length() - 1)) * 1024;
        }

        if (size.endsWith("MB")) {
            return Long.valueOf(size.substring(0, size.length() - 2)) * 1024 * 1024;
        }else if(size.endsWith("M")){
            return Long.valueOf(size.substring(0, size.length() - 1)) * 1024 * 1024;
        }

        return Long.valueOf(size);
    }

    public static String formatSize(long size) {
        if(size <= 0){
            return "0B";
        }
        long kb = size/1024;
        long mb = kb/1024;
        if(mb > 0){
            return mb+"MB";
        }
        if(kb > 0){
            return kb + "KB";
        }
        return size+"B";
    }
}
