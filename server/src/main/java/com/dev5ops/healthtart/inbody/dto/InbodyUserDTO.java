package com.dev5ops.healthtart.inbody.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class InbodyUserDTO {

    //이름 -> 유저
    String userNickname;
    //성별 ->유저
    String userGender;
    //키 ->인바디
    Double height;
    //체중 ->인바디
    Double weight;
    //골격근량 ->인바디
    Double MuscleWeight;
    //체지방률 ->인바디
    Double FatPercentage;
    //기초대사량 ->인바디
    Integer BasalMetabolicRate;
    //인바디 점수 ->인바디
    Integer InbodyScore;

}
