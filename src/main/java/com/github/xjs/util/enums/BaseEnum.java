package com.github.xjs.util.enums;

public abstract class BaseEnum<T> implements EnumAble<T> {
	private T key;
	private String label;
	private boolean isDefault;
	
	public BaseEnum(T key, String label){
		this(key, label, false);
	}
	
	public BaseEnum(T key, String label, boolean isDefault ){
		this.key = key;
		this.label = label;
		this.isDefault = isDefault;
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
	public boolean isDefault() {
		return isDefault;
	}
	@Override
	public String toString(){
		return getValue() +  "="  + getLabel()+"="+isDefault;
	}
}
