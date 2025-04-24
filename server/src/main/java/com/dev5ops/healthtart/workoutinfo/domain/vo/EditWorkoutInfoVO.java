package com.dev5ops.healthtart.workoutinfo.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EditWorkoutInfoVO {
    private String title;
    private Integer time;
    private LocalDateTime updatedAt;
}
