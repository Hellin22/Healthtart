package com.dev5ops.healthtart.user.service;

import com.dev5ops.healthtart.security.JwtUtil;
import com.dev5ops.healthtart.user.domain.KakaoUserProfile;
import com.dev5ops.healthtart.user.domain.UserTypeEnum;
import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import com.dev5ops.healthtart.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoOauthService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    private final UserRepository userRepository;
    private final RestTemplate restTemplate; // 또는 WebClient
    private final JwtUtil jwtUtil;

    public String loginWithKakao(String code, String codeVerifier) {

        // 1. 인가코드로 카카오 access_token 요청
        String kakaoAccessToken = requestAccessToken(code, codeVerifier);

        // 2. access_token으로 사용자 정보 요청
        KakaoUserProfile profile = requestUserProfile(kakaoAccessToken);
        String email = profile.getKakao_account().getEmail();
        String nickname = profile.getKakao_account().getProfile().getNickname();
        String kakaoId = String.valueOf(profile.getId());
        String provider = "kakao";
        String providerId = kakaoId;

        // 3. DB에 사용자 존재 여부 확인 및 저장
        UserEntity user = userRepository.findByProviderAndProviderId(provider, providerId);
        String userCode = (user != null) ? user.getUserCode()
                : saveNewKakaoUser(email, nickname, provider, providerId);

        // 4. JWT 발급 및 인증 객체 등록
        String token = jwtUtil.generateTokenFromKakaoUser(profile, userCode);
        Authentication authentication = jwtUtil.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return token;
    }

    // access_token 요청
    private String requestAccessToken(String code, String codeVerifier) {
        String tokenUri = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("client_secret", kakaoClientSecret);
        params.add("redirect_uri", kakaoRedirectUri); // 프론트 redirect URI
        params.add("code", code);
        params.add("code_verifier", codeVerifier);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map body = response.getBody();
            return (String) body.get("access_token");
        } else {
            throw new RuntimeException("카카오 access_token 요청 실패: " + response.getStatusCode());
        }
    }

    // 사용자 정보 요청
    private KakaoUserProfile requestUserProfile(String accessToken) {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Authorization: Bearer {accessToken}

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserProfile> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                request,
                KakaoUserProfile.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("카카오 사용자 정보 요청 실패: " + response.getStatusCode());
        }
    }

    private String saveNewKakaoUser(String email, String nickname, String provider, String providerId) {
        String curDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString();
        String userCode = curDate + "-" + uuid.substring(0);

        UserEntity user = UserEntity.builder()
                .userCode(userCode)
                .userType(UserTypeEnum.MEMBER)
                .userName(nickname)
                .userEmail(email)
                .userPassword(null)
                .userPhone(null)
                .userNickname(null)
                .userAddress(null)
                .userFlag(true)
                .provider(provider)
                .providerId(providerId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        return userCode;
    }
}

