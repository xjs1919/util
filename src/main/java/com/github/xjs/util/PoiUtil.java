package com.github.xjs.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

/**
 * @author xujs@inspur.com
 *
 * @date 2017年8月16日 下午2:1:12
 */
public class PoiUtil {
	
	@Retention(RetentionPolicy.RUNTIME)  
	@Target(ElementType.FIELD)  
	public static @interface FiledOrder{
		public int value();//从0开始
	}
	
	public interface FieldSerializer<T,V>{
        public String serialize(T bean, V fieldValue);
    }

	public static abstract class FieldNameSerializer<T, V> implements FieldSerializer<T,V> {
		private String fieldName;
		public FieldNameSerializer(String fieldName) {
			this.fieldName = fieldName;
		}
		public String getFieldName() {
			return this.fieldName;
		}
	}
	
	public static abstract class FieldTypeSerializer<T, V> implements FieldSerializer<T,V> {
		private Class<V> fieldType;
		public FieldTypeSerializer(Class<V> clazz) {
			this.fieldType = clazz;
		}
		public Class<V> getFieldType(){
			return this.fieldType;
		}
	}
	
	public static <T> List<T> readExcel(String filename, byte[] bytes,Class<T> clazz) throws IOException{  
		return PoiImport.readExcel(filename, bytes, clazz, 0, true);
	}
	
	public static <T> List<T> readExcel(String filename, byte[] bytes,Class<T> clazz, int sheetIndex) throws IOException{  
		return PoiImport.readExcel(filename, bytes, clazz, sheetIndex, true);
	}
	
	public static <T> List<T> readExcel(String filename, byte[] bytes,Class<T> clazz, int sheetIndex, boolean skipFirst) throws IOException{  
		return PoiImport.readExcel(filename, bytes, clazz, sheetIndex, skipFirst);
	}
	
	public static <T,V> byte[] writeExcel(List<T> datas) throws Exception{
		return writeExcel((List<String>)null, datas, (List<FieldSerializer<T,V>>)null);
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
	
    public static class FieldInfo{
        private Field filed;
        private int order;
        public FieldInfo(Field filed, int order) {
            super();
            this.filed = filed;
            this.order = order;
        }
        public Field getFiled() {
            return filed;
        }
        public void setFiled(Field filed) {
            this.filed = filed;
        }
        public int getOrder() {
            return order;
        }
        public void setOrder(int order) {
            this.order = order;
        }
    }
    
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
                fieldInfos.add(new FieldInfo(field, orderAnno.value()));
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
    // 以下是TEST
    public static class User{//不用遵守javabean规范
    	@FiledOrder(0)
    	private int id;
    	@FiledOrder(1)
    	private String name;
    	@FiledOrder(2)
    	private Date birthDay;
    	@FiledOrder(3)
    	private boolean isMale;
    	public User() {}
    	public User(int id, String name, Date birthDay, boolean isMale) {
    		this.id = id;
    		this.name = name;
    		this.birthDay = birthDay;
    		this.isMale = isMale;
    	}
		@Override
		public String toString() {
			return "User [id=" + id + ", name=" + name + ", birthDay=" + birthDay + ", isMale=" + isMale + "]";
		}
    }
    public static void main(String args[])throws Exception {
    	String file = "C:\\Users\\xujs\\Desktop\\aa.xls";
    	User user1 = new User(1, "aaa", new Date(), true);
    	User user2 = new User(2, "bbb", new Date(), false);
    	User user3 = new User(3, "ccc", new Date(), true);
    	User user4 = new User(4, "ddd", new Date(), false);
    	List<User> users = new ArrayList<User>(4);
    	users.add(user1);users.add(user2);users.add(user3);users.add(user4);
    	FieldSerializer<User, Boolean> serializer = new FieldNameSerializer<User, Boolean>("isMale") {
			@Override
			public String serialize(User bean, Boolean fieldValue) {
				if(fieldValue) {
					return "是";
				}else {
					return "否";
				}
			}
    	};
    	byte[] bytes = writeExcel(new String[] {"id","姓名","生日","是否男性"}, users, serializer);
    	OutputStream out = new FileOutputStream(file);
    	out.write(bytes);
    	out.close();
    	System.out.println("write over");
    	
    	InputStream in = new FileInputStream(file);
    	bytes = IOUtil.readInputStream(in);
    	in.close();
    	List<User> list = readExcel(file,bytes, User.class);
    	System.out.println(list.size());
    	for(User u : list) {
    		System.out.println(u);
    	}
    	System.out.println("read over");
	}
}
