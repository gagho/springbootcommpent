package com.example.springbootcommpent.entity;

import java.io.Serializable;

public class PostBlackUser implements Serializable {
    /** 用户id */
    private Long id;

    /** 拉黑userId */
    private Long userId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}