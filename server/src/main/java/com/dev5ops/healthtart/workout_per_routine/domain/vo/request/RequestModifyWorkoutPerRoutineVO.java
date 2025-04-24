package com.dev5ops.healthtart.workout_per_routine.domain.vo.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequestModifyWorkoutPerRoutineVO {
    private int workoutOrder;
    private String workoutName;
    private String link;
    private int weightSet;
    private int numberPerSet;
    private int weightPerSet;
    private int workoutTime;
    private LocalDateTime updatedAt;
}
