package com.dev5ops.healthtart.recommended_workout_history.domain.entity;

import com.dev5ops.healthtart.workoutinfo.domain.entity.WorkoutInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "RecommendedWorkoutHistory")
@Table(name = "recommended_workout_history")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder
public class RecommendedWorkoutHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="history_code")
    private Long historyCode;

    @Column(name="routine_ratings")
    private Double routineRatings;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Column(name="workout_info_code")
    private Long workoutInfoCode;

}
