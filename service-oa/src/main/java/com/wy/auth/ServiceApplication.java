package com.wy.auth;


import com.wy.common.config.MybatisPlus.MybatisPlusConfig;
import com.wy.common.config.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@Import({MybatisPlusConfig.class, GlobalExceptionHandler.class})
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class,args);
    }
}


