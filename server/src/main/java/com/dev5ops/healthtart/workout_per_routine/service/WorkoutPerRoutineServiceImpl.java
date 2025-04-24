package com.dev5ops.healthtart.workout_per_routine.service;

import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.exercise_equipment.domain.entity.ExerciseEquipment;
import com.dev5ops.healthtart.exercise_equipment.service.ExerciseEquipmentService;
import com.dev5ops.healthtart.routine.domain.entity.Routine;
import com.dev5ops.healthtart.routine.service.RoutineService;
import com.dev5ops.healthtart.workout_per_routine.domain.dto.WorkoutPerRoutineDTO;
import com.dev5ops.healthtart.workout_per_routine.domain.entity.WorkoutPerRoutine;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.*;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.response.ResponseDeleteWorkoutPerRoutineVO;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.response.ResponseFindWorkoutPerRoutineVO;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.response.ResponseInsertWorkoutPerRoutineVO;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.response.ResponseModifyWorkoutPerRoutineVO;
import com.dev5ops.healthtart.workout_per_routine.repository.WorkoutPerRoutineRepository;
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
public class  WorkoutPerRoutineServiceImpl implements WorkoutPerRoutineService {

    private final WorkoutPerRoutineRepository workoutPerRoutineRepository;
    private final ModelMapper modelMapper;
    private final RoutineService routineService;
    private final ExerciseEquipmentService exerciseEquipmentService;

    // 운동 루틴별 운동 전체 조회
    @Override
    public List<ResponseFindWorkoutPerRoutineVO> getWorkoutPerRoutines() {
        List<WorkoutPerRoutine> routinesList = workoutPerRoutineRepository.findAll();
        if (routinesList.isEmpty()) throw new CommonException(StatusEnum.ROUTINE_NOT_FOUND);
        return routinesList.stream()
                .map(routine -> modelMapper.map(routine, ResponseFindWorkoutPerRoutineVO.class))
                .collect(Collectors.toList());
    }

    // 운동 루틴별 운동 단일 조회
    @Override
    public ResponseFindWorkoutPerRoutineVO findWorkoutPerRoutineByCode(Long workoutPerRoutineCode) {
        WorkoutPerRoutine routine = workoutPerRoutineRepository.findById(workoutPerRoutineCode)
                .orElseThrow(() -> new CommonException(StatusEnum.ROUTINE_NOT_FOUND));
        return modelMapper.map(routine, ResponseFindWorkoutPerRoutineVO.class);
    }

    // 루틴 코드별 운동별 루틴 조회
    @Override
    public List<ResponseFindWorkoutPerRoutineVO> findWorkoutPerRoutineByRoutineCode(Long routineCode) {
        List<WorkoutPerRoutine> routinesList = workoutPerRoutineRepository.findByRoutineCode_RoutineCode(routineCode);
        return routinesList.stream()
                .map(routine -> modelMapper.map(routine, ResponseFindWorkoutPerRoutineVO.class))
                .collect(Collectors.toList());
    }

    // 운동 루틴별 운동 등록
    @Override
    @Transactional
    public ResponseInsertWorkoutPerRoutineVO registerWorkoutPerRoutine(WorkoutPerRoutineDTO workoutPerRoutineDTO) {
        // 해당 루틴과 운동 기구를 조회하여 엔티티로 변환
        Routine routine = routineService.getRoutineByCode(workoutPerRoutineDTO.getRoutineCode());
        ExerciseEquipment exerciseEquipment = exerciseEquipmentService.getEquipmentByCode(workoutPerRoutineDTO.getExerciseEquipmentCode());

        WorkoutPerRoutine routineEntity = WorkoutPerRoutine.builder()
                .workoutPerRoutineCode(workoutPerRoutineDTO.getWorkoutPerRoutineCode())
                .workoutOrder(workoutPerRoutineDTO.getWorkoutOrder())
                .workoutName(workoutPerRoutineDTO.getWorkoutName())
                .link(workoutPerRoutineDTO.getLink())
                .weightSet(workoutPerRoutineDTO.getWeightSet())
                .numberPerSet(workoutPerRoutineDTO.getNumberPerSet())
                .weightPerSet(workoutPerRoutineDTO.getWeightPerSet())
                .workoutTime(workoutPerRoutineDTO.getWorkoutTime())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .routineCode(routine)
                .exerciseEquipmentCode(exerciseEquipment)
                .build();

        workoutPerRoutineRepository.save(routineEntity);

        return modelMapper.map(routineEntity, ResponseInsertWorkoutPerRoutineVO.class);
    }


    // 루틴별 운동 수정
    @Override
    @Transactional
    public ResponseModifyWorkoutPerRoutineVO modifyWorkoutPerRoutine(Long workoutPerRoutineCode, EditWorkoutPerRoutineVO modifyRoutine) {
        WorkoutPerRoutine routine = workoutPerRoutineRepository.findById(workoutPerRoutineCode)
                .orElseThrow(() -> new CommonException(StatusEnum.ROUTINE_NOT_FOUND));
        routine.toUpdate(modifyRoutine);
        workoutPerRoutineRepository.save(routine);
        return modelMapper.map(routine, ResponseModifyWorkoutPerRoutineVO.class);
    }

    // 루틴별 운동 삭제
    @Override
    @Transactional
    public ResponseDeleteWorkoutPerRoutineVO deleteWorkoutPerRoutine(Long workoutPerRoutineCode) {
        WorkoutPerRoutine routine = workoutPerRoutineRepository.findById(workoutPerRoutineCode)
                .orElseThrow(() -> new CommonException(StatusEnum.ROUTINE_NOT_FOUND));
        workoutPerRoutineRepository.delete(routine);
        return new ResponseDeleteWorkoutPerRoutineVO();
    }

    @Override
    @Transactional
    public boolean checkForDuplicateRoutines(Map<String, Object> workoutData) {
        // 첫 번째 운동 순서와 운동 이름 가져오기
        Integer firstWorkoutOrder = (Integer) workoutData.get("workoutOrder1");
        String firstWorkoutName = (String) workoutData.get("workoutName1");

        if (firstWorkoutOrder == null || firstWorkoutName == null) {
            return false; // 첫 번째 운동 데이터가 없으면 중복 검사 불가
        }

        // 첫 번째 운동 정보로 운동 루틴 조회
        List<WorkoutPerRoutine> firstWorkouts = workoutPerRoutineRepository.findByWorkoutOrderAndWorkoutName(firstWorkoutOrder, firstWorkoutName);

        // 첫 번째 운동을 기반으로 루틴 코드를 얻어서 나머지 운동들과 비교
        for (WorkoutPerRoutine firstWorkoutRoutine : firstWorkouts) {
            Long routineCode = firstWorkoutRoutine.getRoutineCode().getRoutineCode();  // Long 타입으로 처리

            // 해당 루틴 코드로 전체 운동 리스트 조회
            List<WorkoutPerRoutine> workoutList = workoutPerRoutineRepository.findByRoutineCode_RoutineCode(routineCode);

            // workoutData와 조회된 루틴 크기 비교
            int expectedSize = workoutData.size() / 2; // workoutOrder와 workoutName의 페어로 구성된 데이터이므로 절반
            if (workoutList.size() != expectedSize) {
                continue; // 크기가 다르면 다음 루틴으로
            }

            boolean isDuplicate = true;
            for (int i = 1; i <= expectedSize; i++) {
                // Long 타입으로 변환
                Long currentWorkoutOrder = Long.valueOf((Integer) workoutData.get("workoutOrder" + i));
                String currentWorkoutName = (String) workoutData.get("workoutName" + i);

                // null 체크 추가
                if (currentWorkoutOrder == null || currentWorkoutName == null) {
                    isDuplicate = false;
                    break;
                }

                // 스트림을 사용하여 매칭 확인
                boolean foundMatch = workoutList.stream()
                        .anyMatch(routine -> routine.getWorkoutOrder() == currentWorkoutOrder.intValue() &&
                                routine.getWorkoutName().equals(currentWorkoutName));

                if (!foundMatch) {
                    isDuplicate = false;
                    break; // 매칭되지 않으면 중복이 아님
                }
            }

            if (isDuplicate) {
                return true; // 중복된 루틴 발견
            }
        }

        return false; // 중복된 루틴 없음
    }

    @Override
    public Long findRoutineCodeByWorkoutData(Map<String, Object> workoutData) {
        Integer firstWorkoutOrder = (Integer) workoutData.get("workoutOrder1");
        String firstWorkoutName = (String) workoutData.get("workoutName1");

        // 첫 번째 운동을 기준으로 루틴 코드 찾기
        List<WorkoutPerRoutine> firstWorkouts = workoutPerRoutineRepository.findByWorkoutOrderAndWorkoutName(firstWorkoutOrder, firstWorkoutName);
        if (!firstWorkouts.isEmpty()) {
            return firstWorkouts.get(0).getRoutineCode().getRoutineCode(); // 중복된 첫 번째 루틴의 코드를 반환
        }
        return null;
    }


}
