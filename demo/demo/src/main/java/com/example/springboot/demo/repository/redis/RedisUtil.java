package com.example.springboot.demo.repository.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class RedisUtil {

	/**
	 * Redis工具类
	 */
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	/**
	 * 指定键值对过期时间	
	 * @param key
	 * @param time
	 * @return
	 */
	public boolean expire(String key,long time){
		try {
			if(time > 0){
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 验证键值对是否存在
	 * @param key
	 * @return
	 */
	public boolean validateKey(String key){
		try {
			return redisTemplate.hasKey(key);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 移除键值对
	 * @param keys
	 * @return
	 */
	public boolean deleteKey(String...keys){
		try {
			redisTemplate.delete(CollectionUtils.arrayToList(keys));
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	
	public Object get(String key){
		return key == null ? null : redisTemplate.opsForValue().get(key); 
	}
	
	public boolean set(String key,Object value){
		try {
			redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	
}
