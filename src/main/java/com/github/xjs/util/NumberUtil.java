/**
 * 
 */
package com.github.xjs.util;

import java.text.DecimalFormat;

/**
 * @author 605162215@qq.com
 *
 *         2016年8月15日 上午10:04:47
 */
public class NumberUtil {

	public static int parseInt(String num) {
		if(StringUtil.isEmpty(num)){
			return Integer.MIN_VALUE;
		}
		try {
			return Integer.parseInt(num);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return Integer.MIN_VALUE;
		}
	}

	public static float parseFloat(String num) {
		if(StringUtil.isEmpty(num)){
			return Float.MIN_VALUE;
		}
		try {
			return Float.parseFloat(num);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return Float.MIN_VALUE;
		}
	}

	public static double parseDouble(String num) {
		if(StringUtil.isEmpty(num)){
			return Double.MIN_VALUE;
		}
		try {
			return Double.parseDouble(num);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return Double.MIN_VALUE;
		}
	}

	public static Long parseLong(String num) {
		if(StringUtil.isEmpty(num)){
			return Long.MIN_VALUE;
		}
		try {
			return Long.parseLong(num);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return Long.MIN_VALUE;
		}
	}

	public static String round2(double f) {
		DecimalFormat decimalFormat = new DecimalFormat("0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
		return decimalFormat.format(f);
	}
}
