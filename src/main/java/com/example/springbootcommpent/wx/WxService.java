package com.example.springbootcommpent.wx;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.springbootcommpent.common.BaseResult;
import com.example.springbootcommpent.constants.RedisConstants;
import com.example.springbootcommpent.wx.entity.WxUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhouliangze
 * @Description 微信服务
 * @Date Create in 2019/9/6 15:14
 **/
@Service
public class WxService {

    public Logger logger = LoggerFactory.getLogger(WxService.class);

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 建议appid与secret存入数据库，需要授权时进行查询
     */
    private static String appid = "";

    private static String secret = "";

    private static boolean initialized = false;

    /**
     * 获取微信accessToken
     * @return
     */
    public String getAccessToken(){
        String token = null;
        //查询缓存中是否存在，不存在则去微信中获取
        if(redisTemplate.opsForValue().get(RedisConstants.WX_TOKEN + appid) != null){
            return redisTemplate.opsForValue().get(RedisConstants.WX_TOKEN + appid).toString();
        }else{
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
            url = url.replace("APPID", appid).replace("APPSECRET", secret);
            String result = HttpUtil.get(url);
            JSONObject jsonObject = JSON.parseObject(result);
            token = jsonObject.getString("access_token");
            redisTemplate.opsForValue().set(RedisConstants.WX_TOKEN + appid, token, 7000L);
        }
        return token;
    }

    /**
     * 获取openId和sessionKey
     * @param code 微信登录时提供的code
     * @return
     */
    public BaseResult<Map> getOpenIdAndSessionKey(String code){
        BaseResult<Map> baseResult = new BaseResult();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";
        url = url.replace("APPID", appid).replace("SECRET", secret).replace("JSCODE", code);
        String openResult = HttpUtil.get(url);
        if(StringUtils.isBlank(openResult)){
            logger.error("未获取到微信授权信息");
            baseResult.failured("未获取到微信授权信息");
            return baseResult;
        }

        JSONObject result = JSONObject.parseObject(openResult);
        if(StrUtil.isNotBlank(result.getString("errcode"))){
            logger.error("授权异常:{}", openResult);
            baseResult.failured("授权异常微信返回错误码:" + result.getString("errcode"));
            return baseResult;
        }

        Map resultMap = new HashMap<>();
        resultMap.put("openid", result.getString("openid"));
        resultMap.put("sessionKey", result.getString("session_key"));
        baseResult.setData(resultMap);
        return baseResult;
    }

    /**
     * 解密获取微信用户基础数据
     * @param sessionKey
     * @param iv
     * @param encryptedData
     * @return
     */
    public BaseResult<WxUser> getUserInfoFromWx(String sessionKey, String iv, String encryptedData){
        BaseResult<WxUser> baseResult = new BaseResult();
        if (StrUtil.isBlank(encryptedData) || StrUtil.isBlank(iv)) {
            logger.warn("入参异常,encryptedData={},iv={}", encryptedData, iv);
            baseResult.failured("入参异常");
            return baseResult;
        }
        String plainJson = decrypt(encryptedData, sessionKey, iv);
        if (plainJson == null) {
            logger.warn("sequence:{}, 用户授权信息解码错误,encryptedData={},iv={},sessionKey={}", encryptedData, iv, sessionKey);
            baseResult.failured("用户授权错误");
        }
        JSONObject json = JSON.parseObject(plainJson);
        WxUser wxUser = new WxUser();
        wxUser.setUnionId(json.getString("unionId"));
        wxUser.setNickName(json.getString("nickName"));
        wxUser.setHeadImage(json.getString("avatarUrl"));
        wxUser.setCountry(json.getString("country"));
        wxUser.setProvince(json.getString("province"));
        wxUser.setCity(json.getString("city"));
        baseResult.setData(wxUser);
        return baseResult;
    }

    /**
     * 获取微信用户手机号码 需单独走授权
     * @param encryptedData
     * @param iv
     * @param code
     * @return
     */
    public String getUserPhone(String encryptedData, String iv, String code){
        String phone = "";
        try {
            String wx_url = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";
            wx_url = wx_url.replace("APPID", appid)
                    .replace("SECRET", secret).replace("JSCODE", code);
            String openResult = HttpUtil.get(wx_url);
            if(StrUtil.isBlank(openResult)){
                logger.error("获取不到微信的授权信息");
                return null;
            }

            JSONObject result = JSONObject.parseObject(openResult);
            if(StrUtil.isNotBlank(result.getString("errcode"))){
                logger.error("授权异常:{}", openResult);
                return null;
            }
            String sessionKey = result.getString("session_key");

            byte[] dataByte = Base64.decodeBase64(encryptedData);
            byte[] keyByte = Base64.decodeBase64(sessionKey);
            byte[] ivByte = Base64.decodeBase64(iv);

            AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivByte);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            String phoneResult = new String(cipher.doFinal(dataByte), "UTF-8");

            if (StrUtil.isNotBlank(phoneResult)) {
                JSONObject jsonObject = JSONObject.parseObject(phoneResult);
                phone = jsonObject.getString("phoneNumber");
            }else {
                logger.error("手机号码解密失败");
                return null;
            }

        }catch (Exception e){
            logger.error("获取手机号码异常 ", e);
            return null;
        }

        return phone;

    }

    private String decrypt(String encryptedData, String sessionKey, String iv) {
        try {
            byte[] plain = decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(sessionKey),
                    Base64.decodeBase64(iv));
            return new String(plain);
        } catch (Exception e) {
            logger.error("解密出错 ", e);
            return null;
        }
    }

    private byte[] decrypt(byte[] content, byte[] keyByte, byte[] ivByte) throws Exception {
        initialize();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        Key sKeySpec = new SecretKeySpec(keyByte, "AES");
        // 初始化
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivByte));
        byte[] result = cipher.doFinal(content);
        return result;
    }

    private void initialize() {
        if (initialized) {
            return;
        }
        Security.addProvider(new BouncyCastleProvider());
        initialized = true;
    }

    private AlgorithmParameters generateIV(byte[] iv) throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(iv));
        return params;
    }


}
