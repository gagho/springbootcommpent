package com.example.springbootcommpent.wx.scheduled;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.springbootcommpent.constants.RedisConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author zhouliangze
 * @Description 定时任务刷新token
 * @Date Create in 2019/9/9 9:52
 **/
@Component
public class AccessTokenScheduled {
    private Logger logger = LoggerFactory.getLogger(AccessTokenScheduled.class);

    @Autowired
    private RedisTemplate redisTemplate;


    private static String appid = "";

    private static String secret = "";

    /**
     * 定时任务刷新token，服务启动时刷新token，后续每7000秒刷新一次
     */
    @Scheduled(initialDelay=0, fixedRate=7000000)
    public void flushToken(){
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        url = url.replace("APPID", appid).replace("APPSECRET", secret);
        String result = HttpUtil.get(url);
        JSONObject jsonObject = JSON.parseObject(result);
        String token = jsonObject.getString("access_token");
        redisTemplate.opsForValue().set(RedisConstants.WX_TOKEN + appid, token, 7000L);
        logger.info("刷新token{}", token);
    }
}
