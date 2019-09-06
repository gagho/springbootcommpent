package com.example.springbootcommpent.service;

import com.example.springbootcommpent.common.BaseResult;
import com.example.springbootcommpent.dao.read.PostBlackUserReaderDao;
import com.example.springbootcommpent.entity.PostBlackUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhouliangze
 * @date 2019/9/5 18:20
 */
@Service
public class DruidService {

    private Logger logger = LoggerFactory.getLogger(DruidService.class);

    @Autowired
    private PostBlackUserReaderDao postBlackUserReaderDao;

    public BaseResult getUserById(Long id){
        BaseResult baseResult = new BaseResult();
        try {
            PostBlackUser postBlackUser = postBlackUserReaderDao.selectByPrimaryKey(id);
            baseResult.setData(postBlackUser);
        }catch (Exception e){
            baseResult.exceptioned("查询异常");
            logger.error("查询异常{}", e);
        }
        return baseResult;
    }
}
