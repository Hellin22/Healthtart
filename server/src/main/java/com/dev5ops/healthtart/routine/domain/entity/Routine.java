
package com.dev5ops.healthtart.routine.domain.entity;

import com.dev5ops.healthtart.routine.domain.vo.EditRoutineVO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Entity(name = "WorkoutRoutine")
@Table(name = "routines")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="routine_code")
    private Long routineCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void toUpdate(@Validated EditRoutineVO editRoutineVO) {
        this.updatedAt = editRoutineVO.getUpdatedAt();
    }

    }
