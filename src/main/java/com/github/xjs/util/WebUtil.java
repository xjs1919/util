/**
 * 
 */
package com.github.xjs.util;

import java.io.ByteArrayOutputStream;
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
			int len = 0;
			byte[] buff = new byte[1024];
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			while((len = in.read(buff)) != -1){
				bout.write(buff, 0 ,len);
			}
			IOUtil.closeQuietly(bout);
			return new String(bout.toByteArray(),"UTF-8");
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
