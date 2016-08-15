/**
 * 
 */
package com.chrhc.xjs.util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月12日 下午5:25:52
 */
public class StringUtil {
	
	private static SecureRandom random = new SecureRandom(); 
	
	public static boolean isEmpty(String src){
		if(src == null || src.trim().length() <= 0){
			return true;
		}
		return false;
	}
	
	public static String randomString(int length){
		BigInteger bi = new BigInteger(130, random);  
        String str = bi.toString(32);  
        return str.substring(0, length);  
	}
}
