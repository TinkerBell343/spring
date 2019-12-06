package com.example.springboot.demo.common;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.springboot.demo.repository.redis.RedisUtil;


@Component
public class RedisTokenManager implements TokenManager {

	@Autowired
	private RedisUtil redisUtil;
	
	@Override
	public String setToken(String key) {
		// TODO Auto-generated method stub
		String token = UUID.randomUUID().toString().replace("-", "");
		redisUtil.set(token, key);
		return token;
	}

	@Override
	public String getToken(String key) {
		// TODO Auto-generated method stub
		if(redisUtil.validateKey(key)){
			return (String) redisUtil.get(key);
		}
		return null;
	}

	@Override
	public void removeToken(String key) {
		// TODO Auto-generated method stub
		if(redisUtil.validateKey(key)){
			redisUtil.deleteKey(key);
		}
	}

	
}
