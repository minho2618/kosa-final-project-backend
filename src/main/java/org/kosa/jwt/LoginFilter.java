package org.kosa.jwt;

import com.google.gson.Gson;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Member;
import org.kosa.security.CustomMemberDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter{
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
													throws AuthenticationException{
		String username = super.obtainUsername(request);
        String password = super.obtainPassword(request);
        log.info("username {}", username);
        log.info("password {}", password);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password, null);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        log.info("authentication {}", authentication);
        return authentication;
	}

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws  IOException{
        response.setContentType("text/html;charset=UTF-8");
       log.info("로그인 성공 ......");
        CustomMemberDetails customMemberDetails = (CustomMemberDetails) authentication.getPrincipal();
        
        String username = customMemberDetails.getUsername();//아이디

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(
                customMemberDetails.getMember(), role, 1000L*60*60*10L);
        System.out.println("@@@@@@@@@@@@@@@@@@ getMember "+ customMemberDetails.getMember() +" @@@@@@@@@@@@@@@@@@");

        response.addHeader("Authorization", "Bearer " + token);

        Map<String, Object> map = new HashMap<>();
        Member member = customMemberDetails.getMember();
        map.put("memberId", member.getMemberId());
        map.put("username", member.getEmail());
        map.put("name", member.getName());
        map.put("address", member.getAddress());

        Gson gson= new Gson();
        String arr = gson.toJson(map);
        response.getWriter().print(arr);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        log.info("로그인 실패... ......");
        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);

        Map<String, Object> map = new HashMap<>();
        map.put("errMsg","정보를 다시 확인해주세요.");
        Gson gson= new Gson();
        String arr = gson.toJson(map);
        response.getWriter().print(arr);
    }
	
}








