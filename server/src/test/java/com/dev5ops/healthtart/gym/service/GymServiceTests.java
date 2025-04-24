package com.dev5ops.healthtart.gym.service;

import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.gym.domain.entity.Gym;
import com.dev5ops.healthtart.gym.domain.vo.request.RequestEditGymVO;
import com.dev5ops.healthtart.gym.domain.vo.request.RequestRegisterGymVO;
import com.dev5ops.healthtart.gym.domain.dto.GymDTO;
import com.dev5ops.healthtart.gym.repository.GymRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GymServiceTests {

    @Mock
    private GymRepository gymRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private GymService gymService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("헬스장 등록 성공")
    @Test
    void testRegisterGym_Success() {
        // Given
        RequestRegisterGymVO request = new RequestRegisterGymVO(
                "어게인짐 신대방삼거리역",
                "서울 동작구 상도로 83 2층 어게인짐",
                "110-81-34859"
        );

        GymDTO mockGymDTO = new GymDTO(
                null,
                request.getGymName(),
                request.getAddress(),
                request.getBusinessNumber(),
                null,
                null
        );

        when(modelMapper.map(request, GymDTO.class)).thenReturn(mockGymDTO);

        Gym mockGym = Gym.builder()
                .gymCode(1L)
                .gymName(request.getGymName())
                .address(request.getAddress())
                .businessNumber(request.getBusinessNumber())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(modelMapper.map(mockGymDTO, Gym.class)).thenReturn(mockGym);
        when(gymRepository.save(any(Gym.class))).thenReturn(mockGym);
        when(modelMapper.map(mockGym, GymDTO.class)).thenReturn(new GymDTO(
                1L,
                request.getGymName(),
                request.getAddress(),
                request.getBusinessNumber(),
                mockGym.getCreatedAt(),
                mockGym.getUpdatedAt()
        ));

        // When
        GymDTO responseDTO = gymService.registerGym(mockGymDTO);

        // Then
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getGymCode());
        assertEquals(request.getGymName(), responseDTO.getGymName());
        verify(gymRepository, times(1)).save(any(Gym.class));
    }

    @DisplayName("헬스장 등록 실패")
    @Test
    void testRegisterGym_DuplicateBusinessNumber() {
        // Given
        RequestRegisterGymVO request = new RequestRegisterGymVO(
                "어게인짐 신대방삼거리역",
                "서울 동작구 상도로 83 2층 어게인짐",
                "110-81-34859"
        );

        Gym existingGym = Gym.builder()
                .gymCode(1L)
                .gymName(request.getGymName())
                .address(request.getAddress())
                .businessNumber(request.getBusinessNumber())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(gymRepository.findByBusinessNumber(request.getBusinessNumber())).thenReturn(Optional.of(existingGym));

        GymDTO mockGymDTO = new GymDTO(
                null,
                request.getGymName(),
                request.getAddress(),
                request.getBusinessNumber(),
                null,
                null
        );

        when(modelMapper.map(any(RequestRegisterGymVO.class), eq(GymDTO.class))).thenReturn(mockGymDTO);

        Gym gymEntity = Gym.builder()
                .gymCode(1L)
                .gymName(request.getGymName())
                .address(request.getAddress())
                .businessNumber(request.getBusinessNumber())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(modelMapper.map(any(GymDTO.class), eq(Gym.class))).thenReturn(gymEntity);

        // When & Then
        CommonException exception = assertThrows(CommonException.class, () -> {
            gymService.registerGym(mockGymDTO);
        });

        assertEquals(StatusEnum.GYM_DUPLICATE, exception.getStatusEnum());

        verify(gymRepository, never()).save(any(Gym.class));
    }


    @DisplayName("헬스장 수정 성공")
    @Test
    void testEditGym_Success() {
        // Given
        Long gymCode = 1L;
        RequestEditGymVO request = new RequestEditGymVO(
                "수정된 헬스장 이름",
                "수정된 주소",
                "수정된 사업자번호"
        );

        Gym existingGym = Gym.builder()
                .gymCode(gymCode)
                .gymName("기존 헬스장 이름")
                .address("기존 주소")
                .businessNumber("기존 사업자번호")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(gymRepository.findById(gymCode)).thenReturn(Optional.of(existingGym));

        Gym updatedGym = Gym.builder()
                .gymCode(gymCode)
                .gymName(request.getGymName())
                .address(request.getAddress())
                .businessNumber(request.getBusinessNumber())
                .createdAt(existingGym.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(gymRepository.save(existingGym)).thenReturn(updatedGym);

        GymDTO updatedGymDTO = new GymDTO(
                gymCode,
                updatedGym.getGymName(),
                updatedGym.getAddress(),
                updatedGym.getBusinessNumber(),
                updatedGym.getCreatedAt(),
                updatedGym.getUpdatedAt()
        );

        when(modelMapper.map(updatedGym, GymDTO.class)).thenReturn(updatedGymDTO);

        // When
        GymDTO result = gymService.editGym(gymCode, request);

        // Then
        assertNotNull(result);
        assertEquals("수정된 헬스장 이름", result.getGymName());
        assertEquals("수정된 주소", result.getAddress());
        assertEquals("수정된 사업자번호", result.getBusinessNumber());
        assertNotNull(result.getUpdatedAt());
        verify(gymRepository, times(1)).save(existingGym);
    }

    @DisplayName("헬스장 수정 실패 - 헬스장을 찾지 못함")
    @Test
    void testEditGym_NotFound() {
        // Given
        Long gymCode = 1L;
        RequestEditGymVO request = new RequestEditGymVO(
                "수정된 헬스장 이름",
                "수정된 주소",
                "수정된 사업자번호"
        );

        // 헬스장을 찾지 못했을 때, Optional.empty() 반환하도록 설정
        when(gymRepository.findById(gymCode)).thenReturn(Optional.empty());

        // When & Then
        CommonException exception = assertThrows(CommonException.class, () -> {
            gymService.editGym(gymCode, request);
        });

        assertEquals(StatusEnum.GYM_NOT_FOUND, exception.getStatusEnum());

        // gymRepository.save()는 호출되지 않아야 함
        verify(gymRepository, never()).save(any(Gym.class));
    }


    @DisplayName("헬스장 삭제 성공")
    @Test
    void testDeleteGym_Success() {
        // Given
        Long gymCode = 1L;
        Gym existingGym = Gym.builder()
                .gymCode(gymCode)
                .gymName("기존 헬스장 이름")
                .address("기존 주소")
                .businessNumber("기존 사업자번호")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(gymRepository.findById(gymCode)).thenReturn(Optional.of(existingGym));

        // When
        gymService.deleteGym(gymCode);

        // Then
        verify(gymRepository, times(1)).delete(existingGym);
    }

    @DisplayName("헬스장 삭제 실패")
    @Test
    void testDeleteGym_NotFound() {
        // Given
        Long gymCode = 1L;

        when(gymRepository.findById(gymCode)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(CommonException.class, () -> {
            gymService.deleteGym(gymCode);
        });

        assertEquals(StatusEnum.GYM_NOT_FOUND, ((CommonException) exception).getStatusEnum());
        verify(gymRepository, never()).delete(any(Gym.class));
    }

    @DisplayName("헬스장 단일 조회 성공")
    @Test
    void testFindGymByGymCode_Success() {
        // Given
        Long gymCode = 1L;
        Gym existingGym = Gym.builder()
                .gymCode(gymCode)
                .gymName("testGymName")
                .address("testAddress")
                .businessNumber("000-00-00000")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        GymDTO mockGymDTO = new GymDTO(
                gymCode,
                existingGym.getGymName(),
                existingGym.getAddress(),
                existingGym.getBusinessNumber(),
                existingGym.getCreatedAt(),
                existingGym.getUpdatedAt()
        );

        when(gymRepository.findById(gymCode)).thenReturn(Optional.of(existingGym));
        when(modelMapper.map(existingGym, GymDTO.class)).thenReturn(mockGymDTO);

        // When
        GymDTO result = gymService.findGymByGymCode(gymCode);

        // Then
        assertNotNull(result);
        assertEquals(gymCode, result.getGymCode());
        assertEquals(existingGym.getGymName(), result.getGymName());
        verify(gymRepository, times(1)).findById(gymCode);
    }

    @DisplayName("헬스장 단일 조회 실패 - 헬스장을 찾지 못함")
    @Test
    void testFindGymByGymCode_NotFound() {
        // Given
        Long gymCode = 1L;
        when(gymRepository.findById(gymCode)).thenReturn(Optional.empty());

        // When & Then
        CommonException exception = assertThrows(CommonException.class, () -> {
            gymService.findGymByGymCode(gymCode);
        });

        assertEquals(StatusEnum.GYM_NOT_FOUND, exception.getStatusEnum());
        verify(gymRepository, times(1)).findById(gymCode);
    }

    @DisplayName("헬스장 전체 조회 성공")
    @Test
    void testFindAllGym_Success() {
        // Given
        List<Gym> gymList = new ArrayList<>();
        gymList.add(Gym.builder()
                .gymCode(1L)
                .gymName("testGymName1")
                .address("testAddress1")
                .businessNumber("000-00-00001")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        gymList.add(Gym.builder()
                .gymCode(2L)
                .gymName("testGymName2")
                .address("testAddress2")
                .businessNumber("000-00-00001")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        when(gymRepository.findAll()).thenReturn(gymList);

        when(modelMapper.map(gymList.get(0), GymDTO.class)).thenReturn(
                new GymDTO(1L, "testGymName1", "testAddress1", "000-00-00001", LocalDateTime.now(), LocalDateTime.now()));
        when(modelMapper.map(gymList.get(1), GymDTO.class)).thenReturn(
                new GymDTO(2L, "testGymName2", "testAddress2", "000-00-00002", LocalDateTime.now(), LocalDateTime.now()));

        // When
        List<GymDTO> result = gymService.findAllGym();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(gymRepository, times(1)).findAll();
    }

}
