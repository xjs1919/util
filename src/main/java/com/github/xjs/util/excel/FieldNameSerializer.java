package com.github.xjs.util.excel;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年8月23日 下午1:45:04<br/>
 */
public abstract class FieldNameSerializer<T, V> implements FieldSerializer<T,V> {
	private String fieldName;
	public FieldNameSerializer(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldName() {
		return this.fieldName;
	}
}