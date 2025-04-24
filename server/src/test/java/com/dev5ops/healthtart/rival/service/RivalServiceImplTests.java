package com.dev5ops.healthtart.rival.service;

import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.rival.domain.dto.RivalDTO;
import com.dev5ops.healthtart.rival.domain.entity.Rival;
import com.dev5ops.healthtart.rival.repository.RivalRepository;
import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import com.dev5ops.healthtart.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RivalServiceImplTests {

    @InjectMocks
    private RivalServiceImpl rivalService;

    @Mock
    private RivalRepository rivalRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("내 라이벌 조회 테스트")
    void testFindRivalMatch() {
        // Given
        String userCode = "test-user-code";

        RivalDTO rivalDTO = RivalDTO.builder()
                .rivalMatchCode(1L)
                .userCode(userCode)
                .rivalUserCode("rival-user-code")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(rivalRepository.findByUser_UserCode(userCode)).thenReturn(rivalDTO);

        RivalServiceImpl rivalServiceSpy = spy(rivalService);
        doReturn(userCode).when(rivalServiceSpy).getUserCode();

        // When
        RivalDTO actualRivalDTO = rivalServiceSpy.findRivalMatch();

        // Then
        assertNotNull(actualRivalDTO);
        assertEquals(1L, actualRivalDTO.getRivalMatchCode());
        assertEquals(userCode, actualRivalDTO.getUserCode());
        assertEquals("rival-user-code", actualRivalDTO.getRivalUserCode());

        verify(rivalRepository, times(1)).findByUser_UserCode(userCode);
    }

    @Test
    @DisplayName("라이벌 등록 테스트")
    void testInsertRival() {
        // Given
        String userCode = "test-user-code";
        String rivalUserCode = "rival-user-code";

        RivalServiceImpl rivalServiceSpy = spy(rivalService);
        doReturn(userCode).when(rivalServiceSpy).getUserCode();

        UserEntity user = UserEntity.builder()
                .userCode(userCode)
                .build();

        UserEntity rivalUser = UserEntity.builder()
                .userCode(rivalUserCode)
                .build();

        when(userRepository.findById(userCode)).thenReturn(Optional.of(user));
        when(userRepository.findById(rivalUserCode)).thenReturn(Optional.of(rivalUser));

        Rival rival = Rival.builder()
                .user(user)
                .rivalUser(rivalUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(rivalRepository.save(any(Rival.class))).thenReturn(rival);

        RivalDTO rivalDTO = RivalDTO.builder()
                .rivalMatchCode(1L)
                .userCode(userCode)
                .rivalUserCode(rivalUserCode)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(modelMapper.map(any(Rival.class), eq(RivalDTO.class))).thenReturn(rivalDTO);

        // When
        RivalDTO actualRivalDTO = rivalServiceSpy.insertRival(rivalUserCode);

        // Then
        assertNotNull(actualRivalDTO);
        assertEquals(userCode, actualRivalDTO.getUserCode());
        assertEquals(rivalUserCode, actualRivalDTO.getRivalUserCode());

        verify(userRepository, times(1)).findById(userCode);
        verify(userRepository, times(1)).findById(rivalUserCode);
        verify(rivalRepository, times(1)).save(any(Rival.class));
        verify(modelMapper, times(1)).map(any(Rival.class), eq(RivalDTO.class));
    }

    @Test
    @DisplayName("라이벌 등록 실패 테스트 - 유저 정보가 없을 때 예외 처리")
    void testInsertRival_UserNotFound() {
        // Given
        String userCode = "test-user-code";
        String rivalUserCode = "rival-user-code";

        RivalServiceImpl rivalServiceSpy = spy(rivalService);
        doReturn(userCode).when(rivalServiceSpy).getUserCode();

        when(userRepository.findById(userCode)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            rivalServiceSpy.insertRival(rivalUserCode);
        });

        verify(userRepository, times(1)).findById(userCode);
        verify(userRepository, never()).findById(rivalUserCode);
        verify(rivalRepository, never()).save(any(Rival.class));
    }


    @Test
    @DisplayName("라이벌 등록 실패 테스트 - 라이벌 정보가 없을 때 예외 처리")
    void testInsertRival_RivalUserNotFound() {
        // Given
        String userCode = "test-user-code";
        String rivalUserCode = "rival-user-code";

        RivalServiceImpl rivalServiceSpy = spy(rivalService);
        doReturn(userCode).when(rivalServiceSpy).getUserCode();

        UserEntity user = UserEntity.builder()
                .userCode(userCode)
                .build();
        when(userRepository.findById(userCode)).thenReturn(Optional.of(user));

        when(userRepository.findById(rivalUserCode)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            rivalServiceSpy.insertRival(rivalUserCode);
        });

        verify(userRepository, times(1)).findById(userCode);
        verify(userRepository, times(1)).findById(rivalUserCode);
        verify(rivalRepository, never()).save(any(Rival.class));
    }


    @Test
    @DisplayName("라이벌 삭제 테스트")
    void testDeleteRival() {
        // Given
        Long rivalMatchCode = 1L;

        Rival rival = Rival.builder()
                .rivalMatchCode(rivalMatchCode)
                .build();

        when(rivalRepository.findById(rivalMatchCode)).thenReturn(Optional.of(rival));

        // When
        rivalService.deleteRival(rivalMatchCode);

        // Then
        verify(rivalRepository, times(1)).delete(rival);
        verify(rivalRepository, times(1)).findById(rivalMatchCode);
    }

    @Test
    @DisplayName("라이벌 삭제 - 존재하지 않는 라이벌 예외 처리 테스트")
    void testDeleteRival_NotFound() {
        // Given
        Long rivalMatchCode = 1L;

        when(rivalRepository.findById(rivalMatchCode)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            rivalService.deleteRival(rivalMatchCode);
        });

        verify(rivalRepository, times(1)).findById(rivalMatchCode);
        verify(rivalRepository, never()).delete(any(Rival.class));
    }
}