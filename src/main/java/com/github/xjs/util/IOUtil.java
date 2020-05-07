/**
 * 
 */
package com.github.xjs.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.github.xjs.util.filewriter.FileWriter;

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

	public static String readInputStream(InputStream in, String charset) throws IOException{
		return new String(readInputStream(in), charset);
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

	/***
	 * 获取文件的行数
	 * */
	public static int getFileRowCount(File file){
		if(file == null || !file.exists()){
			return 0;
		}
		try{
			FileReader fileReader = new FileReader(file);
			LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
			lineNumberReader.skip(Long.MAX_VALUE);
			int lines = lineNumberReader.getLineNumber();
			IOUtil.closeQuietly(fileReader, lineNumberReader);
			return lines;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	/***
	 * 把一个文件按照行数拆分多个子文件
	 * */
	public static synchronized File[] splitFile(File src, final int maxRowCount, final FileWriter.ElementProcessor rowProcessor){
		File[] subFiles = null;
		BufferedReader br = null;
		InputStream in = null;
		try{
			String srcFileName = src.getName();
			//先删除老的文件
			File[] oldFiles = src.getParentFile().listFiles();
			for(File oldFile : oldFiles){
				String oldFileName = oldFile.getName();
				if(oldFileName.indexOf(srcFileName) >= 0){
					int underline = oldFileName.lastIndexOf("_");
					if(underline <= 0){
						continue;
					}
					String front = oldFileName.substring(0, underline);
					if(srcFileName.equals(front)){
						oldFile.delete();
					}
				}
			}
			//重新生成新的
			List<FileWriter> fileWriters = new ArrayList<FileWriter>();
			in = new FileInputStream(src);
			br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = null;
			int subFileRowCnt = 0;
			int subFileIdx = 0;
			while((line = br.readLine()) != null){
				subFileRowCnt++;
				FileWriter fileWriter = null;
				File subFile = new File(src.getParent(), srcFileName+"_"+subFileIdx);
				if(!subFile.exists()){
					if(fileWriters.size()>0){
						FileWriter lastFileWriter = fileWriters.get(fileWriters.size()-1);
						if(lastFileWriter != null){
							lastFileWriter.stop();
						}
					}
					createNewSubFile(subFile);
					fileWriter = createFileWriter(subFile, rowProcessor);
					fileWriters.add(fileWriter);
				}else{
					fileWriter = fileWriters.get(fileWriters.size()-1);
				}
				fileWriter.write(line);
				if(subFileRowCnt >= maxRowCount){
					subFileIdx ++;
					subFileRowCnt = 0;
				}
			}
			fileWriters.get(fileWriters.size()-1).stop();
			subFiles = new File[fileWriters.size()];
			for(int i=0; i<subFiles.length; i++){
				FileWriter fileWriter = fileWriters.get(i);
				subFiles[i] = fileWriter.getFile();
			}
			return subFiles;
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally {
			IOUtil.closeQuietly(in, br);
		}
	}

	private static void createNewSubFile(File subFile) {
		if(!subFile.exists()){
			try{
				subFile.createNewFile();
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
	}

	private static FileWriter createFileWriter(File subFile, FileWriter.ElementProcessor processor) {
		FileWriter fileWriter = new FileWriter(subFile, false, processor);
		return fileWriter;
	}

	public static void main(String[] args) {
		File[] files = splitFile(new File("d:\\log.txt"), 5, null);
		for(File file : files){
			System.out.println(file.getAbsolutePath());
		}
		System.out.println("over");
	}

}
