package com.zr.common.service;
import com.zr.common.entity.User;
import com.zr.common.mapper.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public List<User> selectByUsetAll(User user){
       return  userMapper.selectAllUser(user);
    }

    public User selectUserByName(String name){
        return userMapper.selectUserByName(name);
    }

}
