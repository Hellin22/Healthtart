package com.dev5ops.healthtart.user.service;


import com.dev5ops.healthtart.user.domain.UserTypeEnum;
import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import com.dev5ops.healthtart.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId;
        String email;
        String name;

        if ("google".equals(provider)) {
            providerId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else if ("kakao".equals(provider)) {
            // 카카오 고유 ID 가져오기
            providerId = String.valueOf(attributes.get("id"));

            // 카카오 계정 정보에서 프로필 정보 가져오기
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

            // 이름(nickname == 우리쪽에서 name으로 사용)만 가능
            name = (String) kakaoProfile.get("nickname");
            email = null;
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }

        // provider는 kakao인지 google인지 구분
        // providerId는 provider 내부에서의 code
        // provider + providerId로 인해 유일한 사용자임을 구분가능.
        // google, kakao 다른 회원가입 사용자지만 providerId가 동일할 가능성 있기 때문
        UserEntity user = userRepository.findByProviderAndProviderId(provider, providerId);

        if (user == null) {
            // 최초 로그인인 경우
            String curDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String uuid = UUID.randomUUID().toString();

            String userCode = curDate + "-" + uuid.substring(0);

            user = UserEntity.builder()
                    .userCode(userCode)
                    .userType(UserTypeEnum.MEMBER)
                    .userName(name)
                    .userEmail(email)
                    .userPassword(null)
                    .userPhone(null)
                    .userNickname(null)
                    .userAddress(null)
                    .userFlag(true)  // 활성 사용자로 설정
                    .provider(provider)
                    .providerId(providerId)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }else {
            // 기존 사용자 정보 업데이트
            user.setUserName(name);
        }

        // 회원가입 및 유저정보 수정 로직
        user = userRepository.save(user);

        // User 정보를 포함한 attributes 맵 생성
        Map<String, Object> userAttributes = new HashMap<>(attributes);
        userAttributes.put("user", user);

        // 제공자별 ID를 주요 식별자로 사용 -> 카카오는 "id"
        String nameAttributeKey = "google".equals(provider) ? "sub" : "id";
        log.info("userAttributes:{}", userAttributes);
        log.info("userAttributes:{}", userAttributes.toString());
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getUserType().name())),
                userAttributes,
                nameAttributeKey
        );
    }
}