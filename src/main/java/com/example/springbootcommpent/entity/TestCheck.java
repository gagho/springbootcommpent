package com.example.springbootcommpent.entity;

import java.io.Serializable;

/**
 * @author zhouliangze
 * @date 2020/1/15 17:27
 */
public class TestCheck implements Serializable {

    private static final long serialVersionUID = -2778807002735592184L;
    private String name;

    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
