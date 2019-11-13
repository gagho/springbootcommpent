package com.example.springbootcommpent.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.springbootcommpent.annotation.PassToken;
import com.example.springbootcommpent.annotation.UserLoginToken;
import com.example.springbootcommpent.common.BaseResult;
import com.example.springbootcommpent.exceptioncommpent.exception.CustomException;
import com.example.springbootcommpent.jwt.entity.User;
import com.example.springbootcommpent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 验证token的拦截器
 * @author zhouliangze
 * @date 2019/11/11 16:06
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从头部取出token
        String token = request.getHeader("token");

        //开始逻辑
        //非映射方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        //校验注解
        if(method.isAnnotationPresent(PassToken.class)){
            PassToken passToken = method.getAnnotation(PassToken.class);
            if(passToken.required()){
                return true;
            }
        }

        if(method.isAnnotationPresent(UserLoginToken.class)){
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if(userLoginToken.required()){
                if(token == null){
                    throw new CustomException(999, "用户未登陆");
                }

                String userId;
                try {
                    userId = JWT.decode(token).getAudience().get(0);
                }catch (Exception e){
                    throw new RuntimeException("401");
                }
                BaseResult<User> userBaseResult = userService.getUserById(Long.parseLong(userId));
                if(userBaseResult.isSuccess()){
                    if(userBaseResult.getData() == null){
                        throw new CustomException(1, "用户不存在");
                    }

                    JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(userBaseResult.getData().getPassword())).build();
                    try {
                        jwtVerifier.verify(token);
                    }catch (Exception e){
                        throw new RuntimeException("401");
                    }
                }else {
                    throw new CustomException(userBaseResult.getCode(), userBaseResult.getMsg());
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
