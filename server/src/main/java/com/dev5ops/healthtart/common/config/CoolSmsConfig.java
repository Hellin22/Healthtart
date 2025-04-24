package com.dev5ops.healthtart.common.config;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoolSmsConfig {

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.api.base-url}")
    private String baseUrl;

    @Bean
    public DefaultMessageService defaultMessageService() {
        // yml에서 불러온 API 키와 시크릿으로 DefaultMessageService 초기화
        return NurigoApp.INSTANCE.initialize(apiKey, apiSecret, baseUrl);
    }
}
