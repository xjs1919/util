package com.github.xjs.util.enums;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author jiashuai.xujs
 * @date 2021/8/20 10:13
 */
public class EnumLabelValidator implements ConstraintValidator<IsValidEnum, String> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(IsValidEnum constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(String label, ConstraintValidatorContext context) {
        if (label == null) {
            return false;
        }
        if (EnumAble.class.isAssignableFrom(enumClass)){
            Enum[] enums = enumClass.getEnumConstants();
            for(Enum e : enums){
                EnumAble enumAble = (EnumAble)e;
                String enumLabel = enumAble.getLabel();
                if(enumLabel.equals(label)){
                    return true;
                }
            }
            return false;
        }else{
            return false;
        }
    }
}
