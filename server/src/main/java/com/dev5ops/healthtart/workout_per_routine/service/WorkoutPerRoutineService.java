package com.dev5ops.healthtart.workout_per_routine.service;

import com.dev5ops.healthtart.workout_per_routine.domain.dto.WorkoutPerRoutineDTO;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.*;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.response.ResponseDeleteWorkoutPerRoutineVO;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.response.ResponseFindWorkoutPerRoutineVO;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.response.ResponseInsertWorkoutPerRoutineVO;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.response.ResponseModifyWorkoutPerRoutineVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface WorkoutPerRoutineService {
    List<ResponseFindWorkoutPerRoutineVO> getWorkoutPerRoutines();

    ResponseFindWorkoutPerRoutineVO findWorkoutPerRoutineByCode(Long workoutPerRoutineCode);

    // 루틴 코드별 우동별 루틴 조회
    List<ResponseFindWorkoutPerRoutineVO> findWorkoutPerRoutineByRoutineCode(Long routineCode);

    @Transactional
    ResponseInsertWorkoutPerRoutineVO registerWorkoutPerRoutine(WorkoutPerRoutineDTO workoutPerRoutineDTO);

    @Transactional
    ResponseModifyWorkoutPerRoutineVO modifyWorkoutPerRoutine (Long workoutPerRoutineCode, EditWorkoutPerRoutineVO modifyRoutine);

    @Transactional
    ResponseDeleteWorkoutPerRoutineVO deleteWorkoutPerRoutine(Long workoutPerRoutineCode);

    @Transactional
    boolean checkForDuplicateRoutines(Map<String, Object> workoutData);

    Long findRoutineCodeByWorkoutData(Map<String, Object> workoutData);
}
