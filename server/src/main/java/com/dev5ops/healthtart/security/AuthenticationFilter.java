package com.dev5ops.healthtart.security;

import com.dev5ops.healthtart.user.domain.CustomUserDetails;
import com.dev5ops.healthtart.user.domain.dto.JwtTokenDTO;
import com.dev5ops.healthtart.user.domain.vo.request.RequestLoginVO;
import com.dev5ops.healthtart.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env;
    private JwtUtil jwtUtil;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment env, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.userService = userService;
        this.env = env;
        this.jwtUtil = jwtUtil;

        // 커스텀 로그인 경로 설정
        setFilterProcessesUrl("/users/login");
    }

    /* 설명. 로그인 시도 시 동작하는 기능(POST /users/login 요청 시) -> service의 loasUser메서드 전에 작동한다 */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        log.info("로그인시 동작하는 기능임. authenticationfilter에요");
        /* 설명. request body에 담긴 내용을 우리가 만든 RequestLoginVO 타입에 담는다.(일종의 @RequestBody의 개념) */
        try {
            RequestLoginVO creds = new ObjectMapper().readValue(request.getInputStream(), RequestLoginVO.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getUserEmail(), creds.getUserPassword(), new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* 설명. 로그인 성공 시 JWT 토큰 생성하는 기능 */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        log.info("로그인 성공하고 security가 관리하는 principal 객체(authResult): {}", authResult);

        /* 설명. 로그인 이후 관리되고 있는 Authentication 객체를 활용해 JWT Token 만들기 */
        log.info("시크릿 키: " + env.getProperty("token.secret"));

        /* 설명. 토큰의 payload에 어떤 값을 담고 싶은지에 따라 고민해서 재료를 수집한다.(id, 가진 권한들, 만료시간) */
//        String userEmail = ((User)authResult.getPrincipal()).getUsername();  // id의 개념(우리는 email로 작성했음)

        // CustomUserDetails로 캐스팅하여 사용자 정보를 가져옴
        CustomUserDetails userDetails = null;

        if (authResult.getPrincipal() instanceof CustomUserDetails) {
            userDetails = (CustomUserDetails) authResult.getPrincipal(); // 이게 뭔지 봐야할듯.
            log.info("userDetails: {}", userDetails);
            log.info("Authentication: {}", authResult);
            // 이후 작업 계속
        } else {
            throw new IllegalArgumentException("인증 객체가 CustomUserDetails가 아닙니다.");
        }

        String userCode = userDetails.getUserDTO().getUserCode(); // CustomUserDetails에서 UserDTO로 접근하여 userCode 가져옴
        String userEmail = userDetails.getUsername();  // CustomUserDetails의 이메일 정보
        String userNickname = userDetails.getUserDTO().getUserNickname();

        log.info("인증된 회원의 userCode: " + userCode);
        log.info("인증된 회원의 email: " + userEmail);
        log.info("인등된 회원의 userNickname: " + userNickname);

        /* 설명. 권한들을 꺼내 List<String>로 변환 */
        List<String> roles = authResult.getAuthorities().stream()
                .map(role -> role.getAuthority())
                .collect(Collectors.toList());
        log.info("roles: {}", roles.toString());

        JwtTokenDTO tokenDTO = new JwtTokenDTO(userCode, userEmail, userNickname); // principal에서 제공해주는 데이터가 적음.
        String token = jwtUtil.generateToken(tokenDTO, roles, null);

//        response.addHeader("token", token);
        response.addHeader(HttpHeaders.AUTHORIZATION, token);
    }
}