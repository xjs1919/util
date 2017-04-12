
package com.github.xjs.util.encdec;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSA {

	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	/**
	 * RSA签名
	 * 
	 * @param content
	 *            待签名数据
	 * @param private_key
	 *            商户私钥
	 * @param input_charset
	 *            编码格式
	 * @return 签名值
	 */
	public static String sign(String content, String private_key, String input_charset) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(private_key));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(input_charset));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * RSA验签名检查
	 * 
	 * @param content
	 *            待签名数据
	 * @param sign
	 *            签名值
	 * @param public_key
	 *            支付宝公钥
	 * @param input_charset
	 *            编码格式
	 * @return 布尔值
	 */
	public static boolean verify(String content, String sign, String public_key, String input_charset) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decode(public_key);
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes(input_charset));

			boolean bverify = signature.verify(Base64.decode(sign));
			return bverify;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public static String private_key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMu9BBXXiw/bPPiRuJyUiGcnz/FCwZwoavl2nMpZkvI4qq7RZ8Be5fx5Lwnk7ajXB47zRpM9WW54Xo1Sjia2n5zFsJVdX5cCHWSG+//TMuz5f23yNYnCnNMKu/9wOoVn1BSgfnp42UmgBKH43LpxSoOxEcyYUA3A4NRqoG53IjzLAgMBAAECgYAA2g90T+xi6pW8rE9LSEuxsrbp2U+Z6YlY9af+KJW2sYn1JSb75OmtPox1zCE6PXvomxfGcdE35wTuLlQt0UGlkjtySedtvFl48bvpClY4WhQESQ6eha+bYqDuSUb4aHcYV2zA8qf5/XKHD/qG27VghJDB3vIEosyghgsrlxoqUQJBAPg5bavx49IXhmD6v8ClthxAWJncJZW6ES5hVmoScpvgXlk59x9w+Y53OHUiVGrpt3kDMQjeLrNzuT4n4rn7M70CQQDSHtfzGBSQD97jzhL+sBMU9d4m64lDMDz4FokQMbXktUtIHOyK/xES7/jswRdSxX3KfHK6htE1Ww13gFzE2vcnAkAA819bKDsCI/rsxikT2M6gvxJXzIMMVsEYF2FYWKe9txdFajZrFWivRf+eWzpNioWcXgnDCe7N2ySbe1Wf9rlVAkEAih+eY3273qugIG4RzHxgPtIVE86Xb6RhH9Boj02uPUbz0ta4o/Jh/enj33Za551NQHMoU/dVxVYydxDZaspp1wJBAO6/cwJQxcyML3Af1EDRlaTrZF6vl9OwWz1s+e4iQVUGmHcnc+MTakzt1Bylur6AZcZDbNdI9kt+qlyMpGPoDQ8=";
	public static String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDLvQQV14sP2zz4kbiclIhnJ8/xQsGcKGr5dpzKWZLyOKqu0WfAXuX8eS8J5O2o1weO80aTPVlueF6NUo4mtp+cxbCVXV+XAh1khvv/0zLs+X9t8jWJwpzTCrv/cDqFZ9QUoH56eNlJoASh+Ny6cUqDsRHMmFANwODUaqBudyI8ywIDAQAB";

	public static void main(String[] args) throws Exception {
		String msg = "这是机密内容";
		String signed = sign(msg, private_key, "UTF-8"); // 私钥签名
		boolean result = verify(msg, signed, public_key, "UTF-8"); // 公钥认证
		System.out.println(result);
		
	}

}
