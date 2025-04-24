package com.dev5ops.healthtart.user.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMypageDTO {

    private String userName;
    private String userEmail;
    private String userPassword;
    private String userPhone;
    private String userNickname;
    private String userGender;
    private Double userHeight;
    private Double userWeight;
    private LocalDateTime updatedAt;

    // gym
    private Long gymCode;
    private String gymName;

    // rival
    private String rivalUserCode;
    private String rivalNickname;
}
