package com.yibo.miaosha.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

    private static final String SALT = "1a2b3c4d";

    private static String inputPassToFormPass(String inputPass) {
        String str = "" + SALT.charAt(0) + SALT.charAt(2) + inputPass + SALT.charAt(5) + SALT.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDbPass(String inputPass, String dbSalt) {
        String formPass = inputPassToFormPass(inputPass);
        return formPassToDBPass(formPass, dbSalt);
    }

    public static String md5(String src) {
        String encodeString = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(src.getBytes(StandardCharsets.UTF_8));
            encodeString = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodeString;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String t;
        for (byte b : bytes) {
            t = Integer.toHexString(b & 0xFF);
            if (t.length() == 1) {//1得到一位的进行补0操作
                sb.append("0");
            }
            sb.append(t);
        }
        return sb.toString();
    }
}