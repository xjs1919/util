package com.github.xjs.util.enums;

public class BaseEnum<T> implements EnumAble<T> {
	private T key;
	private String label;
	public BaseEnum(T key, String label){
		this.key = key;
		this.label = label;
		EnumFactory.add(this);
	}
	@Override
	public T getValue() {
		return this.key;
	}
	@Override
	public String getLabel() {
		return this.label;
	}
	@Override
	public String toString(){
		return getValue() +  "="  + getLabel();
	}
}
