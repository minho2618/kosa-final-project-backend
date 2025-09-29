package org.kosa.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kosa.entity.Member;
import org.kosa.enums.MemberRole;
import org.kosa.security.CustomMemberDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // ✅ 공개 경로는 필터 적용 안 함
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // 필요 시 다른 공개 API도 추가
        return uri.startsWith("/api/gemini/")
                || uri.startsWith("/swagger-ui/")
                || uri.startsWith("/api-docs/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        // Authorization 헤더 없거나 포맷 불일치 → 그냥 통과
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        // 만료되었으면 인증만 안 세팅하고 통과(401/600 같은 커스텀 상태는 만들지 않음)
        try {
            if (jwtUtil.isExpired(token)) {
                chain.doFilter(request, response);
                return;
            }
        } catch (Exception e) {
            // 토큰 파싱 중 예외(서명불일치 등) → 인증만 안 세팅하고 통과
            chain.doFilter(request, response);
            return;
        }

        Long memberId = jwtUtil.getMemberId(token);
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        Member member = new Member();
        member.setMemberId(memberId);
        member.setEmail(username);
        member.setRole(MemberRole.valueOf(role));

        CustomMemberDetails customMemberDetails = new CustomMemberDetails(member);
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customMemberDetails, null, customMemberDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
        chain.doFilter(request, response);
    }
}
