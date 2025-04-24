package com.dev5ops.healthtart.workoutinfo.service;


import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.routine.domain.entity.Routine;
import com.dev5ops.healthtart.routine.service.RoutineService;
import com.dev5ops.healthtart.workoutinfo.domain.dto.WorkoutInfoDTO;
import com.dev5ops.healthtart.workoutinfo.domain.entity.WorkoutInfo;
import com.dev5ops.healthtart.workoutinfo.domain.vo.EditWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.response.ResponseDeleteWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.response.ResponseFindWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.response.ResponseInsertWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.response.ResponseModifyWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.repository.WorkoutInfoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutInfoServiceImpl implements WorkoutInfoService {

    private final WorkoutInfoRepository workoutInfoRepository;
    private final ModelMapper modelMapper;
    private final RoutineService  routineService;

    @Override
    public List<ResponseFindWorkoutInfoVO> getWorkoutInfos() {
        List<WorkoutInfo> workoutinfoCodesList = workoutInfoRepository.findAll();
        if (workoutinfoCodesList.isEmpty()) throw new CommonException(StatusEnum.ROUTINE_NOT_FOUND);
        return workoutinfoCodesList.stream()
                .map(routine -> modelMapper.map(routine, ResponseFindWorkoutInfoVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseFindWorkoutInfoVO findWorkoutInfoByCode(Long WorkoutInfoCode) {
        WorkoutInfo workoutInfo = workoutInfoRepository.findById(WorkoutInfoCode)
                .orElseThrow(() -> new CommonException(StatusEnum.ROUTINE_NOT_FOUND));
        return modelMapper.map(workoutInfo, ResponseFindWorkoutInfoVO.class);
    }
// findByRoutineCode_RoutineCode
    @Override
    public ResponseFindWorkoutInfoVO getWorkoutInfoByRoutineCode(Long routineCode) {
        WorkoutInfo workoutInfo = workoutInfoRepository.findByRoutineCode_RoutineCode(routineCode);
        return modelMapper.map(workoutInfo, ResponseFindWorkoutInfoVO.class);
    }

    @Override
    @Transactional
    public ResponseInsertWorkoutInfoVO registerWorkoutInfo(WorkoutInfoDTO workoutInfoDTO) {
        Routine routine = routineService.getRoutineByCode(workoutInfoDTO.getRoutineCode());

        WorkoutInfo workoutInfo = WorkoutInfo.builder()
                .workoutInfoCode(workoutInfoDTO.getWorkoutInfoCode())
                .title(workoutInfoDTO.getTitle())
                .time(workoutInfoDTO.getTime())
                .recommendMusic(workoutInfoDTO.getRecommendMusic())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .routineCode(routine)
                .build();

        workoutInfoRepository.save(workoutInfo);

        return modelMapper.map(workoutInfo, ResponseInsertWorkoutInfoVO.class);
    }

    @Override
    @Transactional
    public ResponseModifyWorkoutInfoVO modifyWorkoutInfo(Long WorkoutInfoCode, EditWorkoutInfoVO modifyWorkoutInfo) {
        WorkoutInfo workoutInfo = workoutInfoRepository.findById(WorkoutInfoCode)
                .orElseThrow(() -> new CommonException(StatusEnum.ROUTINE_NOT_FOUND));
        workoutInfo.toUpdate(modifyWorkoutInfo);
        workoutInfoRepository.save(workoutInfo);
        return modelMapper.map(workoutInfo, ResponseModifyWorkoutInfoVO.class);
    }

    @Override
    @Transactional
    public ResponseDeleteWorkoutInfoVO deleteWorkoutInfo(Long WorkoutInfoCode) {
        WorkoutInfo workoutInfo = workoutInfoRepository.findById(WorkoutInfoCode)
                .orElseThrow(() -> new CommonException(StatusEnum.ROUTINE_NOT_FOUND));
        workoutInfoRepository.delete(workoutInfo);
        return new ResponseDeleteWorkoutInfoVO();
    }

    @Override
    public Map<Long, List<Long>> groupingWorkoutInfoCodesByRoutineCode(List<ResponseFindWorkoutInfoVO> dtoList) {
        return dtoList.stream()
                .collect(Collectors.groupingBy(
                        ResponseFindWorkoutInfoVO::getRoutineCode,
                        Collectors.mapping(ResponseFindWorkoutInfoVO::getWorkoutInfoCode, Collectors.toList())
                ));
    }
}
