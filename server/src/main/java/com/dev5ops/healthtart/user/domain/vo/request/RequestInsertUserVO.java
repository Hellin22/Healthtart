package com.dev5ops.healthtart.user.domain.vo.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class RequestInsertUserVO {

    String userName;
    String userType;
    String userEmail;
    String userPassword;
    String userPhone;
    String userNickname;
    String userAddress;
    // Boolean userFlag;
    String userGender;
    Double userHeight;
    Double userWeight;
    Integer userAge;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Long gymCode;

    public void changePwd(String encodedPwd) {
        this.userPassword = encodedPwd;
    }
}
