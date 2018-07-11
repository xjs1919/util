package com.github.xjs.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年7月5日 上午10:55:06<br/>
 */
public class HttpUtil {
public static final int TIME_OUT = 10000;
	
	public static <T> HttpResponse<T> get(String urlstr, Class<T> clazz){
		return get(urlstr,null, clazz);
	}
	
	public static <T> HttpResponse<T> getJson(String urlstr, Class<T> clazz){
		return get(urlstr,"application/json", clazz);
	}
	
	public static <T> HttpResponse<T> get(String urlstr, String contentType, Class<T> clazz) {
		InputStream inputStream = null;
		HttpResponse<T> HttpResponse = new HttpResponse<T>();
		try{
			URL localURL = new URL(urlstr);
			HttpURLConnection conn = (HttpURLConnection) localURL.openConnection();
			conn.setConnectTimeout(TIME_OUT);
			conn.setReadTimeout(TIME_OUT);
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("Connection","Keep-Alive");
			if(contentType!=null) {
				conn.setRequestProperty("Content-Type", contentType);
			}
			conn.setRequestProperty("Cookie", CookieHolder.getAllCookies());
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0");
			HttpResponse.setStatusCode(conn.getResponseCode());
			CookieHolder.parseResponseCookies(conn.getHeaderFields());
			if (conn.getResponseCode() == 200) {
				inputStream = conn.getInputStream();
				byte[] res = IOUtil.readInputStream(inputStream);
				T t = convert(res, clazz);
				HttpResponse.setContent(t);
			}else if(conn.getResponseCode() == 302){
				String location = conn.getHeaderField("location");
				if(!StringUtil.isEmpty(location)){
					return get(location, contentType, clazz);
				}
			}
		}catch(Exception e){
			return errorResponse(e);
		}finally{
			IOUtil.closeQuietly(inputStream);
		}
		return HttpResponse;
	}
	
	public static <T> HttpResponse<T> post(String urlstr, String parameterData,Class<T> clazz) {
		return post(urlstr, parameterData, null, clazz);
    }
	
	public static <T> HttpResponse<T> postJson(String urlstr, String parameterData,Class<T> clazz){
		return post(urlstr, parameterData, "application/json;charset=UTF-8", clazz);
    }
	
	public static <T> HttpResponse<T> post(String urlstr, String parameterData, String contentType, Class<T> clazz) {
		InputStream in = null;
        OutputStream out = null;
        OutputStreamWriter writer = null;
        HttpResponse<T> HttpResponse = new HttpResponse<T>();
		try{
			URL localURL = new URL(urlstr);
	        HttpURLConnection conn = (HttpURLConnection)localURL.openConnection();
	        conn.setConnectTimeout(TIME_OUT);
			conn.setReadTimeout(TIME_OUT);
	        conn.setDoOutput(true);
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Accept-Charset", "UTF-8");
	        conn.setRequestProperty("Connection","Keep-Alive");
	        if(contentType!=null&&contentType.length() > 0) {
	        	conn.setRequestProperty("Content-Type", contentType);
	        }
	        conn.setRequestProperty("Content-Length", parameterData==null?"0": String.valueOf(parameterData.length()));
	        conn.setRequestProperty("Cookie", CookieHolder.getAllCookies());
	        
            out = conn.getOutputStream();
            writer = new OutputStreamWriter(out, "UTF-8");
            
            if(parameterData != null) {
            	  writer.write(parameterData.toString());
                  writer.flush();
            }
          
            HttpResponse.setStatusCode(conn.getResponseCode());
            CookieHolder.parseResponseCookies(conn.getHeaderFields());
            
            if (conn.getResponseCode() == 200) {
            	in = conn.getInputStream();
                byte[] res = IOUtil.readInputStream(in);
        		T t = convert(res, clazz);
        		HttpResponse.setContent(t);
            }
        }catch(Exception e){
        	return errorResponse(e);
        } finally {
        	IOUtil.closeQuietly(writer,out, in);
        }
		return HttpResponse;
	}
	
	public static HttpResponse<String> get302Location(String urlstr,Map<String, String> headers){
		HttpResponse<String> HttpResponse = new HttpResponse<String>();
		try{
			URL localURL = new URL(urlstr);
			HttpURLConnection conn = (HttpURLConnection) localURL.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(TIME_OUT);
			conn.setReadTimeout(TIME_OUT);
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("Connection","Keep-Alive");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko");
			conn.setRequestProperty("Cookie", CookieHolder.getAllCookies());
			if(headers != null && headers.size() > 0) {
	        	for(Map.Entry<String, String> entry : headers.entrySet()) {
	        		conn.setRequestProperty(entry.getKey(), entry.getValue());
	        	}
	        }
			conn.setInstanceFollowRedirects(false);
			if(conn.getResponseCode() == 302){
				String location = conn.getHeaderField("location");
				HttpResponse.setStatusCode(302);
				HttpResponse.setContent(location);
			}else{
				HttpResponse.setStatusCode(conn.getResponseCode());
			}
			CookieHolder.parseResponseCookies(conn.getHeaderFields());
		}catch(Exception e){
			return errorResponse(e);
		}
		return HttpResponse;
	}
	
	
	public static HttpResponse<String> post302Location(String urlstr, String parameterData, Map<String, String> headers){
		InputStream in = null;
        OutputStream out = null;
        OutputStreamWriter writer = null;
        HttpResponse<String> HttpResponse = new HttpResponse<String>();
		try{
			URL localURL = new URL(urlstr);
	        HttpURLConnection conn = (HttpURLConnection)localURL.openConnection();
	        conn.setConnectTimeout(TIME_OUT);
			conn.setReadTimeout(TIME_OUT);
	        conn.setDoOutput(true);
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Accept-Charset", "utf-8");
	        conn.setRequestProperty("Connection","Keep-Alive");
	        conn.setRequestProperty("Content-Length", parameterData==null?"0": String.valueOf(parameterData.length()));
	        conn.setRequestProperty("Cookie", CookieHolder.getAllCookies());
	        if(headers != null && headers.size() > 0) {
	        	for(Map.Entry<String, String> entry : headers.entrySet()) {
	        		conn.setRequestProperty(entry.getKey(), entry.getValue());
	        	}
	        }
	        conn.setInstanceFollowRedirects(false);
            out = conn.getOutputStream();
            writer = new OutputStreamWriter(out, "UTF-8");
            
            if(parameterData != null) {
            	 writer.write(parameterData.toString()); 
                 writer.flush();
            }
            
            if (conn.getResponseCode() == 302) {
            	String location = conn.getHeaderField("location");
				HttpResponse.setStatusCode(302);
				HttpResponse.setContent(location);
            }else{
            	HttpResponse.setStatusCode(conn.getResponseCode());
            }
            CookieHolder.parseResponseCookies(conn.getHeaderFields());
            
        } catch(Exception e){
        	return errorResponse(e);
        } finally {
        	IOUtil.closeQuietly(writer, out, in);
        }
		return HttpResponse;
	}
	
	private static <T> HttpResponse<T> errorResponse(Exception e){
		HttpResponse<T> HttpResponse = new HttpResponse<T>();
		e.printStackTrace();
		HttpResponse.setStatusCode(500);
		HttpResponse.setErrMsg(ExceptionUtil.getStackTrace(e));
		return HttpResponse;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T convert(byte[] res, Class<T> clazz) {
		if(clazz == null){
			return null;
		}
		if(clazz == String.class){
			return (T)StringUtil.toString(res);
		}else if(clazz == byte[].class){
			return (T)res;
		}else if(clazz == JSONObject.class){
			return (T)JSON.parseObject(StringUtil.toString(res));
		}else{
			return JSON.toJavaObject(JSON.parseObject(StringUtil.toString(res)), clazz);
		}
	}
	
	public static class HttpResponse<T>{
		private int statusCode;
		private String errMsg;
		private T content;
		public int getStatusCode() {
			return statusCode;
		}
		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}
		public String getErrMsg() {
			return errMsg;
		}
		public void setErrMsg(String errMsg) {
			this.errMsg = errMsg;
		}
		public T getContent() {
			return content;
		}
		public void setContent(T content) {
			this.content = content;
		}
	}
}
