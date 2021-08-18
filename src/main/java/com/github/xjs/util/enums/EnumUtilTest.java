package com.github.xjs.util.enums;

/**
 * @author 605162215@qq.com
 */
public class EnumUtilTest {

    public enum GenderEnum implements EnumAble<Integer> {

        MALE(1, "男"),
        FEMAIL(2, "女")
        ;

        private Integer value;
        private String label;

        GenderEnum(Integer value, String label){
            this.value = value;
            this.label = label;
        }

        @Override
        public Integer getValue() {
            return this.value;
        }

        @Override
        public String getLabel() {
            return this.label;
        }
    }

    public static void main(String[] args) {
        GenderEnum gender = EnumUtil.getByValue(GenderEnum.class, 1);
        System.out.println(gender);

        gender = EnumUtil.getByName(GenderEnum.class, "MALE");
        System.out.println(gender);

        gender = EnumUtil.getByLabel(GenderEnum.class, "男");
        System.out.println(gender);

        Integer value = EnumUtil.labelToValue(GenderEnum.class, "男");
        System.out.println(value);

        String label =  EnumUtil.valueToLabel(GenderEnum.class, 1);
        System.out.println(label);

        String name = EnumUtil.valueToName(GenderEnum.class, 1);
        System.out.println(name);

        value = EnumUtil.nameToValue(GenderEnum.class, "MALE");
        System.out.println(value);
    }

}
