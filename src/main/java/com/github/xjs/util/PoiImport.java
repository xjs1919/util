package com.github.xjs.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.github.xjs.util.PoiUtil.FieldInfo;

/**
 * @author xujs@inspur.com
 *
 * @date 2017年8月16日 下午4:44:21
 */
public class PoiImport {
	
	public static <T> List<T> readExcel(String filename, byte[] bytes, Class<T> clazz, int sheetIndex, boolean skipFirst) throws IOException{  
		List<String[]> arrList  = readExcel(filename, bytes, sheetIndex, skipFirst);
		if(arrList == null || arrList.size() <= 0) {
			return null;
		}
		List<T> list = new ArrayList<T>(arrList.size());
		for(String[] arr : arrList) {
			T t = stringArrToBean(arr, clazz);
			if(t != null) {
				list.add(t);
			}
		}
		return list;
	}
	
	public static List<String[]> readExcel(String filename, byte[] bytes) throws IOException{  
		return readExcel(filename, bytes, 0, true);
	}
	
    public static List<String[]> readExcel(String filename, byte[] bytes, int sheetIndex, boolean skipFirst) throws IOException{  
        //获得Workbook工作薄对象  
        Workbook workbook = getWorkBook(bytes, filename);  
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回  
        List<String[]> list = new ArrayList<String[]>();  
        //获得sheet工作表  
        Sheet sheet = workbook.getSheetAt(sheetIndex);  
        if(sheet == null){  
            return null;
        }  
        //获得当前sheet的开始行  
        int firstRowNum  = sheet.getFirstRowNum();  
        //获得当前sheet的结束行  
        int lastRowNum = sheet.getLastRowNum();  
        //循环所有行  
        if(skipFirst) {
        	firstRowNum ++;
        }
        for(int rowNum = firstRowNum;rowNum <= lastRowNum;rowNum++){  
            //获得当前行  
            Row row = sheet.getRow(rowNum);  
            if(row == null){  
                continue;  
            }  
            //获得当前行的开始列  
            int firstCellNum = row.getFirstCellNum();
            //获得当前行的列数
			// int lastCellNum = row.getPhysicalNumberOfCells();
			// String[] cells = new String[row.getPhysicalNumberOfCells()];

			//修改记录：
			/**
			 * lastCellNum获取的事物理行数，假如一行数据从第10行开始有3行数据，firstCellNum是10,
			 * lastCellNum就是3，下面的循环cellNum<lastCellNum就不会执行，这一行的数据就不会被保存。
			 * lastCellNum修改为获取最后一行的的下标，无论从第几行有数据，firstCellNum始终小于等于lastCellNum
			 * String[]的长度也修改一下，不然会出现数组越界
			 */
			int lastCellNum = row.getLastCellNum();
			String[] cells = new String[row.getLastCellNum()];
            //循环当前行  
            for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){  
                Cell cell = row.getCell(cellNum);  
                cells[cellNum] = getCellValue(cell);  
            }  
            list.add(cells);  
        }  
        workbook.close();  
        return list;  
    } 
    
    private static <T> T stringArrToBean(String[] arr, Class<T> clazz) {
		try {
			T t = clazz.newInstance();
			List<FieldInfo> fields = PoiUtil.getFiledInfos(clazz);
			if(fields == null || fields.size() <= 0) {
				return null;
			}
			for(FieldInfo filed : fields) {
				int idx = filed.getOrder();
				if(arr.length <= idx) {
					continue;
				}
				fillBeanFieldValue(t, filed, arr[idx]);
			}
			return t;
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
    
	private static <T> void fillBeanFieldValue(T bean, FieldInfo filedInfo, String fieldValue) {
		if(fieldValue == null || fieldValue.length() <= 0) {
			return;
		}
		try {
			Field filed = filedInfo.getFiled();
			Class<?> fieldType = filed.getType();
			if(fieldType == String.class) {
				filed.set(bean, fieldValue);
			}else if(fieldType == int.class) {
				filed.set(bean, Integer.valueOf(fieldValue).intValue());
			}else if(fieldType == byte.class) {
				filed.set(bean, Byte.valueOf(fieldValue).byteValue());
			}else if(fieldType == long.class) {
				filed.set(bean, Long.valueOf(fieldValue).longValue());
			}else if(fieldType == double.class) {
				filed.set(bean, Double.valueOf(fieldValue).doubleValue());
			}else if(fieldType == boolean.class) {
				if("是".equals(fieldValue) || "Y".equalsIgnoreCase(fieldValue) || "YES".equalsIgnoreCase(fieldValue) || "T".equalsIgnoreCase(fieldValue) || "TRUE".equalsIgnoreCase(fieldValue)) {
					filed.set(bean, true);
				}else if("否".equals(fieldValue) || "N".equalsIgnoreCase(fieldValue) || "NO".equalsIgnoreCase(fieldValue) || "F".equalsIgnoreCase(fieldValue) || "FALSE".equalsIgnoreCase(fieldValue)) {
					filed.set(bean, false);
				}
			}else if(fieldType == Integer.class) {
				filed.set(bean, Integer.valueOf(fieldValue));
			}else if(fieldType == Byte.class) {
				filed.set(bean, Byte.valueOf(fieldValue));
			}else if(fieldType == Long.class) {
				filed.set(bean, Long.valueOf(fieldValue));
			}else if(fieldType == Double.class) {
				filed.set(bean, Double.valueOf(fieldValue));
			}else if(fieldType == Boolean.class) {
				if("是".equals(fieldValue) || "Y".equalsIgnoreCase(fieldValue) || "YES".equalsIgnoreCase(fieldValue) || "T".equalsIgnoreCase(fieldValue) || "TRUE".equalsIgnoreCase(fieldValue)) {
					filed.set(bean, Boolean.TRUE);
				}else if("否".equals(fieldValue) || "N".equalsIgnoreCase(fieldValue) || "NO".equalsIgnoreCase(fieldValue) || "F".equalsIgnoreCase(fieldValue) || "FALSE".equalsIgnoreCase(fieldValue)) {
					filed.set(bean, Boolean.FALSE);
				}
			}else if(fieldType == Date.class){
				filed.set(bean, DateUtil.parse(fieldValue, DateUtil.FORMAT_YMDHMS));
			}else {
				throw new RuntimeException("不支持的数据类型："+filed.getName()+":"+fieldType);
			}
		}catch(Exception e){
			throw new RuntimeException("设置字段值异常:"+bean+","+filedInfo.getFiled().getName()+":"+fieldValue, e);
		}
	}

    private static Workbook getWorkBook(byte[] bytes, String fileName) {  
        try {  
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象  
            if(fileName.endsWith(".xls")){  
                //2003  
                return new HSSFWorkbook(new ByteArrayInputStream(bytes));  
            }else if(fileName.endsWith(".xlsx")){  
                //2007  
                return new XSSFWorkbook(new ByteArrayInputStream(bytes));  
            } else {
            	throw new RuntimeException("文件后缀名不合法");
            }
        } catch (IOException e) {  
            LogUtil.info(()->e.getMessage());  
            throw new RuntimeException(e);
        }  
    }  
    
    private static String getCellValue(Cell cell){  
        String cellValue = "";  
        if(cell == null){  
            return cellValue;  
        }  
        //把数字当成String来读，避免出现1读成1.0的情况  
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){  
            cell.setCellType(Cell.CELL_TYPE_STRING);  
        }  
        //判断数据的类型  
        switch (cell.getCellType()){  
            case Cell.CELL_TYPE_NUMERIC: //数字  
                cellValue = String.valueOf(cell.getNumericCellValue());  
                break;  
            case Cell.CELL_TYPE_STRING: //字符串  
                cellValue = String.valueOf(cell.getStringCellValue());  
                break;  
            case Cell.CELL_TYPE_BOOLEAN: //Boolean  
                cellValue = String.valueOf(cell.getBooleanCellValue());  
                break;  
            case Cell.CELL_TYPE_FORMULA: //公式  
                cellValue = String.valueOf(cell.getCellFormula());  
                break;  
            case Cell.CELL_TYPE_BLANK: //空值   
                cellValue = "";  
                break;  
            case Cell.CELL_TYPE_ERROR: //故障  
                cellValue = "非法字符";  
                break;  
            default:  
                cellValue = "未知类型";  
                break;  
        }  
        return cellValue;  
    }  
    
    
}
