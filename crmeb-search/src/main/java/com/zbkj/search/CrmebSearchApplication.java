package com.zbkj.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * CRMEB智能搜索模块启动类
 * @author CRMEB
 * @since 2024-05-20
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.zbkj.search",
        "com.zbkj.common",
        "com.zbkj.service"
})
public class CrmebSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmebSearchApplication.class, args);
    }

}