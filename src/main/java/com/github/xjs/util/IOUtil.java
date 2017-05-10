/**
 * 
 */
package com.github.xjs.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

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
	
	public static List<String> readInputStreamByLine(InputStream in) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line = "";
		List<String> ret = new ArrayList<String>();
		while((line = br.readLine()) != null){
			ret.add(line);
		}
		closeQuietly(in, br);
		return ret;
	}
	
	public static void saveInputStream(InputStream in, OutputStream out) throws IOException{
		byte[] buff = new byte[1024 * 10];
		int len = 0;
		while((len = in.read(buff)) >= 0){
			out.write(buff, 0, len);
		}
	}
	
	public static void saveInputStream(Object obj, OutputStream out) throws IOException{
		if(obj == null){
			return;
		}
		String str = "";
		if(!(obj instanceof String)){
			str = JSON.toJSONString(obj);
		}else{
			str = (String)obj;
		}
		byte[] bytes = str.getBytes("UTF-8");
		out.write(bytes);
		out.flush();
	}
	
	public static void saveFile(Object obj, File file) throws IOException{
		if(obj == null || file == null){
			return;
		}
		String str = "";
		if(!(obj instanceof String)){
			str = JSON.toJSONString(obj);
		}else{
			str = (String)obj;
		}
		byte[] bytes = str.getBytes("UTF-8");
		OutputStream out = new FileOutputStream(file);
		out.write(bytes);
		out.flush();
		out.close();
	}
	
	public static void appendFile(Object obj, File file) throws IOException{
		if(obj == null || file == null){
			return;
		}
		if(!file.exists()){
			file.createNewFile();
		}
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		raf.seek(raf.length());
		String str = "";
		if(!(obj instanceof String)){
			str = JSON.toJSONString(obj);
		}else{
			str = (String)obj;
		}
		byte[] bytes = str.getBytes("UTF-8");
		raf.write(bytes, 0, bytes.length);
		raf.write("\r\n".getBytes());
		raf.close();
	}
	
	public static void log(Object obj) {
		try{
			appendFile(obj, new File("/tmp/log.txt"));
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
}
