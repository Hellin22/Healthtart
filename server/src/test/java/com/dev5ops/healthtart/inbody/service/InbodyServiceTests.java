package com.dev5ops.healthtart.inbody.service;

import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.inbody.aggregate.Inbody;
import com.dev5ops.healthtart.inbody.aggregate.vo.request.RequestEditInbodyVO;
import com.dev5ops.healthtart.inbody.aggregate.vo.request.RequestRegisterInbodyVO;
import com.dev5ops.healthtart.inbody.dto.InbodyDTO;
import com.dev5ops.healthtart.inbody.repository.InbodyRepository;
import com.dev5ops.healthtart.user.domain.UserTypeEnum;
import com.dev5ops.healthtart.user.domain.dto.UserDTO;
import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import com.dev5ops.healthtart.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InbodyServiceTests {

    @InjectMocks
    private InbodyService inbodyService;

    @Mock
    private UserService userService;

    @Mock
    private InbodyRepository inbodyRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("인바디 등록 성공")
    @Test
    void testRegisterInbody_Success() {
        // Given
        String userCode = "test";
        RequestRegisterInbodyVO request = new RequestRegisterInbodyVO(85, 70.5, 175.0, 30.0, 15.0, 22.9, 18.0, LocalDateTime.now(), 1600, userCode);

        UserEntity userEntity = new UserEntity(userCode, UserTypeEnum.MEMBER, "test", "test@test.com", "test", "test", "test", "test", true, "M", 123.1, 123.1, 11, null, null, LocalDateTime.now(), LocalDateTime.now(), null);
        UserDTO userDTO = new UserDTO(
                userEntity.getUserCode(),
                String.valueOf(userEntity.getUserType()),
                userEntity.getUserName(),
                userEntity.getUserEmail(),
                userEntity.getUserPassword(),
                userEntity.getUserPhone(),
                userEntity.getUserNickname(),
                userEntity.getUserAddress(),
                true,
                userEntity.getUserGender(),
                request.getHeight(),
                request.getWeight(),
                userEntity.getUserAge(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        InbodyDTO inbodyDTO = new InbodyDTO(null, request.getInbodyScore(), request.getWeight(), request.getHeight(), request.getMuscleWeight(), request.getFatWeight(), request.getBmi(), request.getFatPercentage(), request.getDayOfInbody(), request.getBasalMetabolicRate(), LocalDateTime.now(), LocalDateTime.now(), userEntity, request.getUserCode());

        Inbody mockInbody = Inbody.builder()
                .inbodyCode(1L)
                .inbodyScore(inbodyDTO.getInbodyScore())
                .weight(inbodyDTO.getWeight())
                .height(inbodyDTO.getHeight())
                .muscleWeight(inbodyDTO.getMuscleWeight())
                .fatWeight(inbodyDTO.getFatWeight())
                .bmi(inbodyDTO.getBmi())
                .fatPercentage(inbodyDTO.getFatPercentage())
                .dayOfInbody(inbodyDTO.getDayOfInbody())
                .basalMetabolicRate(inbodyDTO.getBasalMetabolicRate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(userEntity)
                .build();

        when(userService.findById(userCode)).thenReturn(userDTO);
        when(inbodyRepository.save(any(Inbody.class))).thenReturn(mockInbody);
        when(modelMapper.map(mockInbody, InbodyDTO.class)).thenReturn(inbodyDTO);

        // When
        InbodyDTO result = inbodyService.registerInbody(inbodyDTO);

        // Then
        assertNotNull(result);
        assertEquals(85, result.getInbodyScore());
        assertEquals(70.5, result.getWeight());
        assertEquals(175.0, result.getHeight());
        assertEquals(1600, result.getBasalMetabolicRate());
        assertNotNull(result.getUser());

        // Verify save() 호출 여부 확인
        verify(inbodyRepository, times(1)).save(any(Inbody.class));
        verify(userService, times(1)).findById(userCode);
    }

    @DisplayName("인바디 등록 실패 - 중복된 날짜")
    @Test
    void testRegisterInbody_Failure() {
        // Given
        LocalDateTime duplicateDay = LocalDateTime.now();

        String userCode = "test";
        RequestRegisterInbodyVO request = new RequestRegisterInbodyVO(85, 70.5, 175.0, 30.0, 15.0, 22.9, 18.0, duplicateDay, 1600, userCode);

        UserEntity userEntity = new UserEntity(userCode, UserTypeEnum.MEMBER, "test", "test@test.com", "test", "test", "test", "test", true, "M", 123.1, 123.1, 11, null, null, LocalDateTime.now(), LocalDateTime.now(), null);

        InbodyDTO inbodyDTO = new InbodyDTO(null, request.getInbodyScore(), request.getWeight(), request.getHeight(), request.getMuscleWeight(), request.getFatWeight(), request.getBmi(), request.getFatPercentage(), request.getDayOfInbody(), request.getBasalMetabolicRate(), LocalDateTime.now(), LocalDateTime.now(), userEntity, request.getUserCode());

        // 중복된 날짜로 저장된 기존 Inbody 객체 설정
        Inbody existingInbody = Inbody.builder()
                .inbodyCode(1L)  // InbodyCode 수동 설정
                .inbodyScore(85)
                .weight(70.5)
                .height(175.0)
                .muscleWeight(30.0)
                .fatWeight(15.0)
                .bmi(22.9)
                .fatPercentage(18.0)
                .dayOfInbody(duplicateDay)
                .basalMetabolicRate(1600)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(userEntity)
                .build();

        // UserService 모킹
        when(userService.findById(userCode)).thenReturn(new UserDTO(
                userEntity.getUserCode(),
                String.valueOf(userEntity.getUserType()),
                userEntity.getUserName(),
                userEntity.getUserEmail(),
                userEntity.getUserPassword(),
                userEntity.getUserPhone(),
                userEntity.getUserNickname(),
                userEntity.getUserAddress(),
                true,
                userEntity.getUserGender(),
                userEntity.getUserHeight(),
                userEntity.getUserWeight(),
                userEntity.getUserAge(),
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        // 중복된 인바디 항목을 반환하도록 설정
        when(inbodyRepository.findByDayOfInbodyAndUser(any(LocalDateTime.class), any(UserEntity.class))).thenReturn(Optional.of(existingInbody));

        // ModelMapper 모킹
        when(modelMapper.map(any(RequestRegisterInbodyVO.class), eq(InbodyDTO.class))).thenReturn(inbodyDTO);

        // When & Then
        assertThrows(CommonException.class, () -> inbodyService.registerInbody(inbodyDTO));

        // 중복 인바디로 인해 save()가 호출되지 않도록 확인
        verify(inbodyRepository, never()).save(any(Inbody.class));
    }

    @DisplayName("인바디 수정 성공")
    @Test
    void testEditInbody_Success() {
        // Given
        Long inbodyCode = 1L;

        RequestEditInbodyVO request = new RequestEditInbodyVO(
                90,
                72.0,
                176.0,
                32.0,
                16.0,
                23.5,
                19.0,
                LocalDateTime.now(),
                1700,
                UserEntity.builder()
                        .userCode("test")
                        .userType(UserTypeEnum.MEMBER)
                        .userName("testuser")
                        .userEmail("testuser@example.com")
                        .userPassword("testpassword")
                        .userPhone("010-1234-5678")
                        .userNickname("tester")
                        .userAddress("testAddress")
                        .userFlag(true)
                        .userGender("Male")
                        .userHeight(176.0)
                        .userWeight(72.0)
                        .userAge(25)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .gym(null)
                        .build()
        );

        Inbody existingInbody = Inbody.builder()
                .inbodyCode(inbodyCode)
                .inbodyScore(85)
                .weight(70.0)
                .height(175.0)
                .muscleWeight(30.0)
                .fatWeight(15.0)
                .bmi(22.9)
                .fatPercentage(18.0)
                .dayOfInbody(LocalDateTime.now().minusDays(1))
                .basalMetabolicRate(1600)
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(5))
                .user(request.getUser())
                .build();

        Inbody updatedInbody = Inbody.builder()
                .inbodyCode(inbodyCode)
                .inbodyScore(request.getInbodyScore())
                .weight(request.getWeight())
                .height(request.getHeight())
                .muscleWeight(request.getMuscleWeight())
                .fatWeight(request.getFatWeight())
                .bmi(request.getBmi())
                .fatPercentage(request.getFatPercentage())
                .dayOfInbody(request.getDayOfInbody())
                .basalMetabolicRate(request.getBasalMetabolicRate())
                .createdAt(existingInbody.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .user(request.getUser())
                .build();

        when(inbodyRepository.findById(inbodyCode)).thenReturn(Optional.of(existingInbody));
        when(inbodyRepository.save(existingInbody)).thenReturn(updatedInbody);
        when(modelMapper.map(updatedInbody, InbodyDTO.class)).thenReturn(new InbodyDTO(
                inbodyCode,
                updatedInbody.getInbodyScore(),
                updatedInbody.getWeight(),
                updatedInbody.getHeight(),
                updatedInbody.getMuscleWeight(),
                updatedInbody.getFatWeight(),
                updatedInbody.getBmi(),
                updatedInbody.getFatPercentage(),
                updatedInbody.getDayOfInbody(),
                updatedInbody.getBasalMetabolicRate(),
                updatedInbody.getCreatedAt(),
                updatedInbody.getUpdatedAt(),
                updatedInbody.getUser(),
                updatedInbody.getUser().getUserCode()
        ));

        // When
        InbodyDTO result = inbodyService.editInbody(inbodyCode, request);

        // Then
        assertNotNull(result);
        assertEquals(90, result.getInbodyScore());
        assertEquals(72.0, result.getWeight());
        assertEquals(176.0, result.getHeight());
        assertEquals(1700, result.getBasalMetabolicRate());
        verify(inbodyRepository, times(1)).save(existingInbody);
    }


    @DisplayName("인바디 수정 실패 - 존재하지 않는 인바디")
    @Test
    void testEditInbody_Failure_InbodyNotFound() {
        // Given
        Long inbodyCode = 1L;

        RequestEditInbodyVO request = new RequestEditInbodyVO(
                90,
                72.0,
                176.0,
                32.0,
                16.0,
                23.5,
                19.0,
                LocalDateTime.now(),
                1700,
                UserEntity.builder()
                        .userCode("test")
                        .userType(UserTypeEnum.MEMBER)
                        .userName("testuser")
                        .userEmail("testuser@example.com")
                        .userPassword("testpassword")
                        .userPhone("010-1234-5678")
                        .userNickname("tester")
                        .userAddress("testAddress")
                        .userFlag(true)
                        .userGender("Male")
                        .userHeight(176.0)
                        .userWeight(72.0)
                        .userAge(25)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .gym(null)
                        .build()
        );

        when(inbodyRepository.findById(inbodyCode)).thenReturn(Optional.empty());

        // When & Then
        CommonException exception = assertThrows(CommonException.class, () -> {
            inbodyService.editInbody(inbodyCode, request);
        });

        assertEquals(StatusEnum.INBODY_NOT_FOUND, exception.getStatusEnum());
        verify(inbodyRepository, never()).save(any(Inbody.class));
    }

    @DisplayName("인바디 삭제 성공")
    @Test
    void testDeleteInbody_Success() {
        // Given
        Long inbodyCode = 1L;

        Inbody existingInbody = Inbody.builder()
                .inbodyCode(inbodyCode)
                .inbodyScore(85)
                .weight(70.0)
                .height(175.0)
                .muscleWeight(30.0)
                .fatWeight(15.0)
                .bmi(22.9)
                .fatPercentage(18.0)
                .dayOfInbody(LocalDateTime.now().minusDays(1))
                .basalMetabolicRate(1600)
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(5))
                .user(UserEntity.builder()
                        .userCode("test")
                        .userType(UserTypeEnum.MEMBER)
                        .userName("testuser")
                        .userEmail("testuser@example.com")
                        .userPassword("testpassword")
                        .userPhone("010-1234-5678")
                        .userNickname("tester")
                        .userAddress("testAddress")
                        .userFlag(true)
                        .userGender("Male")
                        .userHeight(175.0)
                        .userWeight(70.0)
                        .userAge(25)
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .updatedAt(LocalDateTime.now().minusDays(5))
                        .gym(null)
                        .build())
                .build();

        when(inbodyRepository.findById(inbodyCode)).thenReturn(Optional.of(existingInbody));

        // When
        inbodyService.deleteInbody(inbodyCode);

        // Then
        verify(inbodyRepository, times(1)).delete(existingInbody);
    }

    @DisplayName("관리자 - 인바디 단 건 조회 성공")
    @Test
    void testFindInbodyByCode_Success() {
        // Given
        Long inbodyCode = 1L;

        Inbody existingInbody = Inbody.builder()
                .inbodyCode(inbodyCode)
                .inbodyScore(85)
                .weight(70.0)
                .height(175.0)
                .muscleWeight(30.0)
                .fatWeight(15.0)
                .bmi(22.9)
                .fatPercentage(18.0)
                .dayOfInbody(LocalDateTime.now().minusDays(1))
                .basalMetabolicRate(1600)
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(5))
                .user(UserEntity.builder()
                        .userCode("test")
                        .userType(UserTypeEnum.MEMBER)
                        .userName("testuser")
                        .userEmail("testuser@example.com")
                        .userPassword("testpassword")
                        .userPhone("010-1234-5678")
                        .userNickname("tester")
                        .userAddress("testAddress")
                        .userFlag(true)
                        .userGender("Male")
                        .userHeight(175.0)
                        .userWeight(70.0)
                        .userAge(25)
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .updatedAt(LocalDateTime.now().minusDays(5))
                        .gym(null)
                        .build())
                .build();

        when(inbodyRepository.findById(inbodyCode)).thenReturn(Optional.of(existingInbody));

        InbodyDTO expectedDTO = new InbodyDTO(
                inbodyCode,
                85,
                70.0,
                175.0,
                30.0,
                15.0,
                22.9,
                18.0,
                LocalDateTime.now().minusDays(1),
                1600,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5),
                existingInbody.getUser(),
                existingInbody.getUser().getUserCode()
        );

        when(modelMapper.map(existingInbody, InbodyDTO.class)).thenReturn(expectedDTO);

        // When
        InbodyDTO result = inbodyService.findInbodyByCode(inbodyCode);

        // Then
        assertNotNull(result, "The result should not be null.");
        assertEquals(85, result.getInbodyScore());
        assertEquals(70.0, result.getWeight());
        assertEquals(175.0, result.getHeight());
        verify(inbodyRepository, times(1)).findById(inbodyCode);

        System.out.println("expectedDTO: " + expectedDTO);
        System.out.println("result: " + result);
    }

    @DisplayName("관리자 - 인바디 전체 조회 성공")
    @Test
    void testFindAllInbody_Success() {
        // Given
        List<Inbody> inbodyList = Arrays.asList(
                Inbody.builder()
                        .inbodyCode(1L)
                        .inbodyScore(85)
                        .weight(70.0)
                        .height(175.0)
                        .muscleWeight(30.0)
                        .fatWeight(15.0)
                        .bmi(22.9)
                        .fatPercentage(18.0)
                        .dayOfInbody(LocalDateTime.now().minusDays(1))
                        .basalMetabolicRate(1600)
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .updatedAt(LocalDateTime.now().minusDays(5))
                        .user(UserEntity.builder()
                                .userCode("test1")
                                .userType(UserTypeEnum.MEMBER)
                                .userName("testuser1")
                                .userEmail("testuser1@example.com")
                                .userPassword("testpassword1")
                                .userPhone("010-1234-5678")
                                .userNickname("tester1")
                                .userAddress("testAddress1")
                                .userFlag(true)
                                .userGender("Male")
                                .userHeight(175.0)
                                .userWeight(70.0)
                                .userAge(25)
                                .createdAt(LocalDateTime.now().minusDays(10))
                                .updatedAt(LocalDateTime.now().minusDays(5))
                                .gym(null)
                                .build())
                        .build(),
                Inbody.builder()
                        .inbodyCode(2L)
                        .inbodyScore(90)
                        .weight(80.0)
                        .height(180.0)
                        .muscleWeight(35.0)
                        .fatWeight(20.0)
                        .bmi(24.7)
                        .fatPercentage(20.0)
                        .dayOfInbody(LocalDateTime.now().minusDays(2))
                        .basalMetabolicRate(1800)
                        .createdAt(LocalDateTime.now().minusDays(6))
                        .updatedAt(LocalDateTime.now().minusDays(6))
                        .user(UserEntity.builder()
                                .userCode("test2")
                                .userType(UserTypeEnum.MEMBER)
                                .userName("testuser2")
                                .userEmail("testuser2@example.com")
                                .userPassword("testpassword2")
                                .userPhone("010-8765-4321")
                                .userNickname("tester2")
                                .userAddress("testAddress2")
                                .userFlag(true)
                                .userGender("Female")
                                .userHeight(180.0)
                                .userWeight(80.0)
                                .userAge(30)
                                .createdAt(LocalDateTime.now().minusDays(10))
                                .updatedAt(LocalDateTime.now().minusDays(5))
                                .gym(null)
                                .build())
                        .build()
        );

        when(inbodyRepository.findAll()).thenReturn(inbodyList);

        when(modelMapper.map(any(Inbody.class), eq(InbodyDTO.class))).thenAnswer(invocation -> {
            Inbody inbody = invocation.getArgument(0);
            return new InbodyDTO(
                    inbody.getInbodyCode(),
                    inbody.getInbodyScore(),
                    inbody.getWeight(),
                    inbody.getHeight(),
                    inbody.getMuscleWeight(),
                    inbody.getFatWeight(),
                    inbody.getBmi(),
                    inbody.getFatPercentage(),
                    inbody.getDayOfInbody(),
                    inbody.getBasalMetabolicRate(),
                    inbody.getCreatedAt(),
                    inbody.getUpdatedAt(),
                    inbody.getUser(),
                    inbody.getUser().getUserCode()
            );
        });

        // When
        List<InbodyDTO> result = inbodyService.findAllInbody();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(85, result.get(0).getInbodyScore());
        assertEquals(90, result.get(1).getInbodyScore());
        verify(inbodyRepository, times(1)).findAll();
    }

    @DisplayName("유저의 단 건 인바디 조회 성공")
    @Test
    void testFindUserInbodyByCode_Success() {
        // Given
        Long inbodyCode = 1L;
        String userCode = "testUser";

        Inbody existingInbody = Inbody.builder()
                .inbodyCode(inbodyCode)
                .inbodyScore(85)
                .weight(70.0)
                .height(175.0)
                .muscleWeight(30.0)
                .fatWeight(15.0)
                .bmi(22.9)
                .fatPercentage(18.0)
                .dayOfInbody(LocalDateTime.now().minusDays(1))
                .basalMetabolicRate(1600)
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(5))
                .user(UserEntity.builder()
                        .userCode(userCode)
                        .userType(UserTypeEnum.MEMBER)
                        .userName("testuser")
                        .userEmail("testuser@example.com")
                        .userPassword("testpassword")
                        .userPhone("010-1234-5678")
                        .userNickname("tester")
                        .userAddress("testAddress")
                        .userFlag(true)
                        .userGender("Male")
                        .userHeight(175.0)
                        .userWeight(70.0)
                        .userAge(25)
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .updatedAt(LocalDateTime.now().minusDays(5))
                        .gym(null)
                        .build())
                .build();

        when(inbodyRepository.findByInbodyCodeAndUser_UserCode(inbodyCode, userCode)).thenReturn(Optional.of(existingInbody));

        InbodyDTO expectedDTO = new InbodyDTO(
                existingInbody.getInbodyCode(),
                existingInbody.getInbodyScore(),
                existingInbody.getWeight(),
                existingInbody.getHeight(),
                existingInbody.getMuscleWeight(),
                existingInbody.getFatWeight(),
                existingInbody.getBmi(),
                existingInbody.getFatPercentage(),
                existingInbody.getDayOfInbody(),
                existingInbody.getBasalMetabolicRate(),
                existingInbody.getCreatedAt(),
                existingInbody.getUpdatedAt(),
                existingInbody.getUser(),
                existingInbody.getUser().getUserCode()
        );
        when(modelMapper.map(existingInbody, InbodyDTO.class)).thenReturn(expectedDTO);

        // When
        InbodyDTO result = inbodyService.findInbodyByCodeAndUser(inbodyCode, userCode);

        // Then
        assertNotNull(result);
        assertEquals(85, result.getInbodyScore());
        assertEquals(70.0, result.getWeight());
        assertEquals(175.0, result.getHeight());
        assertEquals(userCode, result.getUser().getUserCode());
        verify(inbodyRepository, times(1)).findByInbodyCodeAndUser_UserCode(inbodyCode, userCode);
    }

    @DisplayName("유저의 전체 인바디 조회 성공")
    @Test
    void testFindAllUserInbody_Success() {
        // Given
        String userCode = "testUser";

        List<Inbody> inbodyList = Arrays.asList(
                Inbody.builder()
                        .inbodyCode(1L)
                        .inbodyScore(85)
                        .weight(70.0)
                        .height(175.0)
                        .muscleWeight(30.0)
                        .fatWeight(15.0)
                        .bmi(22.9)
                        .fatPercentage(18.0)
                        .dayOfInbody(LocalDateTime.now().minusDays(1))
                        .basalMetabolicRate(1600)
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .updatedAt(LocalDateTime.now().minusDays(5))
                        .user(UserEntity.builder()
                                .userCode(userCode)
                                .userType(UserTypeEnum.MEMBER)
                                .userName("testuser")
                                .userEmail("testuser@example.com")
                                .userPassword("testpassword")
                                .userPhone("010-1234-5678")
                                .userNickname("tester")
                                .userAddress("testAddress")
                                .userFlag(true)
                                .userGender("Male")
                                .userHeight(175.0)
                                .userWeight(70.0)
                                .userAge(25)
                                .createdAt(LocalDateTime.now().minusDays(10))
                                .updatedAt(LocalDateTime.now().minusDays(5))
                                .gym(null)
                                .build())
                        .build(),
                Inbody.builder()
                        .inbodyCode(2L)
                        .inbodyScore(90)
                        .weight(80.0)
                        .height(180.0)
                        .muscleWeight(35.0)
                        .fatWeight(20.0)
                        .bmi(24.7)
                        .fatPercentage(20.0)
                        .dayOfInbody(LocalDateTime.now().minusDays(2))
                        .basalMetabolicRate(1800)
                        .createdAt(LocalDateTime.now().minusDays(6))
                        .updatedAt(LocalDateTime.now().minusDays(6))
                        .user(UserEntity.builder()
                                .userCode(userCode)
                                .userType(UserTypeEnum.MEMBER)
                                .userName("testuser")
                                .userEmail("testuser@example.com")
                                .userPassword("testpassword")
                                .userPhone("010-1234-5678")
                                .userNickname("tester")
                                .userAddress("testAddress")
                                .userFlag(true)
                                .userGender("Male")
                                .userHeight(180.0)
                                .userWeight(80.0)
                                .userAge(30)
                                .createdAt(LocalDateTime.now().minusDays(10))
                                .updatedAt(LocalDateTime.now().minusDays(5))
                                .gym(null)
                                .build())
                        .build()
        );

        when(inbodyRepository.findAllByUser_UserCode(userCode)).thenReturn(inbodyList);

        List<InbodyDTO> expectedDTOList = inbodyList.stream()
                .map(inbody -> modelMapper.map(inbody, InbodyDTO.class))
                .collect(Collectors.toList());

        when(modelMapper.map(any(Inbody.class), eq(InbodyDTO.class))).thenAnswer(invocation -> {
            Inbody inbody = invocation.getArgument(0);
            return new InbodyDTO(
                    inbody.getInbodyCode(),
                    inbody.getInbodyScore(),
                    inbody.getWeight(),
                    inbody.getHeight(),
                    inbody.getMuscleWeight(),
                    inbody.getFatWeight(),
                    inbody.getBmi(),
                    inbody.getFatPercentage(),
                    inbody.getDayOfInbody(),
                    inbody.getBasalMetabolicRate(),
                    inbody.getCreatedAt(),
                    inbody.getUpdatedAt(),
                    inbody.getUser(),
                    inbody.getUser().getUserCode()
            );
        });

        // When
        List<InbodyDTO> result = inbodyService.findAllInbodyByUser(userCode);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(inbodyRepository, times(1)).findAllByUser_UserCode(userCode);
    }
}