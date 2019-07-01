package com.github.xjs.util.excel;

/**
 * @author xujs@inspur.com
 *
 * @date 2019年7月1日 下午3:56:11<br/>
 */
public interface RowStarter{
	public boolean isStartRow(String[] prevRowItems, String[] curRowItems);
}