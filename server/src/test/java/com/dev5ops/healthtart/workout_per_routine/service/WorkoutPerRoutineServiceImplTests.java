package com.dev5ops.healthtart.workout_per_routine.service;


import com.dev5ops.healthtart.exercise_equipment.domain.entity.ExerciseEquipment;
import com.dev5ops.healthtart.exercise_equipment.service.ExerciseEquipmentService;
import com.dev5ops.healthtart.routine.domain.entity.Routine;
import com.dev5ops.healthtart.routine.service.RoutineService;
import com.dev5ops.healthtart.workout_per_routine.domain.dto.WorkoutPerRoutineDTO;
import com.dev5ops.healthtart.workout_per_routine.domain.entity.WorkoutPerRoutine;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.EditWorkoutPerRoutineVO;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.response.ResponseFindWorkoutPerRoutineVO;
import com.dev5ops.healthtart.workout_per_routine.domain.vo.response.ResponseInsertWorkoutPerRoutineVO;
import com.dev5ops.healthtart.workout_per_routine.repository.WorkoutPerRoutineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkoutPerRoutineServiceImplTests {

    @InjectMocks
    private WorkoutPerRoutineServiceImpl workoutPerRoutineService;

    @Mock
    private WorkoutPerRoutineRepository workoutPerRoutineRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RoutineService routineService;

    @Mock
    private ExerciseEquipmentService exerciseEquipmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("운동 루틴 전체 조회 테스트")
    void getWorkoutPerRoutinesSuccess() {
        WorkoutPerRoutine routine = new WorkoutPerRoutine();
        when(workoutPerRoutineRepository.findAll()).thenReturn(List.of(routine));
        when(modelMapper.map(any(WorkoutPerRoutine.class), eq(ResponseFindWorkoutPerRoutineVO.class)))
                .thenReturn(new ResponseFindWorkoutPerRoutineVO());

        List<ResponseFindWorkoutPerRoutineVO> result = workoutPerRoutineService.getWorkoutPerRoutines();

        assertFalse(result.isEmpty());
        verify(workoutPerRoutineRepository).findAll();
    }

    @Test
    @DisplayName("운동 루틴 단일 조회 테스트")
    void findWorkoutPerRoutineSuccess() {
        WorkoutPerRoutine routine = new WorkoutPerRoutine();
        when(workoutPerRoutineRepository.findById(anyLong())).thenReturn(Optional.of(routine));
        when(modelMapper.map(any(WorkoutPerRoutine.class), eq(ResponseFindWorkoutPerRoutineVO.class)))
                .thenReturn(new ResponseFindWorkoutPerRoutineVO());

        ResponseFindWorkoutPerRoutineVO result = workoutPerRoutineService.findWorkoutPerRoutineByCode(1L);

        assertNotNull(result);
        verify(workoutPerRoutineRepository).findById(1L);
    }

    @Test
    @Transactional
    @DisplayName("운동 루틴별 운동 등록 테스트")
    void registerWorkoutPerRoutineSuccess() {
        WorkoutPerRoutineDTO workoutPerRoutineDTO = new WorkoutPerRoutineDTO();
        workoutPerRoutineDTO.setRoutineCode(1L);
        workoutPerRoutineDTO.setExerciseEquipmentCode(1L);
        workoutPerRoutineDTO.setWorkoutOrder(1);
        workoutPerRoutineDTO.setWorkoutName("스쿼트");
        workoutPerRoutineDTO.setLink("http://example.com/video");
        workoutPerRoutineDTO.setWeightSet(3);
        workoutPerRoutineDTO.setNumberPerSet(10);
        workoutPerRoutineDTO.setWeightPerSet(60);
        workoutPerRoutineDTO.setWorkoutTime(30);

        Routine routine = Routine.builder()
                .routineCode(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ExerciseEquipment exerciseEquipment = ExerciseEquipment.builder()
                .exerciseEquipmentCode(1L)
                .exerciseEquipmentName("덤벨")
                .build();

        WorkoutPerRoutine savedWorkoutPerRoutine = WorkoutPerRoutine.builder()
                .workoutPerRoutineCode(1L)
                .workoutOrder(1)
                .workoutName("스쿼트")
                .link("http://example.com/video")
                .weightSet(3)
                .numberPerSet(10)
                .weightPerSet(60)
                .workoutTime(30)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .routineCode(routine)
                .exerciseEquipmentCode(exerciseEquipment)
                .build();

        ResponseInsertWorkoutPerRoutineVO responseVO = new ResponseInsertWorkoutPerRoutineVO();

        when(routineService.getRoutineByCode(1L)).thenReturn(routine);
        when(exerciseEquipmentService.getEquipmentByCode(1L)).thenReturn(exerciseEquipment);
        when(workoutPerRoutineRepository.save(any(WorkoutPerRoutine.class))).thenReturn(savedWorkoutPerRoutine);
        when(modelMapper.map(any(WorkoutPerRoutine.class), any())).thenReturn(responseVO);

        ResponseInsertWorkoutPerRoutineVO result = workoutPerRoutineService.registerWorkoutPerRoutine(workoutPerRoutineDTO);

        assertNotNull(result);
        verify(routineService).getRoutineByCode(1L);
        verify(exerciseEquipmentService).getEquipmentByCode(1L);
        verify(workoutPerRoutineRepository).save(any(WorkoutPerRoutine.class));
        verify(modelMapper).map(any(WorkoutPerRoutine.class), any());
    }


    @Test
    @Transactional
    @DisplayName("운동 루틴 수정 테스트")
    void updateWorkoutPerRoutineSuccess() {
        WorkoutPerRoutine existingRoutine = WorkoutPerRoutine.builder()
                .workoutPerRoutineCode(1L)
                .workoutName("바벨 로우")
                .link("http://healthtart.com")
                .workoutOrder(1)
                .weightSet(3)
                .numberPerSet(10)
                .workoutTime(30)
                .build();

        when(workoutPerRoutineRepository.findById(1L)).thenReturn(Optional.of(existingRoutine));

        EditWorkoutPerRoutineVO editVO = new EditWorkoutPerRoutineVO(2,"데드리프트","http://healthtart.com", 4,
                12, 20, 40,LocalDateTime.now());

        workoutPerRoutineService.modifyWorkoutPerRoutine(1L, editVO);

        assertAll(
                () -> assertEquals(2, existingRoutine.getWorkoutOrder()),
                () -> assertEquals("데드리프트", existingRoutine.getWorkoutName()),
                () -> assertEquals("http://healthtart.com", existingRoutine.getLink()),
                () -> assertEquals(4, existingRoutine.getWeightSet()),
                () -> assertEquals(12, existingRoutine.getNumberPerSet()),
                () -> assertEquals(20, existingRoutine.getWeightPerSet()),
                () -> assertEquals(40, existingRoutine.getWorkoutTime())
        );
        verify(workoutPerRoutineRepository).save(existingRoutine);
    }


    @Test
    @Transactional
    @DisplayName("운동 루틴 삭제 테스트")
    void deleteWorkoutPerRoutineSuccess() {
        WorkoutPerRoutine routine = new WorkoutPerRoutine();
        when(workoutPerRoutineRepository.findById(anyLong())).thenReturn(Optional.of(routine));

        workoutPerRoutineService.deleteWorkoutPerRoutine(1L);

        verify(workoutPerRoutineRepository).delete(routine);
    }
}
