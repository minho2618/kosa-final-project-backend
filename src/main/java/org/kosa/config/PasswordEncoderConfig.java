package org.kosa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration // Spring 설정 파일임을 명시
public class PasswordEncoderConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        // 이제 SecurityConfig와 완전히 독립적으로 생성되어 주입 가능합니다.
        return new BCryptPasswordEncoder();
    }
}