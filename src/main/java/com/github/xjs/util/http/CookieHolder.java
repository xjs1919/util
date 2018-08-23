package com.github.xjs.util.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.xjs.util.StringUtil;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年1月16日 下午2:21:43<br/>
 */
public class CookieHolder {
	
	private static Logger log = LoggerFactory.getLogger(CookieHolder.class);
	
	private static ThreadLocal<HashMap<String, String>> holder = new ThreadLocal<HashMap<String, String>>(){
		@Override
		protected HashMap<String, String> initialValue() {
			return new HashMap<String, String>();
		}
	};
	
	public static void setCookies(String cookieValues) {
		if(cookieValues == null || cookieValues.length() <= 0) {
			return;
		}
		String arr[] = cookieValues.split(";");
		for(String kv : arr) {
			String kvs[] = kv.split("=");
			holder.get().put(StringUtil.trimSpace(kvs[0]), StringUtil.trimSpace(kvs[1]));
		}
	}
	public static void delCookie(String key) {
		if(key == null) {
			return;
		}
		holder.get().remove(key);
	}
	public static void setCookie(String key, String value) {
		if(key == null || value == null) {
			return;
		}
		holder.get().put(key, value);
	}
	
	public static String getCookie(String key) {
		if(key == null) {
			return null;
		}
		return holder.get().get(key);
	}
	
	public static String getAllCookies() {
		HashMap<String, String> cookieMap = holder.get();
		if(cookieMap == null || cookieMap.size() <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, String> entry :cookieMap.entrySet() ) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key).append("=").append(value).append("; ");
		}
		if(sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	
	public static void clearAll() {
		HashMap<String, String> cookieMap = holder.get();
		if(cookieMap != null && cookieMap.size() > 0) {
			cookieMap.clear();
		}
	}
	
	public static void parseResponseCookies(Map<String,List<String>> resposneHeaders) {
		if(resposneHeaders == null || resposneHeaders.size() <= 0) {
			return;
		}
		List<String> cookies = resposneHeaders.get("Set-Cookie");
		if(cookies == null || cookies.size() <= 0) {
			 cookies = resposneHeaders.get("set-cookie");
			 if(cookies == null || cookies.size() <= 0) {
				 return;
			 }
		}
		for(String cookie : cookies) {
			String parts[] = cookie.split(";");
			String part = parts[0];
			String arr[] = new String[2];
			int index=part.indexOf("=");
			arr[0]=part.substring(0, index);
			arr[1]=part.substring(index+1);
			setCookie(arr[0], arr[1]);
			log.info("获取服务端Cookie: name:" +arr[0]+",value:"+arr[1]);
		}
	}
	
}
