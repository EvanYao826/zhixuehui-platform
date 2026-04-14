package com.tianji.ai.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder; // 1. 添加此导入
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris:http://localhost:9200}")
    private String elasticsearchUris;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        String[] uriArray = elasticsearchUris.split(",");
        HttpHost[] hosts = new HttpHost[uriArray.length];
        for (int i = 0; i < uriArray.length; i++) {
            // 注意：HttpHost.create 是 ES 7.x+ 的方法，如果是旧版本可能需要 new HttpHost(...)
            hosts[i] = HttpHost.create(uriArray[i].trim()); // 建议 trim 去除空格
        }

        // 2. 修改类型为 RestClientBuilder
        RestClientBuilder builder = RestClient.builder(hosts);

        return new RestHighLevelClient(builder);
    }
}
