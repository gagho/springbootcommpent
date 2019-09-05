package com.example.springbootcommpent.controller;

import com.example.springbootcommpent.annotation.CacheLock;
import com.example.springbootcommpent.annotation.CacheParam;
import com.example.springbootcommpent.common.BaseResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhouliangze
 * @date 2019/9/4 16:47
 */
@RestController
@RequestMapping("/resubmit/")
public class ResubmitController {

    @CacheLock(prefix = "resubmit")
    @RequestMapping(value = "query", method = RequestMethod.GET)
    public BaseResult query(@CacheParam(name = "token") @RequestParam String token){
        BaseResult baseResult = new BaseResult();
        baseResult.setData("success - " + token );
        return baseResult;
    }
}
