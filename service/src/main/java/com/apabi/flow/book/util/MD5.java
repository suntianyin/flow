package com.apabi.flow.book.util;

import org.apache.http.protocol.HTTP;

import java.security.MessageDigest;

/**
 * @author guanpp
 * @date 2018/8/2 13:58
 * @description
 */
public class MD5 {

    public String str;

    public String md5s(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes(HTTP.UTF_8));
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            str = buf.toString();
//			system.out.println("result: " + buf.toString());// 32位的加密
//			system.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return str;
    }
}
