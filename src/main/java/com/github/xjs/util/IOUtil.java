/**
 * 
 */
package com.github.xjs.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月12日 下午5:30:52
 */
public class IOUtil {
	
	public static void closeQuietly(Closeable... closeables){
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
	
	public static byte[] readInputStream(InputStream in) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int len = 0;
		byte[] buff = new byte[10*1024];
		while((len = in.read(buff)) != -1){
			out.write(buff, 0 ,len);
		}
		out.close();
		return out.toByteArray();
	}
	
}
