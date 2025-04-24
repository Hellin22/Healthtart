package com.dev5ops.healthtart.inbody.aggregate.vo.request;

import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequestEditInbodyVO {
    private Integer inbodyScore;
    private double weight;
    private double height;
    private double muscleWeight;
    private double fatWeight;
    private double bmi;
    private double fatPercentage;
    private LocalDateTime dayOfInbody;
    private Integer basalMetabolicRate;
    private UserEntity user;
}
