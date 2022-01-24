/**
 * 
 */
package com.github.xjs.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 605162215@qq.com
 *
 *         2016年8月12日 下午5:25:52
 */
public class StringUtil {

	/**
	 * 去掉首尾的空格，包括全角空格
	 * */
	public static String trimSpace(String src){
		if(src == null) {
			return null;
		}
		return src.replaceAll("^[　\\s]+", "").replaceAll("[　\\s]+$", "");
	}
	
	public static String save2(double d) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(d);
	}
	
	public static boolean isEmpty(String src) {
		if (src == null || src.trim().length() <= 0) {
			return true;
		}
		return false;
	}

	public static boolean isChinese(String value) {
		boolean result = false;
		String reg = "[^u4E00-u9FA5]+";
		Pattern pattern = Pattern.compile(reg, 34);
		Matcher matcher = pattern.matcher(value);

		if (matcher.find()) {
			result = true;
		}

		return result;
	}
	
	public static String toString(byte[] data){
		try{
			return toString(data, "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String toString(byte[] data, String charset){
		try{
			return new String(data, charset);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] toBytes(String src){
		return toBytes(src, "UTF-8");
	}

	public static byte[] toBytes(String src, String encoding){
		try{
			return src.getBytes(encoding);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String urlEncode(String url){
		try{
			return URLEncoder.encode(url, "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
			return url;
		}
	}
	
	public static String urlDecode(String url){
		try{
			return URLDecoder.decode(url, "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
			return url;
		}
	}

	public static String formalizeClassName(String src){
		if(src == null || src.length() <= 0){
			return null;
		}
		src = src.substring(0).toUpperCase();
		if(src.length() <= 1){
			return src;
		}else{
			src = src.replaceAll("[_-]","_");
			return underlineToCamel(src);
		}
	}

	public static String underlineToCamel(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (c == '_') {
				if (++i < len) {
					sb.append(Character.toUpperCase(param.charAt(i)));
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String camelToUnderline(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append("_");
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	public static List<String> split(String src, String sep){
		Predicate<String> predicate = (fieldValue)->{return fieldValue!=null && !"".equals(fieldValue);};
		return split(src, sep, predicate);
	}

	public static List<String> split(String src, String sep, Predicate<String> predicate){
		if( src == null || src.length() <= 0 ||
			sep == null || sep.length() <= 0 ||
			predicate == null){
			return null;
		}
		String arr[] = src.split(sep);
		List<String> list = new ArrayList<String>(arr.length);
		for(String str : arr){
			if(predicate.test(str)){
				list.add(str);
			}
		}
		return list;
	}
	
	public static String formatTime(int seconds) {
		int hour = seconds/3600;
		int secondLeft = seconds - seconds/3600*3600;
		int minute = secondLeft/60;
		secondLeft = secondLeft - secondLeft/60*60;
		int second = secondLeft;
		StringBuilder sb = new StringBuilder();
		if(hour > 0){
			if(hour > 9){
				sb.append(hour);
			}else{
				sb.append("0").append(hour);
			}
			sb.append(":");
		}else{
			sb.append("00:");
		}
		if(minute>9){
			sb.append(minute);
		}else{
			sb.append("0").append(minute);
		}
		sb.append(":");
		if(second>9){
			sb.append(second);
		}else{
			sb.append("0").append(second);
		}
		return sb.toString();
	}

	/***
	 * 参考： https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Origin
	 * */
	public static String normalizeOrigin(String origin){
		if(StringUtil.isEmpty(origin)){
			return "";
		}
		String old = origin;
		origin = origin.toLowerCase();
		if(!origin.startsWith("http://") && !origin.startsWith("https://")){
			return "";
		}
		if(origin.indexOf("?") >= 0 || origin.indexOf("&") >= 0 ||
				origin.indexOf("'")>=0 || origin.indexOf("\"") >= 0 ||
				origin.indexOf("(")>=0 || origin.indexOf(")") >= 0 ||
				origin.indexOf("\r")>=0 || origin.indexOf("\n") >= 0){
			return "";
		}
		return old;
	}

	public static boolean equalsNullToEmpty(String s1, String s2){
		if(s1 == null){
			s1 = "";
		}
		if(s2 == null){
			s2 = "";
		}
		return s1.equals(s2);
	}
}
