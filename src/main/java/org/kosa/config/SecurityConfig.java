package org.kosa.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.jwt.JWTFilter;
import org.kosa.jwt.JWTUtil;
import org.kosa.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        log.info("bCryptPasswordEncoder call.. =====>");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("SecurityFilterChain ==============>");
        http.csrf(auth->auth.disable());
        http.formLogin(auth->auth.disable());
        http.httpBasic(auth->auth.disable());
        http.authorizeHttpRequests(auth->auth
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated());

        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
