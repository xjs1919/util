/**
 * 
 */
package com.github.xjs.util;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月15日 上午10:31:44
 */
public class WebUtil {
	
	public static String getFullUrl(final HttpServletRequest request) {
		String reqUrl = request.getRequestURL().toString();
		String query = request.getQueryString();
		String url = reqUrl;
		if (!StringUtil.isEmpty(query)) {
			url = url + "?" + query;
		}
		return url;
	}

	public static String getRequestBody(final HttpServletRequest request){
		InputStream in = null;
		try{
			in = request.getInputStream();
			byte[] data = IOUtil.readInputStream(in);
			return new String(data,"UTF-8");
		}catch(Exception e){
			e.printStackTrace();
			IOUtil.closeQuietly(in);
			return "";
		}
	}
	
	public static String getIpAddr(final HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if(ip != null && ip.length() > 0){
			ip = ip.split(",")[0];
		}
		return ip;
	}
}
