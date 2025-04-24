package com.dev5ops.healthtart.workoutinfo.repository;

import com.dev5ops.healthtart.workoutinfo.domain.entity.WorkoutInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutInfoRepository extends JpaRepository <WorkoutInfo, Long> {
    WorkoutInfo findByRoutineCode_RoutineCode(Long routineCode);
}
