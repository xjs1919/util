/**
 * 
 */
package com.github.xjs.util;

/**
 * @author 605162215@qq.com
 *
 * 2017年6月15日 下午5:17:00
 */
public class CharsetUtil {
	
	public static byte[] transferGBKToUTF8(byte[] src){
		try{
			String unicode = new String(src, "GBK");
			return unicode.getBytes("UTF-8");
		}catch(Exception e){
			e.printStackTrace();
			return src;
		}
	}
	
	public static byte[] transferUTF8ToGBK(byte[] src){
		try{
			//去掉bom头
			if(src[0]== -17  && src[1]== -69 && src[2]== -65){
				byte[] dst = new byte[src.length-3];
				System.arraycopy(src, 3, dst, 0, src.length-3);
				src = dst;
			}
			String unicode = new String(src, "UTF-8");
			return unicode.getBytes("GBK");
		}catch(Exception e){
			e.printStackTrace();
			return src;
		}
	}
	
	public static byte[] transferUTF16ToGBK(byte[] src){
		try{
			String str = new String(src, "utf-16");
			return str.getBytes("GBK");
		}catch(Exception e){
			e.printStackTrace();
			return src;
		}
	}
}
