package com.example.springbootcommpent.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.springbootcommpent.jwt.entity.User;
import org.springframework.stereotype.Service;

/**
 * @author zhouliangze
 * @date 2019/11/13 11:20
 */
@Service
public class TokenService {

    public String getToken(User user){
        String token = JWT.create().withAudience(user.getId().toString()).sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }
}
