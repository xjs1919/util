package com.github.xjs.util;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.github.xjs.util.PoiUtil.FieldInfo;
import com.github.xjs.util.PoiUtil.FieldNameSerializer;
import com.github.xjs.util.PoiUtil.FieldSerializer;
import com.github.xjs.util.PoiUtil.FieldTypeSerializer;

/**
 * @author xujs@inspur.com
 *
 * @date 2017年8月16日 下午3:35:56
 */
public class PoiExport {
	
    private Map<Class<?>,FieldSerializer<?,?>> fieldTypeRegistry = new HashMap<Class<?>,FieldSerializer<?,?>>();
    private Map<String, FieldSerializer<?,?>>  filedNameRegistry = new HashMap<String,FieldSerializer<?,?>>();
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public PoiExport() {
    	registerFormatter(new DateSerializer());
    }

    public <T,V> void registerFormatters(List<FieldSerializer<T, V>> serializers) {
		if(serializers == null || serializers.size() <= 0) {
			return;
		}
		for(FieldSerializer<T, V> serializer : serializers) {
			if(serializer instanceof FieldNameSerializer) {
				registerFormatter((FieldNameSerializer<T, V>)serializer);
			}else if(serializer instanceof FieldTypeSerializer) {
				registerFormatter((FieldTypeSerializer<T, V>)serializer);
			}
		}
	}

    public <T,V> void registerFormatter(FieldNameSerializer<T,V> serializer){
        filedNameRegistry.put(serializer.getFieldName(), serializer);
    }

    public <T,V> void registerFormatter(FieldTypeSerializer<T, V> serializer){
        fieldTypeRegistry.put(serializer.getFieldType(), serializer);
    }
    
    public <T> byte[] writeExcel(List<String> headers, List<T> dataSet) throws Exception {
    	if(dataSet == null || dataSet.size() <= 0) {
    		return null;
    	}
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet("sheet1");
        int index = 0;
        // 产生表格标题行
        if(headers != null && headers.size() > 0) {
        	HSSFRow row = sheet.createRow(index++);
            for (int i = 0; i < headers.size(); i++) {
                HSSFCell cell = row.createCell(i);
                HSSFRichTextString text = new HSSFRichTextString(headers.get(i));
                cell.setCellValue(text);
            }
        }
        // 产生表格体
        List<FieldInfo> fieldInfos = PoiUtil.getFiledInfos(dataSet.get(0).getClass());
        if(fieldInfos != null && fieldInfos.size() > 0) {
        	for (T t : dataSet) {
            	HSSFRow row = sheet.createRow(index++);
            	for (int i = 0; i < fieldInfos.size(); i++) {
                    HSSFCell cell = row.createCell(i);
                    String value = getValue(t, fieldInfos.get(i).getFiled());
                    HSSFRichTextString richString = new HSSFRichTextString(value);
                    cell.setCellValue(richString);
                }
            }
        }
        // 输出
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    private String getValue(Object bean, Field field) throws Exception {
        Object value = field.get(bean);
        if(value != null) {
        	return toString(bean,value,field.getName());
        }
        return "";
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private String toString(Object bean, Object fieldValue, String fieldName) {
        if(fieldValue == null){
            return "";
        }
        FieldSerializer serializer = getFieldSerializer(bean, fieldValue, fieldName);
        if(serializer != null){
            return serializer.serialize(bean, fieldValue);
        }else{
            return fieldValue.toString();
        }
    }

    private FieldSerializer<?,?> getFieldSerializer(Object bean, Object fieldValue, String fieldName){
        if(fieldName != null && fieldName.length() > 0){
            FieldSerializer<?,?> serializer = filedNameRegistry.get(fieldName);
            if(serializer != null){
                return serializer;
            }
        }
        if(fieldValue != null){
            FieldSerializer<?,?> serializer = fieldTypeRegistry.get(fieldValue.getClass());
            if(serializer != null){
                return serializer;
            }
            for(Map.Entry<Class<?>,FieldSerializer<?,?>> entry : fieldTypeRegistry.entrySet()){
                Class<?> clazz = entry.getKey();
                if(clazz.isAssignableFrom(fieldValue.getClass())){
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public static class DateSerializer<T> extends FieldTypeSerializer<T, Date> {
        public DateSerializer() {
			super(Date.class);
		}
        @Override
        public String serialize(T bean, Date object) {
            if(object == null){
                return "";
            }
            return DateUtil.format(object, DateUtil.FORMAT_YMDHMS);
        }
    }

}
