﻿﻿﻿package com.zbkj.admin.config;

import com.zbkj.common.constants.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 配置组件（替代 SpringFox Swagger）
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port}")
    private String port;

    @Value("${crmeb.domain}")
    private String domain;

    /**
     * 配置 OpenAPI 基本信息和安全认证
     * 访问地址：http://localhost:{port}/swagger-ui.html
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = Constants.HEADER_AUTHORIZATION_KEY;
        
        return new OpenAPI()
                .info(new Info()
                        .title("Crmeb Java")
                        .description("Crmeb")
                        .version("1.0.0")
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
