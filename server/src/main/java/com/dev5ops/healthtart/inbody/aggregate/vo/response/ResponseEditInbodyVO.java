package com.dev5ops.healthtart.inbody.aggregate.vo.response;

import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResponseEditInbodyVO {
    private Integer inbodyScore;
    private double weight;
    private double height;
    private double muscleWeight;
    private double fatWeight;
    private double bmi;
    private double fatPercentage;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dayOfInbody;
    private Integer basalMetabolicRate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    private UserEntity user;
}
