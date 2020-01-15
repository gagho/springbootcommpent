package com.example.springbootcommpent.service;

import com.example.springbootcommpent.common.BaseResult;
import com.example.springbootcommpent.entity.TestCheck;
import org.springframework.stereotype.Service;

/**
 * @author zhouliangze
 * @date 2020/1/15 17:38
 */
@Service
public class CheckService {

    public BaseResult testCheck(TestCheck testCheck){
        BaseResult baseResult = new BaseResult();
        baseResult.setData(testCheck);
        return baseResult;
    }
}
