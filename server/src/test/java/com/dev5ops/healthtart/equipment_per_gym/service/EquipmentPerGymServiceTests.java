package com.dev5ops.healthtart.equipment_per_gym.service;

import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.equipment_per_gym.domain.entity.EquipmentPerGym;
import com.dev5ops.healthtart.equipment_per_gym.domain.vo.request.RequestEditEquipmentPerGymVO;
import com.dev5ops.healthtart.equipment_per_gym.domain.dto.EquipmentPerGymDTO;
import com.dev5ops.healthtart.equipment_per_gym.repository.EquipmentPerGymRepository;
import com.dev5ops.healthtart.exercise_equipment.domain.entity.ExerciseEquipment;
import com.dev5ops.healthtart.exercise_equipment.repository.ExerciseEquipmentRepository;
import com.dev5ops.healthtart.gym.domain.entity.Gym;
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

class EquipmentPerGymServiceTests {

    @Mock
    private GymRepository gymRepository;

    @Mock
    private ExerciseEquipmentRepository exerciseEquipmentRepository;

    @Mock
    private EquipmentPerGymRepository equipmentPerGymRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EquipmentPerGymService equipmentPerGymService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("운동기구 등록 성공 - 헬스장과 운동기구가 존재")
    @Test
    void testRegisterEquipmentPerGym_Success() {
        // Given
        EquipmentPerGymDTO requestDTO = new EquipmentPerGymDTO(
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Gym(1L, "TestGym", "TestAddress", "000-00-00000", LocalDateTime.now(), LocalDateTime.now()),
                new ExerciseEquipment(1L, "TestEquipment", "이두", "TestDescription", "TestImage", "TestVideo", LocalDateTime.now(), LocalDateTime.now())
        );

        Gym mockGym = Gym.builder()
                .gymCode(1L)
                .gymName("TestGym")
                .address("TestAddress")
                .businessNumber("000-00-00000")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ExerciseEquipment mockEquipment = ExerciseEquipment.builder()
                .exerciseEquipmentCode(1L)
                .exerciseEquipmentName("TestEquipment")
                .bodyPart("이두")
                .exerciseDescription("TestDescription")
                .exerciseImage("TestImage")
                .recommendedVideo("TestVideo")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        EquipmentPerGym mockEquipmentPerGym = EquipmentPerGym.builder()
                .equipmentPerGymCode(1L)
                .gym(mockGym)
                .exerciseEquipment(mockEquipment)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(gymRepository.findById(1L)).thenReturn(Optional.of(mockGym));
        when(exerciseEquipmentRepository.findById(1L)).thenReturn(Optional.of(mockEquipment));
        when(modelMapper.map(requestDTO, EquipmentPerGym.class)).thenReturn(mockEquipmentPerGym);
        when(equipmentPerGymRepository.save(any(EquipmentPerGym.class))).thenReturn(mockEquipmentPerGym);
        when(modelMapper.map(mockEquipmentPerGym, EquipmentPerGymDTO.class)).thenReturn(requestDTO);

        // When
        EquipmentPerGymDTO responseDTO = equipmentPerGymService.registerEquipmentPerGym(requestDTO);

        // Then
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getGym().getGymCode());
        assertEquals(1L, responseDTO.getExerciseEquipment().getExerciseEquipmentCode());
        verify(equipmentPerGymRepository, times(1)).save(any(EquipmentPerGym.class));
    }

    @DisplayName("운동기구 등록 실패 - 헬스장을 찾을 수 없음")
    @Test
    void testRegisterEquipmentPerGym_GymNotFound() {
        // Given
        EquipmentPerGymDTO requestDTO = new EquipmentPerGymDTO(
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Gym(1L, "TestGym", "TestAddress", "000-00-00000", LocalDateTime.now(), LocalDateTime.now()),
                new ExerciseEquipment(1L, "TestEquipment", "이두", "TestDescription", "TestImage", "TestVideo", LocalDateTime.now(), LocalDateTime.now())
        );

        when(gymRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        CommonException exception = assertThrows(CommonException.class, () -> {
            equipmentPerGymService.registerEquipmentPerGym(requestDTO);
        });

        assertEquals(StatusEnum.GYM_NOT_FOUND, exception.getStatusEnum());
        verify(equipmentPerGymRepository, never()).save(any(EquipmentPerGym.class));
    }

    @DisplayName("운동기구 등록 실패 - 운동기구를 찾을 수 없음")
    @Test
    void testRegisterEquipmentPerGym_EquipmentNotFound() {
        // Given
        EquipmentPerGymDTO requestDTO = new EquipmentPerGymDTO(
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new Gym(1L, "TestGym", "TestAddress", "000-00-00000", LocalDateTime.now(), LocalDateTime.now()),
                new ExerciseEquipment(1L, "TestEquipment", "이두", "TestDescription", "TestImage", "TestVideo", LocalDateTime.now(), LocalDateTime.now())
        );

        Gym mockGym = Gym.builder()
                .gymCode(1L)
                .gymName("TestGym")
                .address("TestAddress")
                .businessNumber("000-00-00000")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(gymRepository.findById(1L)).thenReturn(Optional.of(mockGym));
        when(exerciseEquipmentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        CommonException exception = assertThrows(CommonException.class, () -> {
            equipmentPerGymService.registerEquipmentPerGym(requestDTO);
        });

        assertEquals(StatusEnum.EQUIPMENT_NOT_FOUND, exception.getStatusEnum());
        verify(equipmentPerGymRepository, never()).save(any(EquipmentPerGym.class));
    }

    @DisplayName("헬스장 별 운동기구 수정 성공")
    @Test
    void testEditEquipmentPerGym_Success() {
        // Given
        Long equipmentPerGymCode = 1L;

        RequestEditEquipmentPerGymVO request = new RequestEditEquipmentPerGymVO(
                Gym.builder().gymCode(1L).build(),
                ExerciseEquipment.builder().exerciseEquipmentCode(2L).build()
        );

        // 기존 데이터
        Gym existingGym = Gym.builder()
                .gymCode(1L)
                .gymName("Old Gym")
                .address("Old Gym Address")
                .businessNumber("000-00-00000")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ExerciseEquipment existingEquipment = ExerciseEquipment.builder()
                .exerciseEquipmentCode(1L)
                .exerciseEquipmentName("Old Equipment")
                .bodyPart("Arms")
                .exerciseDescription("Old description")
                .exerciseImage("old image url")
                .recommendedVideo("old video url")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 업데이트 할 데이터
        Gym updatedGym = Gym.builder()
                .gymCode(request.getGym().getGymCode())
                .gymName("New Gym")
                .address("New Gym Address")
                .businessNumber("111-11-11111")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ExerciseEquipment updatedEquipment = ExerciseEquipment.builder()
                .exerciseEquipmentCode(request.getExerciseEquipment().getExerciseEquipmentCode())
                .exerciseEquipmentName("New Equipment")
                .bodyPart("Arms")
                .exerciseDescription("Updated description")
                .exerciseImage("new image url")
                .recommendedVideo("new video url")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 기존 헬스장 별 운동 기구
        EquipmentPerGym existingEquipmentPerGym = EquipmentPerGym.builder()
                .equipmentPerGymCode(equipmentPerGymCode)
                .gym(existingGym)
                .exerciseEquipment(existingEquipment)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 업데이트 된 헬스장 별 운동 기구
        EquipmentPerGym updatedEquipmentPerGym = EquipmentPerGym.builder()
                .equipmentPerGymCode(equipmentPerGymCode)
                .gym(updatedGym)
                .exerciseEquipment(updatedEquipment)
                .createdAt(existingEquipmentPerGym.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(gymRepository.findById(request.getGym().getGymCode())).thenReturn(Optional.of(updatedGym));
        when(exerciseEquipmentRepository.findById(request.getExerciseEquipment().getExerciseEquipmentCode())).thenReturn(Optional.of(updatedEquipment));
        when(equipmentPerGymRepository.findById(equipmentPerGymCode)).thenReturn(Optional.of(existingEquipmentPerGym));

        when(equipmentPerGymRepository.save(existingEquipmentPerGym)).thenReturn(updatedEquipmentPerGym);

        System.out.println("existingGym = " + existingGym);
        System.out.println("existingEquipment = " + existingEquipment);
        System.out.println("existingEquipmentPerGym = " + existingEquipmentPerGym);
        System.out.println("====================================================");
        System.out.println("Updated Gym: " + updatedGym);
        System.out.println("Updated Equipment: " + updatedEquipment);
        System.out.println("Saved EquipmentPerGym: " + updatedEquipmentPerGym);

        when(modelMapper.map(updatedEquipmentPerGym, EquipmentPerGymDTO.class)).thenReturn(new EquipmentPerGymDTO(
                equipmentPerGymCode,
                updatedEquipmentPerGym.getCreatedAt(),
                updatedEquipmentPerGym.getUpdatedAt(),
                updatedGym,
                updatedEquipment
        ));

        // When
        EquipmentPerGymDTO result = equipmentPerGymService.editEquipmentPerGym(equipmentPerGymCode, request);

        // Then
        assertNotNull(result);
        assertEquals("New Gym", result.getGym().getGymName());
        assertEquals("New Equipment", result.getExerciseEquipment().getExerciseEquipmentName());
        assertEquals("Updated description", result.getExerciseEquipment().getExerciseDescription());
        verify(equipmentPerGymRepository, times(1)).save(existingEquipmentPerGym);
    }

    @DisplayName("헬스장 별 운동기구 수정 실패 - 헬스장을 찾을 수 없음")
    @Test
    void testEditEquipmentPerGym_GymNotFound() {
        // Given
        Long equipmentPerGymCode = 1L;

        RequestEditEquipmentPerGymVO request = new RequestEditEquipmentPerGymVO(
                Gym.builder().gymCode(1L).build(),
                ExerciseEquipment.builder().exerciseEquipmentCode(2L).build()
        );

        when(gymRepository.findById(request.getGym().getGymCode())).thenReturn(Optional.empty());

        // When & Then
        CommonException exception = assertThrows(CommonException.class, () -> {
            equipmentPerGymService.editEquipmentPerGym(equipmentPerGymCode, request);
        });

        assertEquals(StatusEnum.GYM_NOT_FOUND, exception.getStatusEnum());
        verify(gymRepository, times(1)).findById(request.getGym().getGymCode());
        verify(equipmentPerGymRepository, never()).save(any(EquipmentPerGym.class));
    }

    @DisplayName("헬스장 별 운동기구 수정 실패 - 운동기구를 찾을 수 없음")
    @Test
    void testEditEquipmentPerGym_EquipmentNotFound() {
        // Given
        Long equipmentPerGymCode = 1L;

        RequestEditEquipmentPerGymVO request = new RequestEditEquipmentPerGymVO(
                Gym.builder().gymCode(1L).build(),
                ExerciseEquipment.builder().exerciseEquipmentCode(2L).build()
        );

        when(gymRepository.findById(request.getGym().getGymCode())).thenReturn(Optional.of(new Gym()));
        when(exerciseEquipmentRepository.findById(request.getExerciseEquipment().getExerciseEquipmentCode())).thenReturn(Optional.empty());

        // When & Then
        CommonException exception = assertThrows(CommonException.class, () -> {
            equipmentPerGymService.editEquipmentPerGym(equipmentPerGymCode, request);
        });

        assertEquals(StatusEnum.EQUIPMENT_NOT_FOUND, exception.getStatusEnum());
        verify(exerciseEquipmentRepository, times(1)).findById(request.getExerciseEquipment().getExerciseEquipmentCode());
        verify(equipmentPerGymRepository, never()).save(any(EquipmentPerGym.class));
    }

    @DisplayName("헬스장 별 운동기구 수정 실패 - EquipmentPerGym을 찾을 수 없음")
    @Test
    void testEditEquipmentPerGym_EquipmentPerGymNotFound() {
        // Given
        Long equipmentPerGymCode = 1L;

        RequestEditEquipmentPerGymVO request = new RequestEditEquipmentPerGymVO(
                Gym.builder().gymCode(1L).build(),
                ExerciseEquipment.builder().exerciseEquipmentCode(2L).build()
        );

        when(gymRepository.findById(request.getGym().getGymCode())).thenReturn(Optional.of(new Gym()));
        when(exerciseEquipmentRepository.findById(request.getExerciseEquipment().getExerciseEquipmentCode())).thenReturn(Optional.of(new ExerciseEquipment()));
        when(equipmentPerGymRepository.findById(equipmentPerGymCode)).thenReturn(Optional.empty());

        // When & Then
        CommonException exception = assertThrows(CommonException.class, () -> {
            equipmentPerGymService.editEquipmentPerGym(equipmentPerGymCode, request);
        });

        assertEquals(StatusEnum.EQUIPMENT_PER_GYM_NOT_FOUND, exception.getStatusEnum());
        verify(equipmentPerGymRepository, times(1)).findById(equipmentPerGymCode);
        verify(equipmentPerGymRepository, never()).save(any(EquipmentPerGym.class));
    }

    @DisplayName("헬스장 별 운동기구 삭제 성공")
    @Test
    void testDeleteEquipmentPerGym_Success() {
        // Given
        Long equipmentPerGymCode = 1L;

        Gym existingGym = Gym.builder()
                .gymCode(1L)
                .gymName("Old Gym")
                .address("Old Gym Address")
                .businessNumber("000-00-00000")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ExerciseEquipment existingEquipment = ExerciseEquipment.builder()
                .exerciseEquipmentCode(1L)
                .exerciseEquipmentName("Old Equipment")
                .bodyPart("Arms")
                .exerciseDescription("Old description")
                .exerciseImage("old image url")
                .recommendedVideo("old video url")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        EquipmentPerGym existingEquipmentPerGym = EquipmentPerGym.builder()
                .equipmentPerGymCode(equipmentPerGymCode)
                .gym(existingGym)
                .exerciseEquipment(existingEquipment)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(equipmentPerGymRepository.findById(equipmentPerGymCode)).thenReturn(Optional.of(existingEquipmentPerGym));
        when(gymRepository.findById(existingGym.getGymCode())).thenReturn(Optional.of(existingGym));
        when(exerciseEquipmentRepository.findById(existingEquipment.getExerciseEquipmentCode())).thenReturn(Optional.of(existingEquipment));

        // When
        equipmentPerGymService.deleteEquipmentPerGym(equipmentPerGymCode);

        // Then
        verify(equipmentPerGymRepository, times(1)).delete(existingEquipmentPerGym);
    }

//    @DisplayName("헬스장 별 운동기구 삭제 실패 - EquipmentPerGym을 찾을 수 없음")
//    @Test
//    void testDeleteEquipmentPerGym_NotFound() {
//        // Given
//        Long equipmentPerGymCode = 1L;
//
//        when(equipmentPerGymRepository.findById(equipmentPerGymCode)).thenReturn(Optional.empty());
//
//        // When & Then
//        CommonException exception = assertThrows(CommonException.class, () -> {
//            equipmentPerGymService.deleteEquipmentPerGym(equipmentPerGymCode);
//        });
//
//        assertEquals(StatusEnum.EQUIPMENT_PER_GYM_NOT_FOUND, exception.getStatusEnum());
//        verify(equipmentPerGymRepository, never()).delete(any(EquipmentPerGym.class));
//    }

    @DisplayName("헬스장 별 운동기구 삭제 실패 - 헬스장을 찾을 수 없음")
    @Test
    void testDeleteEquipmentPerGym_GymNotFound() {
        // Given
        Long equipmentPerGymCode = 1L;

        Gym existingGym = Gym.builder().gymCode(1L).build();
        ExerciseEquipment existingEquipment = ExerciseEquipment.builder().exerciseEquipmentCode(1L).build();
        EquipmentPerGym existingEquipmentPerGym = EquipmentPerGym.builder()
                .equipmentPerGymCode(equipmentPerGymCode)
                .gym(existingGym)
                .exerciseEquipment(existingEquipment)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(equipmentPerGymRepository.findById(equipmentPerGymCode)).thenReturn(Optional.of(existingEquipmentPerGym));
        when(gymRepository.findById(existingGym.getGymCode())).thenReturn(Optional.empty());

        // When & Then
        CommonException exception = assertThrows(CommonException.class, () -> {
            equipmentPerGymService.deleteEquipmentPerGym(equipmentPerGymCode);
        });

        assertEquals(StatusEnum.GYM_NOT_FOUND, exception.getStatusEnum());
        verify(equipmentPerGymRepository, never()).delete(any(EquipmentPerGym.class));
    }

    @DisplayName("헬스장 별 운동기구 삭제 실패 - 운동기구를 찾을 수 없음")
    @Test
    void testDeleteEquipmentPerGym_EquipmentNotFound() {
        // Given
        Long equipmentPerGymCode = 1L;

        Gym existingGym = Gym.builder().gymCode(1L).build();
        ExerciseEquipment existingEquipment = ExerciseEquipment.builder().exerciseEquipmentCode(1L).build();
        EquipmentPerGym existingEquipmentPerGym = EquipmentPerGym.builder()
                .equipmentPerGymCode(equipmentPerGymCode)
                .gym(existingGym)
                .exerciseEquipment(existingEquipment)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(equipmentPerGymRepository.findById(equipmentPerGymCode)).thenReturn(Optional.of(existingEquipmentPerGym));
        when(gymRepository.findById(existingGym.getGymCode())).thenReturn(Optional.of(existingGym));
        when(exerciseEquipmentRepository.findById(existingEquipment.getExerciseEquipmentCode())).thenReturn(Optional.empty());

        // When & Then
        CommonException exception = assertThrows(CommonException.class, () -> {
            equipmentPerGymService.deleteEquipmentPerGym(equipmentPerGymCode);
        });

        assertEquals(StatusEnum.EQUIPMENT_NOT_FOUND, exception.getStatusEnum());
        verify(equipmentPerGymRepository, never()).delete(any(EquipmentPerGym.class));
    }

    @DisplayName("헬스장 별 운동기구 단건 조회 성공")
    @Test
    void testFindEquipmentPerGymByCode_Success() {
        // Given
        Long equipmentPerGymCode = 1L;

        Gym existingGym = Gym.builder()
                .gymCode(1L)
                .gymName("Test Gym")
                .address("Test Address")
                .businessNumber("000-00-00000")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ExerciseEquipment existingEquipment = ExerciseEquipment.builder()
                .exerciseEquipmentCode(1L)
                .exerciseEquipmentName("Test Equipment")
                .bodyPart("Arms")
                .exerciseDescription("Test Description")
                .exerciseImage("image url")
                .recommendedVideo("video url")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        EquipmentPerGym existingEquipmentPerGym = EquipmentPerGym.builder()
                .equipmentPerGymCode(equipmentPerGymCode)
                .gym(existingGym)
                .exerciseEquipment(existingEquipment)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Mock: equipmentPerGymRepository.findById()
        when(equipmentPerGymRepository.findById(equipmentPerGymCode)).thenReturn(Optional.of(existingEquipmentPerGym));

        when(modelMapper.map(existingEquipmentPerGym, EquipmentPerGymDTO.class)).thenReturn(new EquipmentPerGymDTO(
                equipmentPerGymCode,
                existingEquipmentPerGym.getCreatedAt(),
                existingEquipmentPerGym.getUpdatedAt(),
                existingGym,
                existingEquipment
        ));

        // When
        EquipmentPerGymDTO result = equipmentPerGymService.findEquipmentPerGymByCode(equipmentPerGymCode);

        // Then
        assertNotNull(result);
        assertEquals("Test Gym", result.getGym().getGymName());
        assertEquals("Test Equipment", result.getExerciseEquipment().getExerciseEquipmentName());
        verify(equipmentPerGymRepository, times(1)).findById(equipmentPerGymCode);
    }

    @DisplayName("헬스장 별 운동기구 단건 조회 실패 - NotFound")
    @Test
    void testFindEquipmentPerGymByCode_NotFound() {
        // Given
        Long equipmentPerGymCode = 1L;

        when(equipmentPerGymRepository.findById(equipmentPerGymCode)).thenReturn(Optional.empty());

        // When & Then
        CommonException exception = assertThrows(CommonException.class, () -> {
            equipmentPerGymService.findEquipmentPerGymByCode(equipmentPerGymCode);
        });

        assertEquals(StatusEnum.EQUIPMENT_PER_GYM_NOT_FOUND, exception.getStatusEnum());
        verify(equipmentPerGymRepository, times(1)).findById(equipmentPerGymCode);
    }

    @DisplayName("헬스장 별 운동기구 전체 조회 성공")
    @Test
    void testFindAllEquipmentPerGym_Success() {
        // Given
        List<EquipmentPerGym> equipmentPerGymList = new ArrayList<>();

        Gym gym1 = Gym.builder().gymCode(1L).gymName("TestGym1").build();
        ExerciseEquipment equipment1 = ExerciseEquipment.builder().exerciseEquipmentCode(1L).exerciseEquipmentName("TestEquipment1").build();
        EquipmentPerGym equipmentPerGym1 = EquipmentPerGym.builder().equipmentPerGymCode(1L).gym(gym1).exerciseEquipment(equipment1).build();

        Gym gym2 = Gym.builder().gymCode(2L).gymName("TestGym2").build();
        ExerciseEquipment equipment2 = ExerciseEquipment.builder().exerciseEquipmentCode(2L).exerciseEquipmentName("TestEquipment2").build();
        EquipmentPerGym equipmentPerGym2 = EquipmentPerGym.builder().equipmentPerGymCode(2L).gym(gym2).exerciseEquipment(equipment2).build();

        equipmentPerGymList.add(equipmentPerGym1);
        equipmentPerGymList.add(equipmentPerGym2);

        when(equipmentPerGymRepository.findAll()).thenReturn(equipmentPerGymList);

        when(modelMapper.map(equipmentPerGym1, EquipmentPerGymDTO.class)).thenReturn(new EquipmentPerGymDTO(
                1L,
                equipmentPerGym1.getCreatedAt(),
                equipmentPerGym1.getUpdatedAt(),
                gym1,
                equipment1
        ));

        when(modelMapper.map(equipmentPerGym2, EquipmentPerGymDTO.class)).thenReturn(new EquipmentPerGymDTO(
                2L,
                equipmentPerGym2.getCreatedAt(),
                equipmentPerGym2.getUpdatedAt(),
                gym2,
                equipment2
        ));

        // When
        List<EquipmentPerGymDTO> result = equipmentPerGymService.findAllEquipmentPer();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("TestGym1", result.get(0).getGym().getGymName());
        assertEquals("TestEquipment1", result.get(0).getExerciseEquipment().getExerciseEquipmentName());
        assertEquals("TestGym2", result.get(1).getGym().getGymName());
        assertEquals("TestEquipment2", result.get(1).getExerciseEquipment().getExerciseEquipmentName());
        verify(equipmentPerGymRepository, times(1)).findAll();
    }

    @DisplayName("부위별 운동기구 조회 성공")
    @Test
    void testFindEquipmentByBodyPart_Success() {
        // Given
        Long gymCode = 1L;
        String bodyPart = "이두";

        Gym gym = Gym.builder().gymCode(gymCode).gymName("Test Gym").build();
        ExerciseEquipment exerciseEquipment = ExerciseEquipment.builder()
                .exerciseEquipmentCode(1L)
                .exerciseEquipmentName("Test Equipment")
                .bodyPart(bodyPart)
                .build();

        EquipmentPerGym equipmentPerGym = EquipmentPerGym.builder()
                .equipmentPerGymCode(1L)
                .gym(gym)
                .exerciseEquipment(exerciseEquipment)
                .build();

        List<EquipmentPerGym> equipmentPerGymList = new ArrayList<>();
        equipmentPerGymList.add(equipmentPerGym);

        when(equipmentPerGymRepository.findByGym_GymCodeAndExerciseEquipment_BodyPart(gymCode, bodyPart)).thenReturn(equipmentPerGymList);
        when(modelMapper.map(equipmentPerGym, EquipmentPerGymDTO.class)).thenReturn(
                new EquipmentPerGymDTO(1L, LocalDateTime.now(), LocalDateTime.now(), gym, exerciseEquipment)
        );

        // When
        List<EquipmentPerGymDTO> result = equipmentPerGymService.findEquipmentByBodyPart(gymCode, bodyPart);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bodyPart, result.get(0).getExerciseEquipment().getBodyPart());
        verify(equipmentPerGymRepository, times(1)).findByGym_GymCodeAndExerciseEquipment_BodyPart(gymCode, bodyPart);
    }

    @DisplayName("부위별 운동기구 조회 실패 - 해당 부위 운동기구가 없음")
    @Test
    void testFindEquipmentByBodyPart_NotFound() {
        // Given
        Long gymCode = 1L;
        String bodyPart = "등";

        // 헬스장 코드와 부위를 기준으로 조회할 때 빈 리스트 반환
        when(equipmentPerGymRepository.findByGym_GymCodeAndExerciseEquipment_BodyPart(gymCode, bodyPart)).thenReturn(new ArrayList<>());

        // When
        List<EquipmentPerGymDTO> result = equipmentPerGymService.findEquipmentByBodyPart(gymCode, bodyPart);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(equipmentPerGymRepository, times(1)).findByGym_GymCodeAndExerciseEquipment_BodyPart(gymCode, bodyPart);
    }

}