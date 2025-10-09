package org.kosa.config;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Tag(name = "Shop", description = "농산물 거래 관련 API")  // Swagger UI에서 보여질 태그
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Farm To Table API")  // API 제목
                        .description("농산물 직거래를 위한 REST API")  // API 설명
                        .version("1.0.0")  // API 버전
                        .contact(new Contact()  // 연락처 정보
                                .name("김선호, 이민호, 우승환")
                                .email("srrsr14@gmail.com")));
    }

}
