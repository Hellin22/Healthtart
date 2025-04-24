package com.dev5ops.healthtart.workout_per_routine.domain.entity;

import com.dev5ops.healthtart.exercise_equipment.domain.entity.ExerciseEquipment;
import com.dev5ops.healthtart.routine.domain.entity.Routine;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.EditWorkoutPerRoutineVO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Entity(name = "WorkoutPerRoutine")
@Table(name = "workout_per_routine")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WorkoutPerRoutine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name="workout_per_routine_code")
    private Long workoutPerRoutineCode;

    @Column(name = "workout_order")
    private int workoutOrder;

    @Column(name ="workout_name")
    private String workoutName;

    @Column(name = "link")
    private String link;

    @Column(name = "weight_set")
    private int weightSet;

    @Column(name = "number_per_set")
    private int numberPerSet;

    @Column(name = "weight_per_set")
    private int weightPerSet;

    @Column(name = "workout_time")
    private int workoutTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "routine_code")
    private Routine routineCode;

    @ManyToOne
    @JoinColumn(name = "exercise_equipment_code")
    private ExerciseEquipment exerciseEquipmentCode;

    public void toUpdate(@Validated EditWorkoutPerRoutineVO editRoutineVO) {
        this.workoutOrder = editRoutineVO.getWorkoutOrder();
        this.workoutName = editRoutineVO.getWorkoutName();
        this.link = editRoutineVO.getLink();
        this.weightSet = editRoutineVO.getWeightSet();
        this.numberPerSet = editRoutineVO.getNumberPerSet();
        this.weightPerSet = editRoutineVO.getWeightPerSet();
        this.workoutTime = editRoutineVO.getWorkoutTime();
        this.updatedAt = LocalDateTime.now();
    }


}
