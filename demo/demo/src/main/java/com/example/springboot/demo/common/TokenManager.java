package com.example.springboot.demo.common;

public interface TokenManager {

	public String setToken(String key);
	
	public String getToken(String key);
	
	public void removeToken(String key);
}
