package com.dev5ops.healthtart.rival.domain.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RivalUserInbodyScoreDTO {
    // 처음 랭킹사이트에서 보여질 데이터 의미(랭킹이나 모든 라이벌 조회시 보이는 데이터 똑같음.)
    // inbody -> inbodyScore
    // user by RivalUserCode-> userName, userGender, userHeight, userWeight, userAge

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
}
