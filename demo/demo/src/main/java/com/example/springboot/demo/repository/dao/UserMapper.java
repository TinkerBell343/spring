package com.example.springboot.demo.repository.dao;

import org.apache.ibatis.annotations.Mapper;

import com.example.springboot.demo.repository.entity.User;


@Mapper
public interface UserMapper {

	public User selectByPrimaryKey(int id);
	
	public int insert(User user);
	
	public int deleteByPrimaryKey(int id);
}
