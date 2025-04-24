package com.dev5ops.healthtart.user.domain;

import lombok.Getter;

@Getter
public class KakaoUserProfile {
    private Long id; // 사용자 고유 ID
    private KakaoAccount kakao_account;

    @Getter
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter
        public static class Profile {
            private String nickname;
            private String profile_image_url;
            private String thumbnail_image_url;
        }
    }
}
