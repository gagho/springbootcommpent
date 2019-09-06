package com.example.springbootcommpent.dao.write;

import com.example.springbootcommpent.entity.PostBlackUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface PostBlackUserWriterDao {
    int deleteByPrimaryKey(Long id);

    int insert(PostBlackUser record);

    int insertSelective(PostBlackUser record);

    int updateByPrimaryKeySelective(PostBlackUser record);

    int updateByPrimaryKey(PostBlackUser record);
}