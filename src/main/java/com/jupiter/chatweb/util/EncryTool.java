package com.jupiter.chatweb.util;

import org.springframework.util.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class EncryTool {
    public static void main(String[] args) {
        System.out.println(getDoubleMd5("A2136637qaz"));
    }
    public static String getDoubleMd5(String str){
        String md5str="";
        try {
            md5str = DigestUtils.md5DigestAsHex(md5str.getBytes("utf-8"));
            md5str = DigestUtils.md5DigestAsHex(md5str.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return md5str;
    }

    public static String decrypt(String encryptedBase64Str, String key) throws Exception {
        String val = encryptedBase64Str.replace("-", "+").replace("_", "/");
        byte[] encryptedBytes = Base64.getDecoder().decode(val);
        byte[] keyBytes = key.getBytes("UTF-8");

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, "UTF-8");
    }
}
