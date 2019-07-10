package com.github.xjs.util.excel;

import java.lang.reflect.Field;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年8月23日 下午1:45:52<br/>
 */
public class FieldInfo{
    private Field filed;
    private int order;
    private String name;
    public FieldInfo(Field filed, int order, String name) {
        super();
        this.filed = filed;
        this.order = order;
        this.name = name;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}