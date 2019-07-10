package com.github.xjs.util.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

/**
 * @author xujs@inspur.com
 *
 * @date 2017年8月16日 下午2:1:12
 */
public class PoiUtil {
	
	public static <T> List<T> readExcel(String filename, byte[] bytes,Class<T> clazz) throws IOException{  
		return PoiImport.readExcel(filename, bytes, clazz, 0, true);
	}
	
	public static <T> List<T> readExcel(String filename, byte[] bytes,Class<T> clazz, int sheetIndex) throws IOException{  
		return PoiImport.readExcel(filename, bytes, clazz, sheetIndex, true);
	}
	
	public static <T> List<T> readExcel(String filename, byte[] bytes,Class<T> clazz, RowStarter starter) throws IOException{  
		return PoiImport.readExcel(filename, bytes, clazz, 0, starter);
	}
	
	public static <T> List<T> readExcel(String filename, byte[] bytes,Class<T> clazz, int sheetIndex, RowStarter starter) throws IOException{  
		return PoiImport.readExcel(filename, bytes, clazz, sheetIndex, starter);
	}
	
	public static <T> List<T> readExcel(String filename, byte[] bytes,Class<T> clazz, int sheetIndex, boolean skipFirst) throws IOException{  
		return PoiImport.readExcel(filename, bytes, clazz, sheetIndex, skipFirst);
	}
	
	public static <T,V> byte[] writeExcel(List<T> datas) throws Exception{
		return writeExcel((List<String>)null, datas, (List<FieldSerializer<T,V>>)null);
	}
	
	@SafeVarargs
	public static <T,V> byte[] writeExcel(List<T> datas, FieldSerializer<T,V>... serializers) throws Exception{
		return writeExcel((List<String>)null, datas, serializers);
	}
	
	public static <T,V> byte[] writeExcel(String[] heads, List<T> datas) throws Exception{
		return writeExcel(heads==null?(List<String>)null:Arrays.asList(heads), datas, (List<FieldSerializer<T,V>>)null);
	}
	
	public static <T,V> byte[] writeExcel(List<String> heads, List<T> datas) throws Exception{
		return writeExcel(heads, datas, (List<FieldSerializer<T,V>>)null);
	}
	
	@SafeVarargs
	public static <T, V> byte[] writeExcel(String[] heads, List<T> datas, FieldSerializer<T,V>... serializers) throws Exception{
		return writeExcel(heads==null?(List<String>)null:Arrays.asList(heads), datas, serializers==null?(List<FieldSerializer<T,V>>)null:Arrays.asList(serializers));
	}
	
	@SafeVarargs
	public static <T, V> byte[] writeExcel(List<String> heads, List<T> datas, FieldSerializer<T,V>... serializers) throws Exception{
		return writeExcel(heads, datas, serializers==null?(List<FieldSerializer<T,V>>)null:Arrays.asList(serializers));
	}
	
	public static <T, V> byte[] writeExcel(String[] heads, List<T> datas, List<FieldSerializer<T,V>> serializers) throws Exception{
		return writeExcel(heads==null?(List<String>)null:Arrays.asList(heads), datas, serializers);
	}

	public static <T,V> byte[] writeExcel(List<String> heads, List<T> datas, List<FieldSerializer<T,V>> serializers) throws Exception{
		PoiExport pe = new PoiExport();
		pe.registerFormatters(serializers);
		return pe.writeExcel(heads, datas);
	}

	public static <T,V> void downloadExcel(HttpServletResponse response, String filename, List<T> dataset) throws Exception {
		downloadExcel(response, filename, (List<String>)null, dataset, (List<FieldSerializer<T,V>>)null);
	}
	
	public static <T,V> void downloadExcel(HttpServletResponse response, String filename, String[] headers, List<T> dataset) throws Exception {
		downloadExcel(response, filename, headers==null?null:Arrays.asList(headers), dataset, (List<FieldSerializer<T,V>>)null);
	}
	
	public static <T,V> void downloadExcel(HttpServletResponse response, String filename, List<String> headers, List<T> dataset) throws Exception {
		downloadExcel(response, filename, headers, dataset, (List<FieldSerializer<T,V>>)null);
	}
	
	@SafeVarargs
	public static <T,V> void downloadExcel(HttpServletResponse response, String filename, String[] headers, List<T> dataset,FieldSerializer<T,V>... serializers) throws Exception {
		downloadExcel(response, filename, headers==null?null:Arrays.asList(headers), dataset, serializers==null?(List<FieldSerializer<T,V>>)null:Arrays.asList(serializers));
	}
	
	@SafeVarargs
	public static <T,V> void downloadExcel(HttpServletResponse response, String filename, List<String> headers, List<T> dataset,FieldSerializer<T,V>... serializers) throws Exception {
		downloadExcel(response, filename, headers, dataset, serializers==null?null:Arrays.asList(serializers));
	}

	public static <T,V> void downloadExcel(HttpServletResponse response, String filename, String[] headers, List<T> dataset,List<FieldSerializer<T,V>> serializers) throws Exception {
		downloadExcel(response, filename, headers==null?null:Arrays.asList(headers), dataset, serializers);
	}
	
	public static <T,V> void downloadExcel(HttpServletResponse response, String filename,List<String> headers, List<T> dataset, List<FieldSerializer<T,V>> serializers) throws Exception {
        //设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("multipart/form-data;charset=UTF-8");
        //设置文件头：最后一个参数是设置下载文件名
        response.setHeader("Content-Disposition", "attachment;fileName="+ URLEncoder.encode(filename, "UTF-8"));
        byte[] bytes = writeExcel(headers, dataset, serializers);
        OutputStream out = response.getOutputStream();
        out.write(bytes);
        out.flush();
    }	
	
	private static ConcurrentHashMap<Class<?>, List<FieldInfo>> fieldInfoCache = new ConcurrentHashMap<Class<?>, List<FieldInfo>>();
	
    public static List<FieldInfo> getFiledInfos(Class<?> clazz){
		List<FieldInfo> list = fieldInfoCache.get(clazz);
		if(list != null && list.size() > 0) {
			return list;
		}
        List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
        Class<?> targetClass = clazz;
        do {
            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                if((Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()))){
                    continue;
                }
                field.setAccessible(true);
                FiledOrder orderAnno = field.getAnnotation(FiledOrder.class);
                if(orderAnno == null) {
                	continue;
                }
                fieldInfos.add(new FieldInfo(field, orderAnno.value(), orderAnno.name()));
            }
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
        Collections.sort(fieldInfos, new Comparator<FieldInfo>() {
			@Override
			public int compare(FieldInfo o1, FieldInfo o2) {
				return o1.getOrder()-o2.getOrder();
			}
        });
        fieldInfoCache.putIfAbsent(clazz, fieldInfos);
        return fieldInfos;
    }
}
