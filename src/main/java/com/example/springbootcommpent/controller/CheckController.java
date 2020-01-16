package com.example.springbootcommpent.controller;

import com.example.springbootcommpent.common.BaseResult;
import com.example.springbootcommpent.entity.TestCheck;
import com.example.springbootcommpent.paramcheck.Check;
import com.example.springbootcommpent.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhouliangze
 * @date 2020/1/15 17:26
 */
@RestController
@RequestMapping("/testCheck/")
public class CheckController {

    @Autowired
    private CheckService checkService;

    @RequestMapping("test")
    @Check({"TestCheck.name not null:姓名不能为空", "age>3"})
    public BaseResult testCheck(@RequestBody TestCheck testCheck, HttpServletRequest request){
        return checkService.testCheck(testCheck);
    }
}
