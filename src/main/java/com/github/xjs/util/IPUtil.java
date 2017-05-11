package com.github.xjs.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPUtil {
	
	private static final Logger log = LoggerFactory.getLogger(IPUtil.class);
	
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
