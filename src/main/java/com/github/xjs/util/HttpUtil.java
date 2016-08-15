/**
 * 
 */
package com.github.xjs.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月15日 上午10:51:36
 */
public class HttpUtil {
	
	public static final int TIME_OUT = 10000;
	
	public static <T> HttpResponse<T> get(String urlstr, Class<T> clazz){
		return get(urlstr,"application/x-www-form-urlencoded", clazz);
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
			conn.setRequestProperty("Content-Type", contentType);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0");
			HttpResponse.setStatusCode(conn.getResponseCode());
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
		return post(urlstr, parameterData, "application/x-www-form-urlencoded;charset=UTF-8", clazz);
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
	        conn.setRequestProperty("Content-Type", contentType);
	        conn.setRequestProperty("Content-Length", String.valueOf(parameterData.length()));

            out = conn.getOutputStream();
            writer = new OutputStreamWriter(out, "UTF-8");
            
            writer.write(parameterData.toString());
            writer.flush();
            
            HttpResponse.setStatusCode(conn.getResponseCode());
            
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
	
	public static HttpResponse<String> get302Location(String urlstr){
		HttpResponse<String> HttpResponse = new HttpResponse<String>();
		try{
			URL localURL = new URL(urlstr);
			HttpURLConnection conn = (HttpURLConnection) localURL.openConnection();
			conn.setConnectTimeout(TIME_OUT);
			conn.setReadTimeout(TIME_OUT);
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setInstanceFollowRedirects(false);
			if(conn.getResponseCode() == 302){
				String location = conn.getHeaderField("location");
				HttpResponse.setStatusCode(302);
				HttpResponse.setContent(location);
			}else{
				HttpResponse.setStatusCode(conn.getResponseCode());
			}
		}catch(Exception e){
			return errorResponse(e);
		}
		return HttpResponse;
	}
	
	
	public static HttpResponse<String> post302Location(String urlstr, String parameterData){
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
	        conn.setRequestProperty("Content-Length", String.valueOf(parameterData.length()));
	        conn.setInstanceFollowRedirects(false);
       
            out = conn.getOutputStream();
            writer = new OutputStreamWriter(out, "UTF-8");
            
            writer.write(parameterData.toString());
            writer.flush();
            
            if (conn.getResponseCode() == 302) {
            	String location = conn.getHeaderField("location");
				HttpResponse.setStatusCode(302);
				HttpResponse.setContent(location);
            }else{
            	HttpResponse.setStatusCode(conn.getResponseCode());
            }
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
	
	public static void main(String[] args) {
		HttpResponse<String> res = HttpUtil.get("http://www.baidu.com/", String.class);
		System.out.println(res.getStatusCode()+","+res.getContent());
	}
}
