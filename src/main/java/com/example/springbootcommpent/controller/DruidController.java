package com.example.springbootcommpent.controller;

import com.example.springbootcommpent.common.BaseResult;
import com.example.springbootcommpent.service.DruidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zhouliangze
 * @date 2019/9/5 18:17
 */
@RestController
@RequestMapping("/testdruid/")
public class DruidController {

    @Autowired
    private DruidService druidService;

    @RequestMapping(value = "getUserById", method = RequestMethod.GET)
    public BaseResult getUserById(Long id){
        BaseResult baseResult = new BaseResult();
        if(id == null){
            baseResult.failured("id不能为空");
            return baseResult;
        }
        return druidService.getUserById(id);
    }


}
