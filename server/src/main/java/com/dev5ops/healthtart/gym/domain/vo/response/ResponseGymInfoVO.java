package com.dev5ops.healthtart.gym.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResponseGymInfoVO {
    private Long gymCode;
    private String gymName;
}
