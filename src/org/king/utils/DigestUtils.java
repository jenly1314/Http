package org.king.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Jenly
 * @date 2014-8-8
 */
public class DigestUtils {
	
	public static final String MD2 = "MD2";
	public static final String MD5 = "MD5";
	public static final String SHA1 = "SHA-1";
	public static final String SHA256 = "SHA-256";
	public static final String SHA384 = "SHA-384";
	public static final String SHA512 = "SHA-512";

	public static String md2(String data) {
		return encrypt(data,MD2);
	}
	
	public static String md5(String data) {
		return encrypt(data,MD5);
	}
	
	public static String sha1(String data) {
		return encrypt(data,SHA1);
	}
	
	public static String sha256(String data) {
		return encrypt(data,SHA256);
	}
	
	public static String sha384(String data) {
		return encrypt(data,SHA384);
	}
	
	public static String sha512(String data) {
		return encrypt(data,SHA512);
	}
	
	public static String encrypt(String data,String algorithm) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			digest.update(data.getBytes());
			return getHashString(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    
    public static String getHashString(byte[] data) {
    	StringBuilder builder = new StringBuilder();
    	for (byte b : data) {
    		builder.append(Integer.toHexString((b >> 4) & 0xF));
    		builder.append(Integer.toHexString(b & 0xF));
    	}
    	return builder.toString();
    }
}
