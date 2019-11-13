package com.example.springbootcommpent.jwt.entity;

import java.io.Serializable;

/**
 * @author zhouliangze
 * @date 2019/11/11 15:49
 */
public class User implements Serializable {

    private static final long serialVersionUID = 3472090085744723320L;
    private Long id;

    private String userName;

    private String password;

    public User(Long id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
