package org.kosa.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.handler.OAuth2LoginSuccessHandler;
import org.kosa.jwt.JWTFilter;
import org.kosa.jwt.JWTUtil;
import org.kosa.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("SecurityFilterChain ==============>");
        http.csrf(auth->auth.disable());
        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());
        http.formLogin(auth->auth.disable());
        http.httpBasic(auth->auth.disable());

        // OAuth2 로그인 설정 추가
        http.oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(endpoint -> endpoint.baseUri("/oauth2/authorization")) // OAuth2 로그인 시작 URL
                .redirectionEndpoint(endpoint -> endpoint.baseUri("/login/oauth2/code/*")) // OAuth2 콜백 URL
                .successHandler(oAuth2LoginSuccessHandler) // 로그인 성공 시 JWT를 발급할 커스텀 핸들러
        );

        // API 권한 설정 (기존과 동일)
        http.authorizeHttpRequests(auth->auth
                .requestMatchers("/api/members","/api/sellers").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/api/gemini/**").permitAll()          // <-- 여길 열어줘야 함
                // .requestMatchers("/**").permitAll() // ToDo: 테스트용 삭제할 것
                .anyRequest().authenticated());

        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 기존 JWT 필터들은 그대로 유지
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 문제 해결용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("*"));
        /*cfg.setAllowedOrigins(List.of("http://localhost:8080"));*/ // 프론트 출처
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));                     // Authorization 등 허용
        cfg.setExposedHeaders(List.of("Location"));
        cfg.setAllowCredentials(true);                           // 쿠키/세션/withCredentials
        cfg.addExposedHeader("Authorization");
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
