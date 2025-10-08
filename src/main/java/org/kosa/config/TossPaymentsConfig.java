package org.kosa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TossPaymentsConfig {
    @Bean
    public RestTemplate restTemplate() {
        // RestTemplate 객체를 생성하여 스프링 컨테이너에 등록합니다.
        return new RestTemplate();
    }
}
