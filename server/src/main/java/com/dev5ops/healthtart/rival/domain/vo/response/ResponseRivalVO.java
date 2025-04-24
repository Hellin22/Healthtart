package com.dev5ops.healthtart.rival.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResponseRivalVO {
    private Long rivalMatchCode;
    private String userCode;
    private String rivalUserCode;
}
