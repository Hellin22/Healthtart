package com.dev5ops.healthtart.user.controller;

import com.dev5ops.healthtart.user.service.KakaoOauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class UserOauthController {

    private final KakaoOauthService kakaoOauthService;

    @PostMapping("/kakao")
    public Map<String, String> kakaoLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        String codeVerifier = body.get("codeVerifier");
        String jwt = kakaoOauthService.loginWithKakao(code, codeVerifier);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        return response;
    }
}