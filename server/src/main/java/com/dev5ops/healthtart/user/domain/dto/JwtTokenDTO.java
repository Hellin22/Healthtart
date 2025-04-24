package com.dev5ops.healthtart.user.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenDTO {

    private String userCode;
    private String userEmail;
    private String userNickname;
}
