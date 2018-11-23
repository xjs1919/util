package com.github.xjs.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ZipUtil {

	public static byte[] zip(MemFile... memFiles) {
		if (memFiles == null || memFiles.length <= 0) {
			return null;
		}
		String parentPath = "";
		ZipOutputStream zos = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			zos = new ZipOutputStream(out);
			for (MemFile memFile : memFiles) {
				if(memFile == null) {
					continue;
				}
				String fileName = memFile.getFilename();
				byte[] contents = memFile.getContent();
				compressBytes(zos, parentPath, fileName, contents);
			}
			IOUtil.closeQuietly(zos);
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("压缩过程出现异常",e);
		} finally {
			IOUtil.closeQuietly(zos);
		}
	}

	/**
	 * @param srcFile 可以是一个文件或者目录
	 */
	public static byte[] zip(File srcFile) {
		// 默认的相对地址，为根路径
		String parentPath = "";
		ZipOutputStream zos = null;
		try {
			// 创建一个Zip输出流
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			zos = new ZipOutputStream(out);
			startCompress(zos, parentPath, srcFile);
			IOUtil.closeQuietly(zos);
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("压缩过程出现异常",e);
		} finally {
			IOUtil.closeQuietly(zos);
		}
	}

	/**
	 * @param srcFile 可以是一个文件或者目录
	 * @param destFile 压缩出来的文件
	 */
	public static boolean zip(File srcFile, File destFile) {
		byte[] bytes = zip(srcFile);
		try {
			FileOutputStream fout = new FileOutputStream(destFile);
			fout.write(bytes);
			fout.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void startCompress(ZipOutputStream zos, String parentPath, File srcFile) throws Exception {
		if (srcFile.isDirectory()) {
			// 如果是压缩目录
			File[] files = srcFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				File aFile = files[i];
				if (aFile.isDirectory()) {
					// 如果是目录，修改相对地址
					String newOppositePath = parentPath + aFile.getName() + "/";
					// 创建目录
					compressDirectory(zos, parentPath, aFile);
					// 进行递归调用
					startCompress(zos, newOppositePath, aFile);
				} else {
					// 如果不是目录，则进行压缩
					compressFile(zos, parentPath, aFile);
				}
			}
		} else {
			// 如果是压缩文件，直接调用压缩方法进行压缩
			compressFile(zos, parentPath, srcFile);
		}
	}

	private static void compressFile(ZipOutputStream zos, String parentPath, File file) throws Exception {
		byte[] bytes = IOUtil.readInputStream(new FileInputStream(file));
		compressBytes(zos, parentPath, file.getName(), bytes);
	}

	private static void compressDirectory(ZipOutputStream zos, String parentPath, File file) throws Exception {
		// 压缩目录，这是关键，创建一个目录的条目时，需要在目录名后面加多一个"/"
		ZipEntry entry = new ZipEntry(parentPath + file.getName() + "/");
		zos.putNextEntry(entry);
		zos.closeEntry();
	}

	private static void compressBytes(ZipOutputStream zos, String parentPath, String fileName, byte[] content)
			throws Exception {
		// 创建一个Zip条目，每个Zip条目都是必须相对于根路径
		ZipEntry entry = new ZipEntry(parentPath + fileName);
		// 将条目保存到Zip压缩文件当中
		zos.putNextEntry(entry);
		zos.write(content);
		zos.closeEntry();
	}

	public static class MemFile {

		private String filename;
		private byte[] content;

		public MemFile() {
			super();
		}

		public MemFile(String filename, byte[] content) {
			super();
			this.filename = filename;
			this.content = content;
		}

		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}

		public byte[] getContent() {
			return content;
		}

		public void setContent(byte[] content) {
			this.content = content;
		}

	}

}