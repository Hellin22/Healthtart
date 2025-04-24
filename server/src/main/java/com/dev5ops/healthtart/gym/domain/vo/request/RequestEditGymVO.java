package com.dev5ops.healthtart.gym.domain.vo.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequestEditGymVO {
    private String gymName;
    private String address;
    private String businessNumber;
}
