package com.dev5ops.healthtart.workoutinfo.domain.entity;

import com.dev5ops.healthtart.routine.domain.entity.Routine;
import com.dev5ops.healthtart.workoutinfo.domain.vo.EditWorkoutInfoVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Entity(name = "WorkoutInfo")
@Table(name = "workout_info")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder
public class WorkoutInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="workout_info_code")
    private Long workoutInfoCode;

    @Column(name = "title")
    private String title;

    @Column(name = "workout_time")
    private Integer time;

    @JsonProperty("recommend_music")
    private String recommendMusic;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name ="routine_code")
    private Routine routineCode;

    public void toUpdate(@Validated EditWorkoutInfoVO editWorkoutInfoVO) {
        this.title = editWorkoutInfoVO.getTitle();
        this.time = editWorkoutInfoVO.getTime();
        this.updatedAt = LocalDateTime.now();
    }

}
