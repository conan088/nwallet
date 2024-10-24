package com.bx.implatform.util;

import java.util.Date ;
import java.util.UUID;

public class IDUtil {

	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	/**
	 * 获取随机6位数
	 */
	public static String getSixRandNum(){
		int num = (int) (Math.random() * 900000) + 100000;  
		return num+"";
	}
	/**
	 * 获取随机4位数
	 */
	public static String getFourRandNum(){
		int num = (int) (Math.random() * 9000) + 1000;  
		return num+"";
	}
	/**
	 * 获取随机字母串
	 */
	public static String getRandomChars(int length) {
        String string = "";
        for (int i = 0; i < length; i++) {
            char c = (char) ((Math.random() * 26) + 97);
            string += (c + "");
        }
        return string;
    }
	/**
	 * 获取随机不重复8位
	 */
	public static String get8Hash() {
        return Integer.toHexString((int)new Date().getTime()).toUpperCase();
    }
	/**
	 * 获取时间戳订单号
	 */
	
}
