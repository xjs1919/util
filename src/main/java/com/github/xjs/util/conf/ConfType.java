package com.github.xjs.util.conf;


import com.github.xjs.util.StringUtil;

/**
 * @Description
 * @Author xujs@mamcharge.com
 * @Date 2019/9/7 12:53
 **/
public enum ConfType {

    YMAL(1, "yml", new ConfYml()),
    PROPERTIES(2, "properties", new ConfProp());

    private int type;
    private String suffix;
    private ConfAble confHandler;
    private ConfType(int type, String suffix, ConfAble confAble){
        this.type = type;
        this.suffix = suffix;
        this.confHandler = confAble;
    }
    public int getType() {
        return type;
    }

    public String getSuffix() {
        return suffix;
    }

    public ConfAble getConfHandler() {
        return confHandler;
    }

    public static ConfType getBySuffix(String suffix){
        if(StringUtil.isEmpty(suffix)){
            return null;
        }
        suffix = suffix.toLowerCase();
        if(suffix.endsWith("yml")){
            return YMAL;
        }else{
            return PROPERTIES;
        }
    }
}
