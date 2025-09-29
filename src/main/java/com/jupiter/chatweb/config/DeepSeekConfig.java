package com.jupiter.chatweb.config;

import lombok.Data;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "deepseek")
@Data
public class DeepSeekConfig {
    private String apiKey;
    private String baseUrl;
    private String model;

    @Bean
    public RestTemplate restTemplate() {
        // 配置连接池（需引入 httpclient 依赖）
        PoolingHttpClientConnectionManager connManager =
                new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(100); // 最大连接数
        connManager.setDefaultMaxPerRoute(50); // 每个路由最大连接数

        // 配置超时
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(30000)    // 连接超时30秒
                .setSocketTimeout(60000)     // 数据读取超时30秒
                .build();

        // 创建 HttpClient
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        // 构建 RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(
                new HttpComponentsClientHttpRequestFactory(httpClient)
        );

        return restTemplate;
    }
}