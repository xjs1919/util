package com.github.xjs.util;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * @description
 *
 * @author xujs@mamcharge.com
 * @date 2019/10/17 15:50
 **/
public class HttpUtil {

    public static final int CONNECT_TIMEOUT = 5000;
    public static final int READ_TIMEOUT = 600000;

    public static String get(String urlstr){
        return get(urlstr, null);
    }

    public static String get(final String urlstr, Map<String, Object> params){
        return get(urlstr, params, null);
    }

    public static String get(final String urlstr, Map<String, Object> params, Map<String,Object> headers){
        String url = urlstr;
        try{
            url = appendParamsToUrl(urlstr, params);
            HttpURLConnection conn = getConnection(url, "get");
            //添加header
            if(headers != null && headers.size() > 0){
                for(Map.Entry<String, Object> header : headers.entrySet()){
                    conn.setRequestProperty(header.getKey(), header.getValue().toString());
                }
            }
            return parseResponse(urlstr, conn);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String post(String url){
        return post(url, null, null);
    }

    public static interface JsonConverter<T>{
        public String toJson(T t);
    }

    public static <T> String postJosn(String urlstr, T bean, JsonConverter<T> converter){
        String json = converter.toJson(bean);
        return postJosn(urlstr, json);
    }

    public static String postJosn(String urlstr, String json){
        Map<String, Object>headers = new HashMap<String, Object>();
        headers.put("Content-Type", "application/json;charset=utf-8");
        return post(urlstr, json, headers);
    }

    public static String post(String urlstr,  Map<String, Object> params){
        return post(urlstr, params, null);
    }

    public static String post(String urlstr, Object params, Map<String, Object>headers){
        OutputStream httpOut = null;
        try{
            HttpURLConnection conn = getConnection(urlstr, "post");
            //添加header
            if(headers != null && headers.size() > 0){
                for(Map.Entry<String, Object> header : headers.entrySet()){
                    conn.setRequestProperty(header.getKey(), header.getValue().toString());
                }
            }
            //写参数
            if(params != null){
                httpOut = conn.getOutputStream();
                if(params instanceof String){
                    httpOut.write(StringUtil.toBytes((String)params));
                }else if(params instanceof Map){
                    writeParams(httpOut, (Map)params);
                }else {
                    System.out.println("不支持的参数类型："+params.getClass().getName());
                }
                IOUtil.closeQuietly(httpOut);
            }
            //读取
            return parseResponse(urlstr, conn);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String upload(String urlstr, File file){
        return upload(urlstr, file, "file");
    }

    public static String upload(String urlstr, File file, String formName){
        try{
            return upload(urlstr, new FileInputStream(file), file.getName(), formName);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String upload(String url, InputStream in, String fileName, String formName){
        return upload(url, in, fileName, formName, null);
    }

    public static String upload(String urlstr, File file, Map<String, Object> params){
        return upload(urlstr, file, "file", params);
    }

    public static String upload(String urlstr, File file, String formName, Map<String, Object> params){
        try{
            return upload(urlstr, new FileInputStream(file), file.getName(), formName, params);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String upload(String urlstr, InputStream fileIn, String fileName, String formName, Map<String, Object> params){
        String BOUNDARY = "---------------------------123821742118716";
        OutputStream httpOut = null;
        try {
            HttpURLConnection conn = getConnection(urlstr, "post");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            httpOut = conn.getOutputStream();
            // text
            writeParams(BOUNDARY, params, httpOut);
            // file
            writeFile(BOUNDARY, formName, fileName, fileIn, httpOut);
            // 读取返回数据
            return parseResponse(urlstr, conn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtil.closeQuietly(httpOut, fileIn);
        }
    }

    private static HttpURLConnection getConnection(String urlstr, String method)throws Exception{
        method = method.toUpperCase();
        URL url = new URL(urlstr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        if(method.equals("POST")){
            conn.setDoOutput(true);
        }
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod(method);
        conn.setRequestProperty("Connection","Keep-Alive");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0");
        allowHttps(conn);
        return conn;
    }

    private static void allowHttps(HttpURLConnection conn) {
        if(conn instanceof HttpsURLConnection){
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws CertificateException {
                }
            } };
            try {
                SSLContext sc = SSLContext.getInstance("SSL", "SunJSSE");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection sconn = (HttpsURLConnection)conn;
                sconn.setSSLSocketFactory(sc.getSocketFactory());
                sconn.setHostnameVerifier((hostname, sslSession)->true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static String parseResponse(String url, HttpURLConnection conn){
        InputStream httpIn = null;
        try{
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                httpIn = conn.getInputStream();
                byte[] res = IOUtil.readInputStream(httpIn);
                return StringUtil.toString(res);
            }else{
                throw new RuntimeException("url:"+url+"返回："+responseCode);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally{
            IOUtil.closeQuietly(httpIn);
        }
    }

    private static String appendParamsToUrl(String urlstr, Map<String, Object> params){
        if(params == null || params.size() <= 0){
            return urlstr;
        }
        String paramstr = createParamString(params);
        if(urlstr.indexOf("?") > 0){
            urlstr += "&" + paramstr;
        }else{
            urlstr += "?" + paramstr;
        }
        return urlstr;
    }

    private static void writeParams(OutputStream outStream, Map<String, Object> params)throws Exception {
        if(params == null || params.size() <= 0){
            return;
        }
        String paramstr = createParamString(params);
        outStream.write(StringUtil.toBytes(paramstr));
        outStream.flush();
    }

    private static void writeParams(String boundary, Map<String, Object> params, OutputStream out)throws Exception{
        if (params == null || params.size() <= 0) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        for(Map.Entry<String, Object> entry : params.entrySet()){
            String name = (String) entry.getKey();
            Object value = (Object) entry.getValue();
            if (name == null) {
                continue;
            }
            sb.append("\r\n").append("--").append(boundary).append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
            sb.append(value.toString());
        }
        out.write(sb.toString().getBytes());
        out.flush();
    }

    private static void writeFile(String boundary, String inputName, String fileName, InputStream in, OutputStream out)throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append("\r\n").append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + fileName + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");
        out.write(sb.toString().getBytes());
        int bytes = 0;
        byte[] buff = new byte[1024];
        while ((bytes = in.read(buff)) != -1) {
            out.write(buff, 0, bytes);
        }
        in.close();
        byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
        out.write(endData);
        out.flush();
    }

    private static String createParamString(Map<String, Object> params){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Object> entry : params.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            sb.append(key).append("=").append(StringUtil.urlEncode(value.toString())).append("&");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    /**
     *     @GetMapping("/get/test1")
     *     public String getTest1(@RequestParam("username")String username){
     *         log.error("username:{}", username);
     *         return username;
     *     }
     *     @PostMapping("/post/test1")
     *     public String postTest1(@RequestParam("username")String username){
     *         log.error("username:{}", username);
     *         return username;
     *     }
     *     @PostMapping("/post/test2")
     *     public String postTest2(@RequestBody User user){
     *         log.error("user:{}", user.toString());
     *         return user.toString();
     *     }
     *     @PostMapping("/post/test3")
     *     public String postTest2(@RequestParam("file") MultipartFile file,
     *                             @RequestParam("username") String username){
     *         log.error("username:{},file.name:{},file.length:{}", username, file.getOriginalFilename(), file.getSize());
     *         return username+","+file.getOriginalFilename()+","+file.getSize();
     *     }
    **/
    public static void main(String[] args)throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username","中文测试");
        String res = HttpUtil.get("http://localhost:8081/demo/get/test1", params);
        System.out.println(res);
        params = new HashMap<String, Object>();
        params.put("username","中文测试");
        res = HttpUtil.post("http://localhost:8081/demo/post/test1", params);
        System.out.println(res);
        res = HttpUtil.postJosn("http://localhost:8081/demo/post/test2", "{\"username\":\"中文测试\"}");
        System.out.println(res);
        params = new HashMap<String, Object>();
        params.put("username","中文测试");
        res = HttpUtil.upload("http://localhost:8081/demo/post/test3", new File("d:\\log.txt"), params);
        System.out.println(res);
    }

}
