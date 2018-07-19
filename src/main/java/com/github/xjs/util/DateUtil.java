/**
 * 
 */
package com.github.xjs.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月12日 下午5:32:37
 */
public class DateUtil {
	public static final String FORMAT_MDHM = "MM-dd HH:mm";
	public static final String FORMAT_YMD = "yyyy-MM-dd";
	public static final String FORMAT_YMDHM = "yyyy-MM-dd HH:mm";
	public static final String FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_GMT = "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'";

	private static final Locale DEFAULT_LOCALE = Locale.CHINA;
	
	private static ThreadLocal<Map<String, SimpleDateFormat>> threadLocal = new ThreadLocal<Map<String, SimpleDateFormat>>() {  
        protected synchronized Map<String, SimpleDateFormat> initialValue() {  
        	Map<String, SimpleDateFormat> map = new HashMap<String, SimpleDateFormat>();
        	map.put(FORMAT_MDHM, new SimpleDateFormat(FORMAT_MDHM, DEFAULT_LOCALE));
        	map.put(FORMAT_YMD, new SimpleDateFormat(FORMAT_YMD, DEFAULT_LOCALE));
        	map.put(FORMAT_YMDHM, new SimpleDateFormat(FORMAT_YMDHM, DEFAULT_LOCALE));
        	map.put(FORMAT_YMDHMS, new SimpleDateFormat(FORMAT_YMDHMS, DEFAULT_LOCALE));
        	map.put(FORMAT_GMT, new SimpleDateFormat(FORMAT_GMT, Locale.ENGLISH));
            return map;  
        }  
    };  
  
    private DateUtil(){}
    
    public static SimpleDateFormat getDateFormat(String format) {  
    	Map<String, SimpleDateFormat> map = (Map<String, SimpleDateFormat>) threadLocal.get();  
    	SimpleDateFormat sdf = map.get(format);
    	if(sdf != null){
    		return sdf;
    	}
		try{
			sdf = new SimpleDateFormat(format, DEFAULT_LOCALE);
			map.put(format, sdf);
		}catch(Exception e){
			e.printStackTrace();
		}
		return sdf;
    }  
  
    public static Date parse(String textDate, String format) { 
    	if(textDate == null || textDate.length() <= 0){
    		return null;
    	}
    	try{
    		SimpleDateFormat sdf = getDateFormat(format);
    		if(sdf == null){
    			return null;	
    		}
    		return sdf.parse(textDate);  
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }  
    
	public static String format(Date date, String format){
		if(date == null){
			return null;
		}
		SimpleDateFormat sdf = getDateFormat(format);
		if(sdf == null){
			return null;
		}
		return sdf.format(date);
	}
	
	public static Date east8ToGmt(Date src){
		if(src == null){
			return null;
		}
		TimeZone srcTimeZone = TimeZone.getTimeZone("GMT+8");  
	    TimeZone destTimeZone = TimeZone.getTimeZone("GMT");  
		long targetTime = src.getTime() - srcTimeZone.getRawOffset() + destTimeZone.getRawOffset();  
        return new Date(targetTime);
	}
	
	public static String now() {
        return DateUtil.format(new Date(), DateUtil.FORMAT_MDHM);
    }
	
	public static String nowDateTime() {
        return DateUtil.format(new Date(), DateUtil.FORMAT_YMDHMS);
    }
	
	public static Date addDays(Date date, int days){
		if(date == null){
			return null;
		}
		Calendar cal = Calendar.getInstance(DEFAULT_LOCALE);
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}
	
	public static Date addHours(Date date, int hours){
		if(date == null){
			return null;
		}
		Calendar cal = Calendar.getInstance(DEFAULT_LOCALE);
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, hours);
		return cal.getTime();
	}
	
	public static Date addMinute(Date date, int minutes){
		if(date == null){
			return null;
		}
		Calendar cal = Calendar.getInstance(DEFAULT_LOCALE);
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}
	
	public static Date firstDayOfMonth(){
		return firstDayOfMonth(null); 
	}
	
	public static Date firstDayOfMonth(Date date){
		Calendar  cal = Calendar.getInstance(DEFAULT_LOCALE);  
		if(date != null){
			cal.setTime(date);
		}
        int firstDay  = cal.getActualMinimum(Calendar.DAY_OF_MONTH); 
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        return cal.getTime();  
	}
	
	public static Date lastDayOfMonth(){
		return lastDayOfMonth(null);
	}
	
	public static Date lastDayOfMonth(Date date){
		Calendar  cal = Calendar.getInstance(DEFAULT_LOCALE); 
		if(date != null){
			cal.setTime(date);
		}
        int lastDay  = cal.getActualMaximum(Calendar.DAY_OF_MONTH);  
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        return cal.getTime();  
	}
	
	/**
	 * 把日期字符串标准化成yyyyMMdd格式，比如：<br/>
	 * 2018年12月12日上午8点10分<br/>
	 * 2018/12/12 上午 8点10分<br/>
	 * 2018-12-12 上午 8点10分<br/>
	 * 2018-12-12 8点10分<br/>
	 * 2018-12-12<br/>
	 * */
	public static String formalizeToYMD(String src) {
		if(src == null || src.length() <= 0) {
			return null;
		}
		src = src.replaceAll("[年月日号]", "-");
		src = src.replace("/", "-");
		src = src.replaceAll("([\\d\\-]*).*", "$1");
		String arr[] = src.split("\\-");
		StringBuilder sb = new StringBuilder();
		for(String s : arr) {
			if(s.length() < 2) {
				sb.append("0");
			}
			sb.append(s);
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(format(addMinute(new Date(), -5),FORMAT_YMDHMS));
		System.out.println(format(addHours(new Date(), -5),FORMAT_YMDHMS));
		System.out.println(format(firstDayOfMonth(),FORMAT_YMD)+","+format(lastDayOfMonth(),FORMAT_YMD));
	}
}
