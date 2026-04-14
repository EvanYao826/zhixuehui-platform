package com.tianji.ai.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.elasticsearch.ElasticsearchDocumentStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris:http://localhost:9200}")
    private String elasticsearchUris;

    @Value("${spring.elasticsearch.username:}")
    private String username;

    @Value("${spring.elasticsearch.password:}")
    private String password;

    @Value("${spring.elasticsearch.index:ai-embeddings}")
    private String indexName;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        HttpHost[] hosts = elasticsearchUris.split(",")
                .stream()
                .map(HttpHost::create)
                .toArray(HttpHost[]::new);

        RestClient.Builder builder = RestClient.builder(hosts);
        
        if (!username.isEmpty() && !password.isEmpty()) {
            builder.setHttpClientConfigCallback(httpClientBuilder -> 
                httpClientBuilder.setDefaultCredentialsProvider(
                    new org.apache.http.impl.client.BasicCredentialsProvider() {
                        {
                            setCredentials(
                                org.apache.http.auth.AuthScope.ANY,
                                new org.apache.http.auth.UsernamePasswordCredentials(username, password)
                            );
                        }
                    }
                )
            );
        }

        return new RestHighLevelClient(builder);
    }

    @Bean
    public ElasticsearchDocumentStore elasticsearchDocumentStore(
            RestHighLevelClient restHighLevelClient,
            EmbeddingClient embeddingClient) {
        return new ElasticsearchDocumentStore(
                restHighLevelClient,
                embeddingClient,
                indexName
        );
    }
}