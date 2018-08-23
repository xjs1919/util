/**
 * 
 */
package com.github.xjs.util;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.github.xjs.util.result.ICodeMsg;
import com.github.xjs.util.result.Result;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月15日 上午10:31:44
 */
public class WebUtil {
	
	private static final Logger log = LoggerFactory.getLogger(WebUtil.class);
	
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
	
	public static void renderJson(HttpServletResponse response, ICodeMsg codeMsg){
		if(codeMsg == null){
			return;
		}
		renderJson(response, Result.error(codeMsg));
	}
	
	public static<T> void renderJson(HttpServletResponse response, T t){
		renderJson(response, Result.success(t));
	}
	
	private static <T> void renderJson(HttpServletResponse response, Result<T> data){
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		try{
			OutputStream out = response.getOutputStream();
			out.write(JSON.toJSONString(data).getBytes("UTF-8"));
			out.flush();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static boolean isAjax(HttpServletRequest request){
		String xRequestWith = request.getHeader("X-Requested-With");
		if(xRequestWith != null && xRequestWith.indexOf("XMLHttpRequest") >= 0) {
			return true;
		}
		String accept = request.getHeader("Accept");
		if(accept != null && accept.indexOf("application/json") >= 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * nginx需要加配置：
	 * proxy_set_header Host $host;
     * proxy_set_header X-Real-IP $remote_addr;
     * proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
	 * 
	 * */
	public static String getRemoteIP(HttpServletRequest request) {
		 String formProxy = getFromProxy(request);
		 log.debug("[getRemoteIP]formProxy:"+formProxy);
		 if(!StringUtil.isEmpty(formProxy)){
            String[] ips = formProxy.split(",");  
            for (int index = 0; index < ips.length; index++) {  
                String ip = ips[index];  
                if (!StringUtil.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {  
                    return ip;
                }  
            }  
		 }
		 String fromRealIp = request.getHeader("X-Real-IP");
		 log.debug("[getRemoteIP]fromRealIp:"+fromRealIp);
		 if(!StringUtil.isEmpty(fromRealIp) && !"unknown".equalsIgnoreCase(fromRealIp)){
			 return fromRealIp;
		 }
		 log.debug("[getRemoteIP]getRemoteAddr:"+request.getRemoteAddr());
		 return request.getRemoteAddr();
	}
	
	private static String getFromProxy(HttpServletRequest request){
		String ip = request.getHeader("X-Forwarded-For");  
        if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
            if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("WL-Proxy-Client-IP");  
            }  
            if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("HTTP_CLIENT_IP");  
            }  
            if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
            }  
        } 
        return ip;  
	}
}
