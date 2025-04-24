package com.dev5ops.healthtart.record_per_user.domain.entity;

import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity(name = "RecordPerUser")
@Table(name = "record_per_user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RecordPerUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_record_code", nullable = false, unique = true)
    private Long userRecordCode;

    @Column(name = "day_of_exercise", nullable = false)
    private LocalDateTime dayOfExercise;

    @Column(name = "exercise_duration", nullable = false)
    private Integer exerciseDuration;

    @Column(name = "record_flag", nullable = false)
    private boolean recordFlag;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_code", nullable = false)
    private UserEntity user;

    @Column(name = "routine_code", nullable = false)
    private Long RoutineCode;

    public void setDayOfExercise(LocalDateTime dayOfExercise) {
        // 시간을 00:00:00으로 설정하여 저장
        this.dayOfExercise = dayOfExercise.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
}