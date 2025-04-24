package com.dev5ops.healthtart.user.domain.vo.request;

import lombok.Getter;

@Getter
public class EmailRequestVO {
    private String userPhone;
    private String verificationCode;
}
