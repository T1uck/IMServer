package com.xFly.IMServer.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.xFly.IMServer"})
@MapperScan({"com.xFly.IMServer.common.**.mapper"})
public class IMServerCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(IMServerCustomApplication.class,args);
    }

}