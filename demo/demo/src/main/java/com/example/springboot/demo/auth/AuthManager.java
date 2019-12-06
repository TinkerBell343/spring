package com.example.springboot.demo.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.springboot.demo.common.TokenManager;

@Component
public class AuthManager {
	/**
	 * 权限管理类
	 */
	@Autowired
	private TokenManager tm;
	/**
	 * 获取请求体
	 * @return
	 */
	public HttpServletRequest getRequest(){
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
	/**
	 * 授权登陆并返回token，此处user代表用户标识
	 * @param user
	 * @return
	 */
	public String Authority(String user){
		String token = tm.setToken(user);
		return token;
	}
	
	public String getUser(){
		HttpServletRequest request=getRequest();
		String token=request.getParameter("token").toString();
		return tm.getToken(token);
	}

	public void logout(){
		HttpServletRequest request=getRequest();
		String token=request.getParameter("token").toString();
		tm.removeToken(token);
	}
}
