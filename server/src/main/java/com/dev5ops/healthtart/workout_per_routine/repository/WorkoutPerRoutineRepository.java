package com.dev5ops.healthtart.workout_per_routine.repository;

import com.dev5ops.healthtart.workout_per_routine.domain.entity.WorkoutPerRoutine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutPerRoutineRepository extends JpaRepository<WorkoutPerRoutine, Long> {
    List<WorkoutPerRoutine> findByWorkoutOrderAndWorkoutName(Integer workoutOrder, String workoutName);

    List<WorkoutPerRoutine> findByRoutineCode_RoutineCode(Long routineCode);
}
