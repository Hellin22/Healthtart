package com.dev5ops.healthtart.user.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserTypeEnum {
    MEMBER("MEMBER"),
    ADMIN("ADMIN");

    private final String userType;

    UserTypeEnum(String userType) {
        this.userType = userType;
    }

    // JSON으로 직렬화할 때 사용할 값 지정
    // Enum 직렬화될 때 getType() 메서드가 반환하는 값이 JSON으로 변환됨
    @JsonValue
    public String getType() {
        return userType;
    }
}
