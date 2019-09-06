package com.example.springbootcommpent;

import com.example.springbootcommpent.resubmit.CacheKeyGenerator;
import com.example.springbootcommpent.resubmit.LockKeyGenerator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.example.springbootcommpent.dao")
public class SpringbootcommpentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootcommpentApplication.class, args);
    }

    @Bean
    public CacheKeyGenerator cacheKeyGenerator(){
        return new LockKeyGenerator();
    }

}
