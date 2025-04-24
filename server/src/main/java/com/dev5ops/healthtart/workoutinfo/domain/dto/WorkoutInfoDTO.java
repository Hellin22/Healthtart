package com.dev5ops.healthtart.workoutinfo.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class WorkoutInfoDTO {
    @JsonProperty("workout_info_code")
    private Long workoutInfoCode;

    @JsonProperty("title")
    private String title;

    @JsonProperty("time")
    private Integer time;

    @JsonProperty("recommend_music")
    private String recommendMusic;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("routine_code")
    private Long routineCode;
}
