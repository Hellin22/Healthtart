package com.dev5ops.healthtart.workoutinfo.service;

import com.dev5ops.healthtart.workoutinfo.domain.dto.WorkoutInfoDTO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.EditWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.response.ResponseDeleteWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.response.ResponseFindWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.response.ResponseInsertWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.response.ResponseModifyWorkoutInfoVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface WorkoutInfoService {
    // 운동 루틴 전체 조회
    List<ResponseFindWorkoutInfoVO> getWorkoutInfos();

    // 운동 루틴 단일 조회
    ResponseFindWorkoutInfoVO findWorkoutInfoByCode(Long WorkoutInfoCode);

    ResponseFindWorkoutInfoVO getWorkoutInfoByRoutineCode(Long routineCode);

    @Transactional
    ResponseInsertWorkoutInfoVO registerWorkoutInfo(WorkoutInfoDTO workoutInfoDTO);

    @Transactional
    ResponseModifyWorkoutInfoVO modifyWorkoutInfo(Long WorkoutInfoCode, EditWorkoutInfoVO modifyWorkoutInfo);

    // 루틴 멈추기 누르면 운동루틴 삭제
    @Transactional
    ResponseDeleteWorkoutInfoVO deleteWorkoutInfo(Long WorkoutInfoCode);

    Map<Long, List<Long>> groupingWorkoutInfoCodesByRoutineCode(List<ResponseFindWorkoutInfoVO> dtoList);
}
