/**
 * 
 */
package com.github.xjs.util;

import java.io.FileInputStream;
import java.util.List;

import com.github.xjs.bean.ImportSentence;

/**
 * @author xujs@inspur.com
 *
 * @date 2017年8月21
 */
public class PoiTest {
	public static void main(String[] args)throws Exception {
		String fileName = "C:\\Users\\xujs\\Desktop\\三上recycle1.xlsx";
		byte[] bytes = IOUtil.readInputStream(new FileInputStream(fileName));
		List<ImportSentence> sentences = PoiUtil.readExcel(fileName,bytes,ImportSentence.class);
		for(ImportSentence sentence : sentences) {
			System.out.println(sentence);
		}
	}
}
