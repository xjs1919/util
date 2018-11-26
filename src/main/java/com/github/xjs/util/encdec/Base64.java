/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.github.xjs.util.encdec;

public final class Base64 {

    /**
     * Encodes hex octects into Base64
     *
     * @param binaryData Array containing binaryData
     * @return Encoded Base64 array
     */
    public static String encode(byte[] binaryData) {
    	return java.util.Base64.getEncoder().encodeToString(binaryData);
    }

    /**
     * Decodes Base64 data into octects
     *
     * @param encoded string containing Base64 data
     * @return Array containind decoded data.
     */	
    public static byte[] decode(String encoded) {
    	return java.util.Base64.getDecoder().decode(encoded);
    }

}
