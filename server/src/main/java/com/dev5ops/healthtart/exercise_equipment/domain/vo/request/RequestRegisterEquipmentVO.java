package com.dev5ops.healthtart.exercise_equipment.domain.vo.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequestRegisterEquipmentVO {
    private String exerciseEquipmentName;
    private String bodyPart;
    private String exerciseDescription;
    private String exerciseImage;
    private String recommendedVideo;
}
