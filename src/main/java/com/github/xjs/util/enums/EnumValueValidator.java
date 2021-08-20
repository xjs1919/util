package com.github.xjs.util.enums;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author jiashuai.xujs
 * @date 2021/8/20 10:13
 */
public class EnumValueValidator implements ConstraintValidator<IsValidEnum, Object> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(IsValidEnum constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        if (EnumAble.class.isAssignableFrom(enumClass)){
            Enum[] enums = enumClass.getEnumConstants();
            for(Enum e : enums){
                EnumAble enumAble = (EnumAble)e;
                Object enumValue = enumAble.getValue();
                if(enumValue.equals(value)){
                    return true;
                }
            }
            return false;
        }else{
            return false;
        }
    }
}
