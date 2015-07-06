/**
 * @Title: AESHelper.java
 * @Package com.xiao4r.util
 * @Description: TODO
 * Copyright: Copyright (c) 2015
 * Company:
 * 
 * @author Comsys-戴智青
 * @date 2015-3-16 下午1:10:30
 * @version V1.0
 */

package com.xiao4r.utils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密工具类，符合在线解密网站:http://www.seacha.com/tools/aes.html 规范
 * @author Comsys-戴智青
 * @date 2015-3-16 下午1:10:30
 *
 */
public class AESHelper {
	/**算法/模式/补码方式**/
	private final static String MODEL = "AES/ECB/PKCS5Padding";
    /**
     * 根据密钥来加密字节数组
     * @param data 需要加密数据   
     * @param key 密匙
     * @return byte[]
     * @Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance(MODEL);
        cipher.init(Cipher.ENCRYPT_MODE, k);
        return cipher.doFinal(data);
    }
    
    /**
     *  获取密钥
     * @return Key
     * @Exception
     */
    public static Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }
    
    /** 字节数组转成16进制字符串 **/
    public static String byte2hex(byte[] b) { // 一个字节的数，
        StringBuffer sb = new StringBuffer(b.length * 2);
        String tmp = "";
        for (int n = 0; n < b.length; n++) {
            // 整数转成十六进制表示
            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString(); // 转成字符串
    }
    
    /**
     * 
     * TODO加密字符串，返回十六进制字符串
     * @return String
     * @Exception
     */
    public static String encrypt(String data , String key) throws Exception {   
    	 byte[] keyByte = key.getBytes();
         byte[] dataByte = data.getBytes();
         byte[] ans = encrypt(dataByte, keyByte);
        return byte2hex(ans);   
    }   
}
