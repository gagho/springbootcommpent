package com.example.springbootcommpent.dao.read;

import com.example.springbootcommpent.entity.PostBlackUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface PostBlackUserReaderDao {
    PostBlackUser selectByPrimaryKey(Long id);
}