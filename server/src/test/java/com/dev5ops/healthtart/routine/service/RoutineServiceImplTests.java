package com.dev5ops.healthtart.routine.service;

import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.routine.domain.dto.RoutineDTO;
import com.dev5ops.healthtart.routine.domain.entity.Routine;
import com.dev5ops.healthtart.routine.domain.vo.*;
import com.dev5ops.healthtart.routine.domain.vo.response.ResponseDeleteRoutineVO;
import com.dev5ops.healthtart.routine.domain.vo.response.ResponseFindRoutineVO;
import com.dev5ops.healthtart.routine.domain.vo.response.ResponseInsertRoutineVO;
import com.dev5ops.healthtart.routine.domain.vo.response.ResponseModifyRoutineVO;
import com.dev5ops.healthtart.routine.repository.RoutineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoutineServiceImplTests {

    @InjectMocks
    private RoutineServiceImpl routineService;

    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("루틴이 존재할 때 루틴 목록 조회 테스트")
    void getRoutinesSuccess() {
        Routine routine = new Routine();
        when(routineRepository.findAll()).thenReturn(List.of(routine));
        when(modelMapper.map(any(Routine.class), eq(ResponseFindRoutineVO.class)))
                .thenReturn(new ResponseFindRoutineVO());

        List<ResponseFindRoutineVO> result = routineService.getRoutines();

        assertFalse(result.isEmpty());
        verify(routineRepository).findAll();
    }

    @Test
    @DisplayName("루틴이 없을 때 예외 발생 테스트")
    void getRoutinesFail() {
        when(routineRepository.findAll()).thenReturn(List.of());

        CommonException exception = assertThrows(CommonException.class,
                () -> routineService.getRoutines());
        assertEquals(StatusEnum.ROUTINE_NOT_FOUND, exception.getStatusEnum());
    }

    @Test
    @DisplayName("단일 코드로 루틴 조회 테스트")
    void findRoutineSuccess() {
        Routine routine = new Routine();
        when(routineRepository.findById(anyLong())).thenReturn(Optional.of(routine));
        when(modelMapper.map(any(Routine.class), eq(ResponseFindRoutineVO.class)))
                .thenReturn(new ResponseFindRoutineVO());

        ResponseFindRoutineVO result = routineService.findRoutineByCode(1L);

        assertNotNull(result);
        verify(routineRepository).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 루틴을 조회하면 예외 발생 테스트")
    void findRoutineFail() {
        when(routineRepository.findById(anyLong())).thenReturn(Optional.empty());

        CommonException exception = assertThrows(CommonException.class,
                () -> routineService.findRoutineByCode(1L));
        assertEquals(StatusEnum.ROUTINE_NOT_FOUND, exception.getStatusEnum());
    }

    @Test
    @Transactional
    @DisplayName("루틴 등록 테스트")
    void registerRoutineSuccess() {
        Routine savedRoutine = Routine.builder()
                .routineCode(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        RoutineDTO routineDTO = new RoutineDTO();

        when(routineRepository.save(any(Routine.class))).thenReturn(savedRoutine);
        when(modelMapper.map(any(Routine.class), any())).thenReturn(routineDTO);

        RoutineDTO result = routineService.registerRoutine(routineDTO);

        assertNotNull(result);
        verify(routineRepository).save(any(Routine.class));
        verify(modelMapper).map(any(Routine.class), any());
    }



    @Test
    @Transactional
    @DisplayName("루틴 수정 테스트")
    void modifyRoutineSuccess() {
        Long routineCode = 1L;

        EditRoutineVO modifyRoutine = new EditRoutineVO(LocalDateTime.now());

        Routine routine = new Routine();
        when(routineRepository.findById(routineCode)).thenReturn(Optional.of(routine));

        ResponseModifyRoutineVO responseVO = new ResponseModifyRoutineVO();
        when(modelMapper.map(any(Routine.class), eq(ResponseModifyRoutineVO.class)))
                .thenReturn(responseVO);

        ResponseModifyRoutineVO result = routineService.modifyRoutine(routineCode, modifyRoutine);

        assertNotNull(result);
        verify(routineRepository).findById(routineCode);
        verify(routineRepository).save(any(Routine.class));
        verify(modelMapper).map(any(Routine.class), eq(ResponseModifyRoutineVO.class));
    }


    @Test
    @DisplayName("루틴 삭제 성공 테스트")
    void deleteRoutineSuccess() {
        // given
        Long routineCode = 1L;
        Routine routine = new Routine();

        // when
        when(routineRepository.findById(routineCode)).thenReturn(Optional.of(routine));

        // 서비스 메서드 호출
        routineService.deleteRoutine(routineCode);

        // then
        verify(routineRepository).findById(routineCode);
        verify(routineRepository).delete(routine);
    }
}
