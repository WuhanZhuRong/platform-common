package com.zr.common.controller;
import com.zr.common.entity.User;
import com.zr.common.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Promote User Manager")
@RequestMapping("/api/v1/study/UserInfo")
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @ApiOperation(value = "UseAll search")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "成功返回 JSON 格式的公共返回类", responseContainer = "List")})
    @GetMapping(value = "selectList")
    public Object getAllUser(User user ){
        return userService.selectByUsetAll(user);
    }

    @ApiOperation(value = "User By Name search")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "成功返回 JSON 格式的公共返回类", responseContainer = "List")})
    @GetMapping(value = "select")
    public Object getUser(String name){
        return  userService.selectUserByName(name);
    }

    @ApiOperation(value = "User Login")
    @ApiResponses(value = {@ApiResponse(code = 200,message = "成功返回 JSON 格式的公共返回类",responseContainer = "int")})
    @PostMapping(value = "login")
    public Object login(){
        return "login";
    }
}
