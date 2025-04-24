package com.dev5ops.healthtart.inbody.dto;

import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class InbodyDTO {
    private Long inbodyCode;
    private Integer inbodyScore;
    private double weight;
    private double height;
    private double muscleWeight;
    private double fatWeight;
    private double bmi;
    private double fatPercentage;
    private LocalDateTime dayOfInbody;
    private Integer basalMetabolicRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserEntity user;
    private String userCode;
}
