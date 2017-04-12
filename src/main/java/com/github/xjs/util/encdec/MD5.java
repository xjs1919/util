package com.github.xjs.util.encdec;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.github.xjs.util.HexUtil;

public class MD5 {

	public static String md5(final String src) {
		return md5(src, "");
	}
	
	public static String md5(final String src, final String salt) {
		final String saltedSrc = mergePasswordAndSalt(src, salt, false);
		final MessageDigest messageDigest = getMessageDigest();
		byte[] digest;
		try {
			digest = messageDigest.digest(saltedSrc.getBytes("UTF-8"));
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 not supported!");
		}
		return new String(HexUtil.encodeHex(digest));
	}
	
	private static final MessageDigest getMessageDigest() {
		final String algorithm = "MD5";
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (final NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such algorithm [" + algorithm + "]");
		}
	}

	private static String mergePasswordAndSalt(String password, final Object salt, final boolean strict) {
		if (password == null) {
			password = "";
		}
		if (strict && salt != null) {
			if (salt.toString().lastIndexOf("{") != -1 || salt.toString().lastIndexOf("}") != -1)
				throw new IllegalArgumentException("Cannot use { or } in salt.toString()");
		}
		if (salt == null || "".equals(salt))
			return password;
		else
			return password + salt.toString();
	}
	
	public static void main(String[] args) {
		String ss = MD5.md5("hello,world");
		System.out.println(ss);
	}
}
