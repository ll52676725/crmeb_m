package com.zbkj.common.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * Elasticsearch配置类
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
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Value("${spring.elasticsearch.rest.uris}")
    private String uris;

    @Value("${spring.elasticsearch.rest.connection-timeout}")
    private String connectionTimeout;

    @Value("${spring.elasticsearch.rest.read-timeout}")
    private String readTimeout;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        // 解析Elasticsearch地址
        String[] uriArray = uris.split(",");
        HttpHost[] httpHosts = new HttpHost[uriArray.length];
        for (int i = 0; i < uriArray.length; i++) {
            String uri = uriArray[i].trim();
            httpHosts[i] = HttpHost.create(uri);
        }

        return new RestHighLevelClient(
                RestClient.builder(httpHosts)
                        .setRequestConfigCallback(requestConfigBuilder -> {
                            // 设置连接超时和读取超时
                            int connectTimeout = Integer.parseInt(connectionTimeout.replace("ms", ""));
                            int socketTimeout = Integer.parseInt(readTimeout.replace("ms", ""));
                            requestConfigBuilder.setConnectTimeout(connectTimeout);
                            requestConfigBuilder.setSocketTimeout(socketTimeout);
                            return requestConfigBuilder;
                        })
        );
    }
}
