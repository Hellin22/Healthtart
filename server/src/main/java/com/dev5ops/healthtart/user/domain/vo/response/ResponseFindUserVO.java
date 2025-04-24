package com.dev5ops.healthtart.user.domain.vo.response;

import com.dev5ops.healthtart.user.domain.UserTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ResponseFindUserVO {
    private String userCode;
    private UserTypeEnum userType;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userPhone;
    private String userNickname;
    private String userAddress;
    private Boolean userFlag;
    private String userGender;
    private Double userHeight;
    private Double userWeight;
    private Integer userAge;
    private String provider;
    private String providerId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    private Long gymCode;
}
