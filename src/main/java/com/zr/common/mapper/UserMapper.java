package com.zr.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.zr.common.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User>selectAllUser(User user);

    User  selectUserByName(@Param("name")String name);


}
