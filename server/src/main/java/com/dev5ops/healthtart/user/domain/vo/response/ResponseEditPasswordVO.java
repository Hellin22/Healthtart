package com.dev5ops.healthtart.user.domain.vo.response;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
public class ResponseEditPasswordVO {
    private String message;

    public ResponseEditPasswordVO(String message) {
        this.message = message;
    }
}
