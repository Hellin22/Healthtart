package com.dev5ops.healthtart.workoutinfo.domain.vo.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequestModifyWorkoutInfoVO {
    private String title;
    private Integer time;
    private LocalDateTime updatedAt;

}
