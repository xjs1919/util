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