package com.zbkj.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zbkj"})
public class CrmebAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmebAiApplication.class, args);
    }

}