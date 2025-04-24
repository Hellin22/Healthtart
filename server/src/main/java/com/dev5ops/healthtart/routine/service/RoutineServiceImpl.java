package com.dev5ops.healthtart.routine.service;

import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.routine.domain.dto.RoutineDTO;
import com.dev5ops.healthtart.routine.domain.entity.Routine;
import com.dev5ops.healthtart.routine.domain.vo.*;
import com.dev5ops.healthtart.routine.domain.vo.response.ResponseFindRoutineVO;
import com.dev5ops.healthtart.routine.domain.vo.response.ResponseModifyRoutineVO;
import com.dev5ops.healthtart.routine.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineServiceImpl implements RoutineService {

    private final RoutineRepository routineRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ResponseFindRoutineVO> getRoutines() {
        List<Routine> routinesList = routineRepository.findAll();
        if (routinesList.isEmpty()) throw new CommonException(StatusEnum.ROUTINE_NOT_FOUND);
        return routinesList.stream()
                .map(routine -> modelMapper.map(routine, ResponseFindRoutineVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseFindRoutineVO findRoutineByCode(Long routineCode) {
        Routine routine = routineRepository.findById(routineCode)
                .orElseThrow(() -> new CommonException(StatusEnum.ROUTINE_NOT_FOUND));
        return modelMapper.map(routine, ResponseFindRoutineVO.class);
    }

    public Routine getRoutineByCode(Long routineCode) {
        if (routineCode == null) {
            throw new IllegalArgumentException("조회할 루틴 코드가 null입니다.");
        }
        return routineRepository.findById(routineCode)
                .orElseThrow(() -> new CommonException(StatusEnum.ROUTINE_NOT_FOUND));
    }

    @Override
    @Transactional
    public RoutineDTO registerRoutine(RoutineDTO routineDTO) {

        Routine routine = Routine.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Routine savedRoutine = routineRepository.save(routine);
        System.out.println("저장된 루틴 코드: " + savedRoutine.getRoutineCode());
        return modelMapper.map(routine, RoutineDTO.class);
    }

    @Override
    @Transactional
    public ResponseModifyRoutineVO modifyRoutine(Long routineCode, EditRoutineVO modifyRoutine) {
        Routine routine = routineRepository.findById(routineCode)
                .orElseThrow(() -> new CommonException(StatusEnum.ROUTINE_NOT_FOUND));
        routine.toUpdate(modifyRoutine);
        routineRepository.save(routine);
        return modelMapper.map(routine, ResponseModifyRoutineVO.class);
    }

    @Override
    @Transactional
    public void deleteRoutine(Long routineCode) {
        Routine routine = routineRepository.findById(routineCode)
                .orElseThrow(() -> new CommonException(StatusEnum.ROUTINE_NOT_FOUND));
        routineRepository.delete(routine);
    }

}


