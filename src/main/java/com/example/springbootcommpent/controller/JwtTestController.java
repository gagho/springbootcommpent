package com.example.springbootcommpent.controller;

import com.example.springbootcommpent.annotation.PassToken;
import com.example.springbootcommpent.annotation.UserLoginToken;
import com.example.springbootcommpent.common.BaseResult;
import com.example.springbootcommpent.jwt.entity.User;
import com.example.springbootcommpent.service.TokenService;
import com.example.springbootcommpent.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouliangze
 * @date 2019/11/13 11:18
 */
@RestController
@RequestMapping("/jwttest/")
public class JwtTestController {

    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    @PassToken
    @RequestMapping("login")
    public BaseResult login(@RequestBody User user){
        BaseResult baseResult = new BaseResult();
        if(StringUtils.isBlank(user.getUserName()) || StringUtils.isBlank(user.getPassword())){
            baseResult.failured("入参异常");
            return baseResult;
        }
        BaseResult<User> userBaseResult = userService.getUserForLogin(user.getUserName(), user.getPassword());
        if(userBaseResult.getData() == null){
            baseResult.failured("账户或密码错误");
        }else{
            String token = tokenService.getToken(userBaseResult.getData());
            baseResult.setData(token);
        }
        return baseResult;
    }

    @RequestMapping("jwttest")
    @UserLoginToken
    public BaseResult jwtTest(){
        BaseResult baseResult = new BaseResult();
        baseResult.setMsg("请求成功");
        return baseResult;
    }
}
