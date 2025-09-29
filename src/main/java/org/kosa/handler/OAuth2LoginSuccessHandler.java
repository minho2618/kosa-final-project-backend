package org.kosa.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Member; // 🚨 import 추가
import org.kosa.enums.MemberProvider; // 🚨 import 추가
import org.kosa.jwt.JWTUtil;
import org.kosa.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final MemberService memberService; // 사용자 정보를 저장/업데이트할 서비스

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String registrationId = null;
        String email;
        String name; // name 변수 선언

        // 1. Provider 이름(registrationId) 추출
        if (authentication instanceof OAuth2AuthenticationToken) {
            registrationId = ((OAuth2AuthenticationToken) authentication)
                    .getAuthorizedClientRegistrationId();
        } else {
            log.error("Authentication 객체가 예상한 타입이 아닙니다: {}", authentication.getClass().getName());
            throw new ServletException("인증 객체 타입 오류: 소셜 로그인이 아닙니다.");
        }

        // 2. Provider 별로 사용자 정보 추출 로직 분기
        if ("naver".equals(registrationId)) {
            // 네이버 전용 로직
            Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");

            if (naverResponse == null) {
                log.error("네이버 응답에서 'response' 키를 찾을 수 없습니다.");
                throw new RuntimeException("네이버 로그인 인증 실패: 필수 속성 누락");
            }

            email = (String) naverResponse.get("email");
            name = (String) naverResponse.get("name"); // 네이버 name 추출

        } else if ("google".equals(registrationId)) {
            // Google 전용 로직
            email = (String) attributes.get("email");
            name = (String) attributes.get("name"); // Google name 추출

        } else {
            // 지원하지 않는 Provider 처리
            log.error("지원하지 않는 소셜 로그인 Provider: {}", registrationId);
            throw new ServletException("지원하지 않는 소셜 로그인 제공자입니다.");
        }

        // 🚨 3. DB 저장 및 JWT 생성 로직 (통합) 🚨

        // Provider 설정 (Enum 변환)
        MemberProvider provider = MemberProvider.valueOf(registrationId.toUpperCase()); // NAVER 또는 GOOGLE

        // DB에 사용자 정보를 저장/업데이트
        Member loggedInMember = memberService.saveOrUpdateSocialMember(provider, email, name);

        // JWT 토큰 생성
        Long expiredMs = 60 * 60 * 10 * 1000L; // 10시간
        String jwtToken = jwtUtil.createJwt(
                loggedInMember,
                loggedInMember.getRole().name(),
                expiredMs
        );

        // 4. 리다이렉트 경로를 범용적인 경로로 수정
        String redirectUri = "http://localhost:80/auth/social/callback?token=" + jwtToken;

        // 5. 프론트엔드 콜백 페이지로 리다이렉션
        log.info("Redirecting to: {}", redirectUri);
        getRedirectStrategy().sendRedirect(request, response, redirectUri);

        // super.onAuthenticationSuccess 호출은 제거합니다. (직접 리다이렉트 했으므로)
    }
}