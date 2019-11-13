package com.example.springbootcommpent.service;

import com.example.springbootcommpent.common.BaseResult;
import com.example.springbootcommpent.jwt.entity.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouliangze
 * @date 2019/11/11 15:52
 */
@Service
public class UserService {
    private static Map<Long, User> USER = new HashMap();
    static {
        User user = new User(1L, "张三", "123456");
        User user1 = new User(2L, "李四", "123456");
        User user2 = new User(3L, "王五", "123456");
        User user3 = new User(4L, "狗蛋", "123456");
        User user4 = new User(5L, "二愣子", "123456");
        USER.put(user.getId(), user);
        USER.put(user1.getId(), user1);
        USER.put(user2.getId(), user2);
        USER.put(user3.getId(), user3);
        USER.put(user4.getId(), user4);
    }

    public BaseResult<User> getUserById(Long id){
        BaseResult<User> baseResult = new BaseResult();
        baseResult.setData(USER.get(id));
        return baseResult;
    }

    public BaseResult<User> getUserForLogin(String userName, String password){
        BaseResult<User> baseResult = new BaseResult<>();
        USER.forEach((key, value) ->{
            if(value.getUserName().equals(userName) && value.getPassword().equals(password)){
                baseResult.setData(value);
                return;
            }
        });
        return baseResult;
    }
}
