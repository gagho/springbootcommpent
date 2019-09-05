package com.example.springbootcommpent;

import com.example.springbootcommpent.resubmit.CacheKeyGenerator;
import com.example.springbootcommpent.resubmit.LockKeyGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringbootcommpentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootcommpentApplication.class, args);
    }

    @Bean
    public CacheKeyGenerator cacheKeyGenerator(){
        return new LockKeyGenerator();
    }

}
