package com.dev5ops.healthtart.recommended_workout_history.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseRegisterRecommendedWorkoutHistoryVO {
    private Long historyCode;
    private Double routineRatings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long workoutInfoCode;
}
