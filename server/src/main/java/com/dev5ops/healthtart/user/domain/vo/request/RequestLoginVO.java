package com.dev5ops.healthtart.user.domain.vo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestLoginVO {

    @JsonProperty("userEmail")
    private String userEmail;

    @JsonProperty("userPassword")
    private String userPassword;
}
