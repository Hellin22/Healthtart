package com.dev5ops.healthtart.recommended_workout_history.domain.dto;

import com.dev5ops.healthtart.workoutinfo.domain.entity.WorkoutInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class RecommendedWorkoutHistoryDTO {

    @JsonProperty("history_code")
    private Long historyCode;

    @JsonProperty("routine_ratings")
    private Double routineRatings;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("workout_info_code")
    private Long workoutInfoCode;
}
