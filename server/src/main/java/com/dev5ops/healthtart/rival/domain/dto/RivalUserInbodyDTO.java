package com.dev5ops.healthtart.rival.domain.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RivalUserInbodyDTO {
    // 이 dto는 라이벌 선택을 눌렀을 경우 어떠한 데이터를 추가로 받아올까에 대한 내용
    // 일단 userCode와 rivalUserCode가 필요함. 이 2명의 데이터를 끌어봐야하기 때문.

    // rival 데이터
    private Long rivalMatchCode;

    // userEntity 데이터
    private String userCode;
    private String userName;
    private String userGender;
    private Double userHeight;
    private Double userWeight;
    private Integer userAge;
    private Boolean userFlag;

    // inbody 데이터
    private Integer inbodyScore;
    private Double height;
    private Double weight;
    private double muscleWeight;
    private double fatWeight;
    private double bmi;
    private double fatPercentage;
    private Integer basalMetabolicRate;
}
