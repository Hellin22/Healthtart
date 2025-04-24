package com.dev5ops.healthtart.record_per_user.service;

import com.dev5ops.healthtart.record_per_user.domain.dto.RecordPerUserDTO;
import com.dev5ops.healthtart.record_per_user.domain.entity.RecordPerUser;
import com.dev5ops.healthtart.record_per_user.repository.RecordPerUserRepository;
import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RecordPerUserServiceTests {

    @Mock
    private RecordPerUserRepository recordPerUserRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RecordPerUserService recordPerUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("유저별 운동 기록 조회 성공")
    @Test
    void findRecordByUserCode_Success() {
        // given
        String userCode = "testUserCode";
        UserEntity mockUser = UserEntity.builder()
                .userCode(userCode)
                .build();

        RecordPerUser mockFirstRecordPerUser = RecordPerUser.builder()
                .userRecordCode(1L)
                .dayOfExercise(LocalDateTime.of(2024,10,9,0,0,0))
                .exerciseDuration(60)
                .recordFlag(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(mockUser)
                .RoutineCode(1L)
                .build();

        RecordPerUser mockSecondRecordPerUser = RecordPerUser.builder()
                .userRecordCode(2L)
                .dayOfExercise(LocalDateTime.of(2024,10,10,0,0,0))
                .exerciseDuration(45)
                .recordFlag(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(mockUser)
                .RoutineCode(1L)
                .build();

        List<RecordPerUser> mockRecordsPerUser = Arrays.asList(mockFirstRecordPerUser, mockSecondRecordPerUser);

        RecordPerUserDTO mockFirstRecordPerUserDTO = new RecordPerUserDTO(1L,
                LocalDateTime.of(2024,10,10,0,0,0),
                1,
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                userCode,
                1L);

        RecordPerUserDTO mockSecondRecordPerUserDTO = new RecordPerUserDTO(2L,
                LocalDateTime.of(2024,10,10,0,0,0),
                1,
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                userCode,
                1L);

        List<RecordPerUserDTO> mockRecordsPerUserDTO = Arrays.asList(mockFirstRecordPerUserDTO, mockSecondRecordPerUserDTO);

        // when
        when(recordPerUserRepository.findUserByUserCode(userCode)).thenReturn(mockRecordsPerUser);
        when(modelMapper.map(mockFirstRecordPerUser, RecordPerUserDTO.class)).thenReturn(mockFirstRecordPerUserDTO);
        when(modelMapper.map(mockSecondRecordPerUser, RecordPerUserDTO.class)).thenReturn(mockSecondRecordPerUserDTO);

        List<RecordPerUserDTO> actual = recordPerUserService.findRecordByUserCode(userCode);

        // then
        assertNotNull(actual);
        assertEquals(mockRecordsPerUserDTO, actual);
        verify(recordPerUserRepository, times(1)).findUserByUserCode(userCode);
        verify(modelMapper, times(2)).map(any(RecordPerUser.class), eq(RecordPerUserDTO.class));
    }

    @DisplayName("날짜별 운동 기록 조회 성공")
    @Test
    void findRecordPerDate_Success() {
        // given
        String userCode = "testUserCode";
        LocalDateTime dayOfExercise = LocalDateTime.of(2024, 10, 9,0,0);

        RecordPerUser mockFirstRecordPerUser = RecordPerUser.builder()
                .userRecordCode(1L)
                .dayOfExercise(dayOfExercise)
                .exerciseDuration(60)
                .recordFlag(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(UserEntity.builder().userCode(userCode).build())
                .RoutineCode(1L)
                .build();

        RecordPerUser mockSecondRecordPerUser = RecordPerUser.builder()
                .userRecordCode(2L)
                .dayOfExercise(dayOfExercise)
                .exerciseDuration(45)
                .recordFlag(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(UserEntity.builder().userCode(userCode).build())
                .RoutineCode(1L)
                .build();

        List<RecordPerUser> mockRecordsPerUser = Arrays.asList(mockFirstRecordPerUser, mockSecondRecordPerUser);

        RecordPerUserDTO mockFirstRecordPerUserDTO = new RecordPerUserDTO(1L,
                dayOfExercise,
                1,
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                userCode,
                1L);

        RecordPerUserDTO mockSecondRecordPerUserDTO = new RecordPerUserDTO(2L,
                dayOfExercise,
                1,
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                userCode,
                1L);

        List<RecordPerUserDTO> mockRecordsPerUserDTO = Arrays.asList(mockFirstRecordPerUserDTO, mockSecondRecordPerUserDTO);

        // when
        when(recordPerUserRepository.findByUser_UserCodeAndDayOfExercise(userCode, dayOfExercise)).thenReturn(mockRecordsPerUser);
        when(modelMapper.map(mockFirstRecordPerUser, RecordPerUserDTO.class)).thenReturn(mockFirstRecordPerUserDTO);
        when(modelMapper.map(mockSecondRecordPerUser, RecordPerUserDTO.class)).thenReturn(mockSecondRecordPerUserDTO);

        List<RecordPerUserDTO> actual = recordPerUserService.findRecordPerDate(userCode, dayOfExercise);

        // then
        assertNotNull(actual);
        assertEquals(mockRecordsPerUserDTO, actual);
        verify(recordPerUserRepository, times(1)).findByUser_UserCodeAndDayOfExercise(userCode, dayOfExercise);
        verify(modelMapper, times(2)).map(any(RecordPerUser.class), eq(RecordPerUserDTO.class));
    }
}
