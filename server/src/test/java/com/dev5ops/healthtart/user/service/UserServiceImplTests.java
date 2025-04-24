package com.dev5ops.healthtart.user.service;

import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.user.domain.UserTypeEnum;
import com.dev5ops.healthtart.user.domain.dto.UserDTO;
import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import com.dev5ops.healthtart.user.domain.vo.request.RequestInsertUserVO;
import com.dev5ops.healthtart.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTests {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유저 단일 조회 테스트")
    void testRegisterUser(){
        // Given
        String userCode = "20241007-00d92a1c-3a7c-49b7-9c34-7b6cfab7b40c";
        String dateTimeString = "2024-10-19 19:11:04";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);

        UserEntity userEntity = UserEntity.builder()
                .userCode(userCode)
                .userType(UserTypeEnum.MEMBER)
                .userName("김12")
                .userEmail("test42@test.com")
                .userPassword("$2b$12$SB5LJyzV9wwHGP5P5kGfYuivcvhLyzyVJDJu9QNZsphtJdgy/8Zta")
                .userPhone("010-1234-5712")
                .userNickname("nickname42")
                .userAddress("Address 42")
                .userFlag(true)
                .userGender("F")
                .userHeight(180.0)
                .userWeight(79.0)
                .userAge(33)
                .createdAt(localDateTime)
                .updatedAt(localDateTime)
                .build();

        UserDTO userDTO = UserDTO.builder()
                .userCode(userCode)
                .userType("MEMBER")
                .userName("김12")
                .userEmail("test42@test.com")
                .userPassword("$2b$12$SB5LJyzV9wwHGP5P5kGfYuivcvhLyzyVJDJu9QNZsphtJdgy/8Zta")
                .userPhone("010-1234-5712")
                .userNickname("nickname42")
                .userAddress("Address 42")
                .userFlag(true)
                .userGender("F")
                .userHeight(180.0)
                .userWeight(79.0)
                .userAge(33)
                .createdAt(localDateTime)
                .updatedAt(localDateTime)
                .build();

        when(modelMapper.map(userEntity, UserDTO.class)).thenReturn(userDTO);
        when(userRepository.findById(userCode)).thenReturn(Optional.of(userEntity));

        // When
        UserDTO responseDTO = userService.findById(userCode);

        // Then
        assertNotNull(responseDTO);
        assertEquals(userCode, responseDTO.getUserCode());
        assertEquals("김12", responseDTO.getUserName());
        assertEquals("test42@test.com", responseDTO.getUserEmail());
        assertEquals("nickname42", responseDTO.getUserNickname());

        verify(userRepository, times(1)).findById(userCode);
        verify(modelMapper, times(1)).map(userEntity, UserDTO.class);
    }

    @Test
    @DisplayName("전체 유저 조회 테스트")
    void testFindAllUsers() {
        // Given
        String userCode1 = "20241007-00d92a1c-3a7c-49b7-9c34-7b6cfab7b40c";
        String userCode2 = "20241008-00d92a1c-3a7c-49b7-9c34-7b6cfab7b40d";
        String dateTimeString = "2024-10-19 19:11:04";
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        UserEntity userEntity1 = UserEntity.builder()
                .userCode(userCode1)
                .userType(UserTypeEnum.MEMBER)
                .userName("김철수")
                .userEmail("kim@test.com")
                .userPassword("$2b$12$SB5LJyzV9wwHGP5P5kGfYuivcvhLyzyVJDJu9QNZsphtJdgy/8Zta")
                .userPhone("010-1234-5678")
                .userNickname("철수")
                .userAddress("서울")
                .userFlag(true)
                .userGender("M")
                .userHeight(180.0)
                .userWeight(75.0)
                .userAge(30)
                .createdAt(localDateTime)
                .updatedAt(localDateTime)
                .build();

        UserEntity userEntity2 = UserEntity.builder()
                .userCode(userCode2)
                .userType(UserTypeEnum.MEMBER)
                .userName("이영희")
                .userEmail("lee@test.com")
                .userPassword("$2b$12$SB5LJyzV9wwHGP5P5kGfYuivcvhLyzyVJDJu9QNZsphtJdgy/8Zta")
                .userPhone("010-8765-4321")
                .userNickname("영희")
                .userAddress("부산")
                .userFlag(true)
                .userGender("F")
                .userHeight(165.0)
                .userWeight(55.0)
                .userAge(28)
                .createdAt(localDateTime)
                .updatedAt(localDateTime)
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(userEntity1, userEntity2));

        when(modelMapper.map(userEntity1, UserDTO.class)).thenReturn(new UserDTO(
                userCode1, "MEMBER","김철수", "kim@test.com", "$2b$12$SB5LJyzV9wwHGP5P5kGfYuivcvhLyzyVJDJu9QNZsphtJdgy/8Zta",
                "010-1234-5678", "철수", "서울", true, "M", 180.0, 75.0, 30,  localDateTime, localDateTime));

        when(modelMapper.map(userEntity2, UserDTO.class)).thenReturn(new UserDTO(
                userCode2, "MEMBER", "이영희", "lee@test.com", "$2b$12$SB5LJyzV9wwHGP5P5kGfYuivcvhLyzyVJDJu9QNZsphtJdgy/8Zta",
                "010-8765-4321", "영희", "부산", true, "F", 165.0, 55.0, 28, localDateTime, localDateTime));

        // When
        List<UserDTO> users = userService.findAllUsers();

        // Then
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("김철수", users.get(0).getUserName());
        assertEquals("이영희", users.get(1).getUserName());

        verify(userRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(userEntity1, UserDTO.class);
        verify(modelMapper, times(1)).map(userEntity2, UserDTO.class);
    }

    @Test
    @DisplayName("회원가입 테스트")
    void testSignUpUser() {
        // Given
        RequestInsertUserVO request = new RequestInsertUserVO(
                "홍길동",
                "MEMBER",
                "test@example.com",
                "encodedpassword",
                "010-1234-5678",
                "홍길동닉네임",
                "서울",
                "M",
                180.0,
                75.0,
                30,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(request.getUserEmail())).thenReturn("True");

        when(userRepository.findByUserEmail(request.getUserEmail())).thenReturn(null);

        when(userRepository.findByUserNickname(request.getUserNickname())).thenReturn(null);

        when(bCryptPasswordEncoder.encode(request.getUserPassword())).thenReturn("encodedpassword");

        UserEntity userEntity = UserEntity.builder()
                .userType(UserTypeEnum.valueOf(request.getUserType()))
                .userName(request.getUserName())
                .userEmail(request.getUserEmail())
                .userPassword("encodedpassword")
                .userPhone(request.getUserPhone())
                .userNickname(request.getUserNickname())
                .userAddress(request.getUserAddress())
                .userFlag(true)
                .userGender("M")
                .userHeight(request.getUserHeight())
                .userWeight(request.getUserWeight())
                .userAge(request.getUserAge())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


        when(userRepository.save(userEntity)).thenReturn(userEntity);

        // When
        userService.signUpUser(request);

        // Then
        verify(stringRedisTemplate.opsForValue()).get(request.getUserEmail());
        verify(userRepository).findByUserEmail(request.getUserEmail());
        verify(userRepository).findByUserNickname(request.getUserNickname());
        verify(bCryptPasswordEncoder).encode(request.getUserPassword());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void testDeleteUser() {
        // Given
        String userCode = "test-user-code";

        UserEntity userEntity = UserEntity.builder()
                .userCode(userCode)
                .userName("홍길동")
                .userEmail("test@example.com")
                .userPassword("encodedpassword")
                .userPhone("010-1234-5678")
                .userNickname("홍길동닉네임")
                .userAddress("서울")
                .userFlag(true) // 삭제되지 않은 사용자
                .userGender("M")
                .userHeight(180.0)
                .userWeight(75.0)
                .userAge(30)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userCode)).thenReturn(Optional.of(userEntity));

        // When
        userService.deleteUser(userCode);

        // Then
        assertFalse(userEntity.getUserFlag());
        assertNotNull(userEntity.getUpdatedAt());

        verify(userRepository, times(1)).findById(userCode);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("회원 삭제 - 사용자 미존재 시 예외 발생 테스트")
    void testDeleteUser_userNotFound() {
        // Given
        String userCode = "non-existent-user-code";

        when(userRepository.findById(userCode)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CommonException.class, () -> {
            userService.deleteUser(userCode);
        });

        verify(userRepository, times(1)).findById(userCode);
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}