package com.dev5ops.healthtart.routine.service;

import com.dev5ops.healthtart.routine.domain.dto.RoutineDTO;
import com.dev5ops.healthtart.routine.domain.entity.Routine;
import com.dev5ops.healthtart.routine.domain.vo.*;
import com.dev5ops.healthtart.routine.domain.vo.response.ResponseDeleteRoutineVO;
import com.dev5ops.healthtart.routine.domain.vo.response.ResponseFindRoutineVO;
import com.dev5ops.healthtart.routine.domain.vo.response.ResponseModifyRoutineVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoutineService {
    List<ResponseFindRoutineVO> getRoutines();

    ResponseFindRoutineVO findRoutineByCode(Long routineCode);

    @Transactional
    RoutineDTO registerRoutine(RoutineDTO routineDTO);

    @Transactional
    ResponseModifyRoutineVO modifyRoutine(Long routineCode, EditRoutineVO modifyRoutine);

    @Transactional
    void deleteRoutine(Long routineCode);

    Routine getRoutineByCode(Long routineCode);
}
