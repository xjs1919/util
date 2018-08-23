package com.github.xjs.util.excel;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年8月23日 下午1:45:21<br/>
 */
public abstract class FieldTypeSerializer<T, V> implements FieldSerializer<T,V> {
	private Class<V> fieldType;
	public FieldTypeSerializer(Class<V> clazz) {
		this.fieldType = clazz;
	}
	public Class<V> getFieldType(){
		return this.fieldType;
	}
}
