package com.example.springboot.demo.common.utils;

public class StringUtil {

	/**
	 * 验证字符串是否为null或空串
	 * @param str
	 * @return
	 */
	public static boolean validString(String str){
		if(str == null || str.trim().isEmpty()){
			return false;
		}
		return true;
	}
}
