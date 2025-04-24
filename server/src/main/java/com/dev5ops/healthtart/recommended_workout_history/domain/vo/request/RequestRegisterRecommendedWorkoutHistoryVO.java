package com.dev5ops.healthtart.recommended_workout_history.domain.vo.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestRegisterRecommendedWorkoutHistoryVO {
    private Double routineRatings;
    private Long workoutInfoCode;
}

