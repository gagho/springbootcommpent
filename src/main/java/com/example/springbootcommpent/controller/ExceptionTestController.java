package com.example.springbootcommpent.controller;

import com.example.springbootcommpent.common.BaseResult;
import com.example.springbootcommpent.exceptioncommpent.exception.CustomException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouliangze
 * @date 2019/8/20 17:32
 */
@RestController
@RequestMapping("/")
public class ExceptionTestController {


    @RequestMapping("exceptiontest")
    public BaseResult exceptionTest(Integer num){
        BaseResult baseResult = new BaseResult();
        if (num == null) {
            throw new CustomException(400, "num不能为空");
        }
        int i = 10 / num;
        baseResult.setData(i);
        return baseResult;
    }
}
