package com.github.xjs.util.enums;


import java.util.function.Function;

/**
 * @author 605162215@qq.com
 */
public class EnumUtil {

    public static <V, E extends Enum<E> & EnumAble<V>> E getByValue(Class<E> clazz, V value) {
        return getByFunc(clazz, (e)->e.getValue().equals(value));
    }

    public static <V, E extends Enum<E> & EnumAble<V>> E getByLabel(Class<E> clazz, String label) {
        return getByFunc(clazz, (e)->e.getLabel().equals(label));
    }

    public static <V, E extends Enum<E> & EnumAble<V>> E getByName(Class<E> clazz, String name) {
       return getByFunc(clazz, (e)->e.name().equals(name));
    }

    public static <V, E extends Enum<E> & EnumAble<V>> String valueToLabel(Class<E> clazz, V value) {
        E e = getByValue(clazz, value);
        if(e == null){
            return null;
        }
        return e.getLabel();
    }

    public static <V, E extends Enum<E> & EnumAble<V>> V labelToValue(Class<E> clazz, String label) {
        E e = getByLabel(clazz, label);
        if(e == null){
            return null;
        }
        return e.getValue();
    }

    public static <V, E extends Enum<E> & EnumAble<V>> String valueToName(Class<E> clazz, V value) {
        E e = getByValue(clazz, value);
        if(e == null){
            return null;
        }
        return e.name();
    }

    public static <V, E extends Enum<E> & EnumAble<V>> V nameToValue(Class<E> clazz, String name) {
        E e = getByName(clazz, name);
        if(e == null){
            return null;
        }
        return e.getValue();
    }

    public static <V, E extends Enum<E> & EnumAble<V>> E getByFunc(Class<E> clazz, Function<E, Boolean> func) {
        if (clazz == null) {
            return null;
        }
        E[] enums = clazz.getEnumConstants();
        for (E e : enums) {
            if (e == null) {
                continue;
            }
            if (func.apply(e)) {
                return e;
            }
        }
        return null;
    }

}
