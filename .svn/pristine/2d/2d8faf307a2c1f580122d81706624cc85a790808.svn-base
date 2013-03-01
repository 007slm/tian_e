/**
 *
 */
package com.orange.browser;

import android.util.Log;

import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



/**
 *
 * Revision History:
 *   <<Date>>           <<Who>>      <<What>>
 *
 */

/**
 * @author GiterLiu
 *
 */
public class AESUtil {
	private static String	      TAG	          = "AESUtil";
	private static String	      AESKey	      = "linxunpassword-anti-attackable!!";
	private static String	      defaultUserId	= "linxun0000000000";
	private static byte[]	      key	          = AESKey.getBytes();
	private final static String	HEX	          = "0123456789ABCDEF";
	private static String	      ENCODING	    = "UTF-8";



	public static String encrypt(String cleartext) throws Exception {
	    cleartext = getPaddingContent(cleartext);
	    byte[] encryptStr = encrypt(key, cleartext.getBytes(), defaultUserId);
	    return (toHex(encryptStr));
	}


	// UniSys AES Encoding
	public static String encrypt(String cleartext, String ivs) throws Exception {
	    byte[] encryptStr = encrypt(key, cleartext.getBytes(ENCODING), ivs);
		return (toHex(encryptStr));
	}

	// UniSys AES Decoding
	public static String decrypt(String encrypted, String ivs) throws Exception {
		byte[] enc = toByte(encrypted);
		byte[] result;
		String resultStr = "";
		result = decryptNoPadding(key, enc, ivs);
		resultStr = new String(result, ENCODING);

		return resultStr;
	}

	public static String decrypt2(String seed, String encrypted) throws Exception {
		byte[] rawKey = getRawKey(seed.getBytes());

		byte[] enc = toByte(encrypted);
		byte[] result = decrypt(rawKey, enc);
		return new String(result);
	}

	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed);
		kgen.init(128, sr); // 192 and 256 bits may not be available
		SecretKey skey = kgen.generateKey();

		byte[] raw = skey.getEncoded();
		return raw;
	}

	private static byte[] encrypt(byte[] key, byte[] cleartext, String ivs) throws Exception {

		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		// Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		// Read the IV from the file. It's the first 16 bytes.
		byte[] iv = ivs.getBytes();

		IvParameterSpec spec = new IvParameterSpec(iv);

		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, spec);
		byte[] encrypted = cipher.doFinal(cleartext);
		return encrypted;
	}

	private static byte[] decryptNoPadding(byte[] raw, byte[] encrypted, String ivs) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		// Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		// Read the IV from the file. It's the first 16 bytes.
		byte[] iv = new byte[16];
		iv = ivs.getBytes();
		IvParameterSpec spec = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, spec);

		// cipher.init(Cipher.DECRYPT_MODE, skeySpec);;
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
		Log.e("1", "decrypt2");

		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		// Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		// cipher.init(Cipher.DECRYPT_MODE, skeySpec);

		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		// Read the IV from the file. It's the first 16 bytes.
		String ivs = "linxun0000000000";
		byte[] iv = new byte[16];

		iv = ivs.getBytes();
		IvParameterSpec spec = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, spec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	public static String toHex(String txt) {
		return toHex(txt.getBytes());
	}

	public static String fromHex(String hex) {
		return new String(toByte(hex));
	}

	public static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for(int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }
		return result;
	}

	public static String toHex(byte[] buf) {
		if(buf == null) {
            return "";
        }
		StringBuffer result = new StringBuffer(2 * buf.length);
		for(int i = 0; i < buf.length; i++){
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	public static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for(int i = 0; i < b.length; i++){
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}

	public static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;

		for(i = 0; i < buf.length; i++){
			if((buf[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }
			strbuf.append(Long.toString(buf[i] & 0xff, 16));
		}

		return strbuf.toString();
	}

	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}

	public static String getPaddingUserId(String userId) {
		StringBuffer sb = null;
		if(userId != null && !userId.equals("")){
			sb = new StringBuffer(userId);
			while (sb.length() < 16){
				sb.append("0");
			}
			return sb.toString();
		} else{
			return defaultUserId;
		}

	}

	public static int getRandomNumber() {
		Random rnd = new Random();
		int rezult = rnd.nextInt(100000000);
		if(rezult > 10000000){
			return rezult;
		} else{
			return getRandomNumber();
		}
	}

	public static String getPaddingContent(String content) {
		StringBuffer sb = null;
		if(content != null && !content.equals("")){
			sb = new StringBuffer(content);
			if(content.length() <= 16){
				while (sb.length() < 16){
					sb.append(" ");
				}
			} else if(content.length() <= 32){
				while (sb.length() < 32){
					sb.append(" ");
				}
			} else{
				// error...
				return null;
			}
			return sb.toString();
		} else{
			return null;
		}

	}

	/**
	 * This function is used to encrypt the content.
	 *
	 * @param randomeNum
	 * @param protocolFuncName
	 * @param userId
	 * @return
	 */
	public static String getEncryptValue(int randomeNum, String protocolFuncName, String userId) {
		String new_string = "";
		new_string = randomeNum + "," + protocolFuncName;
		new_string = getPaddingContent(new_string);
		try{
			return encrypt(new_string, getPaddingUserId(userId));
		} catch (Exception e){
			Log.e(TAG, e.getMessage());
			return null;
		}
	}

	public static String getEncryptValue(String imsi) {
	        try{
	            return encrypt(imsi);
	        } catch (Exception e){
            // fix bug only in sony yuga ,the e.getMessage() is null,which cause app
            // crash
            if (null != e.getMessage()) {
                Log.e(TAG, e.getMessage());
            }
	            return null;
	        }
	}

	public static String getDecryptValue(String encryptContent, String userId) {
		try{
			return decrypt(encryptContent, getPaddingUserId(userId));
		} catch (Exception e){
			Log.e(TAG, e.getMessage());
			return null;
		}
	}


	/**
	 * Demonstrate how to encrypt the content.<br>
	 * NOTE:if the request is the second one, the random number should be increased by 1
	 * :randomNum=rondomNum+1;
	 */
	public static String example_encrypt() {
		int randomNum = -1;
		String protocolFuncName = "startClient";
		String userId = "";
		randomNum = getRandomNumber();
		return getEncryptValue(randomNum, protocolFuncName, userId);
	}

	/**
	 * Demonstrate how to decrypt the content.
	 */
	public static String example_decrypt(String encryptedContent, String userId) {
		return getDecryptValue(encryptedContent, userId);
	}
}
