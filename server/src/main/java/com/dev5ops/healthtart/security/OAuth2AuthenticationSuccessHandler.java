package com.dev5ops.healthtart.security;

import com.dev5ops.healthtart.security.JwtUtil;
import com.dev5ops.healthtart.user.domain.UserTypeEnum;
import com.dev5ops.healthtart.user.domain.dto.JwtTokenDTO;
import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Authentication Success");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info("OAuth2User attributes: {}", oAuth2User.getAttributes());

        // User 객체 추출
        Object userObj = oAuth2User.getAttribute("user");
        if (!(userObj instanceof UserEntity)) {
            log.error("User object not found or invalid type in OAuth2User attributes");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid user data");
            return;
        }
        UserEntity user = (UserEntity) userObj;

        String userCode = user.getUserCode();
        String userEmail = user.getUserEmail();
        String userNickname = user.getUserNickname();

        String userName = user.getUserName();
        String encodedUserName = URLEncoder.encode(userName, StandardCharsets.UTF_8);

        String provider = user.getProvider();
        String providerId = user.getProviderId();

        // 사용자 권한 설정 (예: "ROLE_USER")
        List<String> roles = List.of("MEMBER");

        // JWT 토큰에 들어갈 데이터(JwtTokenDTO)
        JwtTokenDTO tokenDTO = new JwtTokenDTO(userCode, userEmail, userNickname);

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(tokenDTO, roles, provider);
        log.info("Generated JWT token: {}", token);

        // 리다이렉트 url 설정
        String redirectUrl = String.format("http://localhost:5173");

        // 추가정보를 받을 회원 구분
        if(userNickname != null){ // 이미 회원가입된 회원
            redirectUrl += "?token=" + token;
        }else{ // 신규 가입회원 -> 추가정보 페이지로 리다이렉트
                                                            // userName은 한글이어서 UTF-8로 인코딩
            redirectUrl += "/users/addinfo" + "?token=" + token + "&userName=" + encodedUserName + "&userEmail=" + userEmail + "&provider=" + provider + "&providerId=" + providerId;
        }

        log.info("Redirecting to: {}", redirectUrl);

        // 프론트엔드 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String extractProvider(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            return ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        }
        return "unknown";
    }


}
