package com.github.xjs.util.enums;

public class EnumFactoryTest {
	//这是我们的枚举类
	public static class MonthEnum extends BaseEnum<Integer>{
		public static MonthEnum January = new MonthEnum(1, "1月");
		public static MonthEnum February = new MonthEnum(2, "2月");
		public static MonthEnum March = new MonthEnum(3, "3月");
	    
	    public MonthEnum(int value, String label){  
	        super(value, label);
	    }  
	}
	//test
	public static void main(String[] args) {
		MonthEnum monthEnum = EnumFactory.getByValue(MonthEnum.class, 1);
		System.out.println(monthEnum);
	}
}
