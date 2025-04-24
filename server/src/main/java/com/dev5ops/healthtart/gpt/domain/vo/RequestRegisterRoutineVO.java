package com.dev5ops.healthtart.gpt.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequestRegisterRoutineVO {
    private String userCode;
    private String bodyPart;
    private int time;
}
