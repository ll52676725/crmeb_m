package com.zbkj.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * CRMEB 智能搜索模块启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.zbkj")
public class CrmebSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmebSearchApplication.class, args);
    }

}