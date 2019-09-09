package com.example.springbootcommpent.wx.entity;

import java.io.Serializable;

/**
 * @Author zhouliangze
 * @Description 介绍
 * @Date Create in 2019/9/9 10:38
 **/
public class WxUser implements Serializable {
    private static final long serialVersionUID = 6154019563115778344L;

    /**
     * 开放平台下唯一标识
     */
    private String unionId;

    private String nickName;

    private String headImage;

    private String country;

    private String province;

    private String city;

    private String sex;

    /**
     * 电话号码需要走专门的接口进行获取
     */
    private String phone;

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
