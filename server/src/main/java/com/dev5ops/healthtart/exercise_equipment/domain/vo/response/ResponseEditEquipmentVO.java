package com.dev5ops.healthtart.exercise_equipment.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResponseEditEquipmentVO {
    private String exerciseEquipmentName;
    private String bodyPart;
    private String exerciseDescription;
    private String exerciseImage;
    private String recommendedVideo;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
