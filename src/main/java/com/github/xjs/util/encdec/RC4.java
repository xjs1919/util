package com.github.xjs.util.encdec;

import java.io.UnsupportedEncodingException;

public class RC4 {

	public static String encrypt(String data, String key) {
		if (data == null || key == null) {
			return null;
		}
		try {
			byte b_data[]= data.getBytes("UTF-8");
			byte rc4Encodered[] = RC4Base(b_data, key);
			return Base64.encode(rc4Encodered);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decrypt(String data, String key) {
		if (data == null || key == null) {
			return null;
		}
		try{
			byte[] base64Decoded = Base64.decode(data);
			byte[] rc4Decoded = RC4Base(base64Decoded, key);
			return new String(rc4Decoded, "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] initKey(String aKey) {
		byte[] b_key = aKey.getBytes();
		byte state[] = new byte[256];

		for (int i = 0; i < 256; i++) {
			state[i] = (byte) i;
		}
		int index1 = 0;
		int index2 = 0;
		if (b_key == null || b_key.length == 0) {
			return null;
		}
		for (int i = 0; i < 256; i++) {
			index2 = ((b_key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
			byte tmp = state[i];
			state[i] = state[index2];
			state[index2] = tmp;
			index1 = (index1 + 1) % b_key.length;
		}
		return state;
	}

	private static byte[] RC4Base(byte[] input, String mKkey) {
		int x = 0;
		int y = 0;
		byte key[] = initKey(mKkey);
		int xorIndex;
		byte[] result = new byte[input.length];

		for (int i = 0; i < input.length; i++) {
			x = (x + 1) & 0xff;
			y = ((key[x] & 0xff) + y) & 0xff;
			byte tmp = key[x];
			key[x] = key[y];
			key[y] = tmp;
			xorIndex = ((key[x] & 0xff) + (key[y] & 0xff)) & 0xff;
			result[i] = (byte) (input[i] ^ key[xorIndex]);
		}
		return result;
	}
	
	public static void main(String[] args) {
		String str = encrypt("中国1234", "3545C7B737D3D3223CA");
		System.out.println(str);
		str = decrypt(str, "3545C7B737D3D3223CA");
		System.out.println(str);
	}
}
