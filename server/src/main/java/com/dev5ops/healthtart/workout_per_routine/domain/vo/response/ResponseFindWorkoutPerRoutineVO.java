package com.dev5ops.healthtart.workout_per_routine.domain.vo.response;

import com.dev5ops.healthtart.exercise_equipment.domain.entity.ExerciseEquipment;
import com.dev5ops.healthtart.routine.domain.entity.Routine;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseFindWorkoutPerRoutineVO {
    private Long workoutPerRoutineCode;
    private int workoutOrder;
    private String workoutName;
    private String link;
    private int weightSet;
    private int numberPerSet;
    private int weightPerSet;
    private int workoutTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    private Routine routineCode;
    private ExerciseEquipment exerciseEquipmentCode;


}
