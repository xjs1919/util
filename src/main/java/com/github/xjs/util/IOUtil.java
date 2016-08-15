/**
 * 
 */
package com.github.xjs.util;

import java.io.Closeable;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月12日 下午5:30:52
 */
public class IOUtil {
	
	public static void close(Closeable... closeables){
		if(closeables == null || closeables.length <= 0){
			return;
		}
		for(Closeable closeable : closeables){
			if(closeable != null){
				try{
					closeable.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
}
