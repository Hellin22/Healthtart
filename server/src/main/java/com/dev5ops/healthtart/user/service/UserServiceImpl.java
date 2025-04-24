package com.dev5ops.healthtart.user.service;

import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.CoolSmsException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.gym.domain.entity.Gym;
import com.dev5ops.healthtart.gym.repository.GymRepository;
import com.dev5ops.healthtart.security.JwtUtil;
import com.dev5ops.healthtart.user.domain.CustomUserDetails;
import com.dev5ops.healthtart.user.domain.UserTypeEnum;
import com.dev5ops.healthtart.user.domain.dto.*;
import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import com.dev5ops.healthtart.user.domain.vo.request.RegisterGymPerUserRequest;
import com.dev5ops.healthtart.user.domain.vo.request.RequestInsertUserVO;
import com.dev5ops.healthtart.user.domain.vo.request.RequestOauth2VO;
import com.dev5ops.healthtart.user.domain.vo.request.RequestResetPasswordVO;
import com.dev5ops.healthtart.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    private GymRepository gymRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;  // StringRedisTemplate 사용
    private final RestTemplate restTemplate;
    private final CoolSmsService coolSmsService;

    @Autowired
    public UserServiceImpl(GymRepository gymRepository, BCryptPasswordEncoder bCryptPasswordEncoder, ModelMapper modelMapper, UserRepository userRepository, JwtUtil jwtUtil, StringRedisTemplate stringRedisTemplate, RestTemplate restTemplate, CoolSmsService coolSmsService) {
        this.gymRepository = gymRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
        this.restTemplate = restTemplate;
        this.coolSmsService = coolSmsService;
    }

    // 회원가입
    @Override
    public void signUpUser(RequestInsertUserVO request) {

        // Redis에서 이메일 인증 여부 확인
        String emailVerificationStatus = stringRedisTemplate.opsForValue().get(request.getUserEmail());

        if (!"True".equals(emailVerificationStatus)) {
            log.error("이메일 인증이 완료되지 않았습니다: {}", request.getUserEmail());
            throw new CommonException(StatusEnum.EMAIL_VERIFICATION_REQUIRED); // 이메일 인증이 필요하다는 커스텀 예외 던지기
        }
        if (userRepository.findByUserEmail(request.getUserEmail()) != null) throw new CommonException(StatusEnum.EMAIL_DUPLICATE);
        if(!isValidAndUniqueNickname(request.getUserNickname())) throw new CommonException(StatusEnum.INVALID_NICKNAME_LENGTH);

        String curDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString();

        String userCode = curDate + "-" + uuid;

        request.changePwd(bCryptPasswordEncoder.encode(request.getUserPassword()));

        // modelMapper 대신 빌더 패턴을 사용하여 UserEntity 생성
        UserEntity insertUser = UserEntity.builder()
                .userCode(null) // UserCode는 생성 후 나중에 설정하거나 생성할 수 있습니다.
                .userType(UserTypeEnum.valueOf(request.getUserType())) // UserTypeEnum으로 변환
                .userName(request.getUserName()) // 이름 설정
                .userEmail(request.getUserEmail()) // 이메일 설정
                .userPassword(request.getUserPassword()) // 암호화된 비밀번호 설정
                .userPhone(request.getUserPhone()) // 전화번호 설정
                .userNickname(request.getUserNickname()) // 닉네임 설정
                .userAddress(request.getUserAddress()) // 주소 설정
                .userFlag(true) // 회원 활성 상태 설정
                .userGender(request.getUserGender()) // 성별 설정
                .userHeight(request.getUserHeight()) // 키 설정
                .userWeight(request.getUserWeight()) // 몸무게 설정
                .userAge(request.getUserAge()) // 나이 설정
                .createdAt(LocalDateTime.now()) // 생성일자 설정
                .updatedAt(LocalDateTime.now()) // 수정일자 설정
                .build();

        userRepository.save(insertUser);
    }

    // 회원 전체 조회
    @Override
    public List<UserDTO> findAllUsers() {
        List<UserEntity> allUsers = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();

        for(UserEntity user : allUsers) {
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            userDTOList.add(userDTO);
        }
        return userDTOList;
    }

    // 이메일로 회원 조회
    @Override
    public UserDTO findUserByEmail(String userEmail) {
        UserEntity findUser = userRepository.findByUserEmail(userEmail);

        if(findUser == null)
            throw new CommonException(StatusEnum.USER_NOT_FOUND);

        return modelMapper.map(findUser, UserDTO.class);
    }

    // 회원 가입 전 중복 이메일 체크 메서드
    @Override
    public void findUserByEmail2(String userEmail){
        UserEntity findUser = userRepository.findByUserEmail(userEmail);

        if(findUser != null)
            throw new CommonException(StatusEnum.USER_NOT_FOUND);
    }

    @Override
    public void editUserInfo(UserDTO userDTO) {
        String userCode = getUserCode();
        UserEntity user = userRepository.findById(userCode).orElseThrow(() -> new CommonException(StatusEnum.USER_NOT_FOUND));

        user.setUserHeight(userDTO.getUserHeight());
        user.setUserWeight(userDTO.getUserWeight());

        userRepository.save(user);
    }

    @Override
    public UserDTO findById(String userCode) {
        UserEntity user = userRepository.findById(userCode)
                .orElseThrow(() -> new CommonException(StatusEnum.USER_NOT_FOUND));

        return modelMapper.map(user, UserDTO.class);
    }


    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUserEmail(userEmail);

        if (user == null) throw new CommonException(StatusEnum.USER_NOT_FOUND);

        // 사용자의 권한 설정
        // ADMIN인 경우 MEMBER 속성도 가짐.
        List<GrantedAuthority> roles = new ArrayList<>();
        if("MEMBER".equals(user.getUserType().name())){
            roles.add(new SimpleGrantedAuthority("MEMBER"));
        }else{
            roles.add(new SimpleGrantedAuthority("MEMBER"));
            roles.add(new SimpleGrantedAuthority("ADMIN"));
        }

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        if(userDTO.getUserFlag() == false) throw new CommonException(StatusEnum.USER_NOT_FOUND);

        return new CustomUserDetails(
                userDTO,
                roles,
                true,
                true,
                true,
                user.getUserFlag());
    }

    @Override
    public void deleteUser(String userCode) {
        UserEntity user = userRepository.findById(userCode)
                .orElseThrow(() -> new CommonException(StatusEnum.USER_NOT_FOUND));

        user.removeRequest(user);
        userRepository.save(user);
    }

    // userNickname 유효성 검사하는 서비스코드 -> 회원가입, 마이페이지에서 사용
    @Override
    public Boolean checkValideNickname(String userNickname) {
        return isValidAndUniqueNickname(userNickname);
    }

    @Override
    public void saveOauth2User(RequestOauth2VO request) {

        String userCode = getUserCode();
        UserEntity findUser = userRepository.findById(userCode)
                .orElseThrow(() -> new CommonException(StatusEnum.USER_NOT_FOUND));

        findUser.setUserPhone(request.getUserPhone());
        findUser.setUserNickname(request.getUserNickname());
        findUser.setUserAddress(request.getUserAddress());
        findUser.setUserGender(request.getUserGender());
        findUser.setUserHeight(request.getUserHeight());
        findUser.setUserWeight(request.getUserWeight());
        findUser.setUserAge(request.getUserAge());
        findUser.setUpdatedAt(LocalDateTime.now());

        userRepository.save(findUser);
    }

    @Override
    public ResponseMypageDTO getMypageInfo() {

        String userCode = getUserCode();

        ResponseMypageDTO responseMypageDTO = userRepository.findMypageInfo(userCode);
        if(responseMypageDTO == null) throw new CommonException(StatusEnum.USER_NOT_FOUND);

        return responseMypageDTO;
    }

    @Override
    public void editPassword(EditPasswordDTO editPasswordDTO) {
        String userCode = getUserCode();
        UserEntity user = userRepository.findById(userCode).orElseThrow(() -> new CommonException(StatusEnum.USER_NOT_FOUND));

        if (!bCryptPasswordEncoder.matches(editPasswordDTO.getCurrentPassword(), user.getUserPassword())) {
            throw new CommonException(StatusEnum.INVALID_PASSWORD);
        }

        user.setUserPassword(bCryptPasswordEncoder.encode(editPasswordDTO.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public void updateUserGym(RegisterGymPerUserRequest registerGymRequest) {
        String userCode = registerGymRequest.getUserCode();
        String businessNumber = registerGymRequest.getBusinessNumber().trim();

        log.info(businessNumber);

        UserEntity user = userRepository.findById(userCode).orElseThrow(() -> new CommonException(StatusEnum.USER_NOT_FOUND));
        Gym gym = gymRepository.findByBusinessNumber(businessNumber).orElseThrow(() -> new CommonException(StatusEnum.GYM_NOT_FOUND));

        log.info(businessNumber);

        user.setGym(gym);
        userRepository.save(user);
    }

    @Override
    public void deleteUserGym(RegisterGymPerUserRequest registerGymRequest) {
        String userCode = registerGymRequest.getUserCode();

        UserEntity user = userRepository.findById(userCode).orElseThrow(() -> new CommonException(StatusEnum.USER_NOT_FOUND));

        user.setGym(null);
        userRepository.save(user);
    }

    @Override
    public void resetPassword(RequestResetPasswordVO request) {

        UserEntity findUser = userRepository.findByUserEmail(request.getUserEmail());
        if(findUser == null) throw new CommonException(StatusEnum.USER_NOT_FOUND);

        // 비밀번호 bCrypt 암호화.
        findUser.setUserPassword(bCryptPasswordEncoder.encode(request.getUserPassword()));

        userRepository.save(findUser);
    }

    public String getUserCode() {
        // 현재 인증된 사용자 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 인증된 사용자가 문자열(String)인 경우 (로그인하지 않은 상태)
        if (principal instanceof String) throw new CommonException(StatusEnum.USER_NOT_FOUND);

        // 인증된 사용자가 CustomUserDetails인 경우
        CustomUserDetails userDetails = (CustomUserDetails) principal;

        log.info("Authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        log.info("userDetails: {}", userDetails.toString());

        // 현재 로그인한 유저의 유저코드 반환
        return userDetails.getUserDTO().getUserCode();
    }

    public boolean isValidAndUniqueNickname(String nickname) {
        int maxAllowedLength = 15;  // 한글 7자, 영어 15자 기준 (혼합 시 한글은 2배로 계산)
        int currentLength = 0;

        // 특수기호 확인을 위한 정규표현식
        String specialCharacters = "[!@#$%^&*()_+=|<>?{}\\[\\]~-]";

        // 특수기호가 포함되어 있는지 확인
        if (nickname.matches(".*" + specialCharacters + ".*")) {
            return false;  // 특수기호가 포함되어 있으면 false 반환
        }

        // 닉네임의 길이 체크 (한글은 2글자, 영어는 1글자 계산)
        for (int i = 0; i < nickname.length(); i++) {
            char ch = nickname.charAt(i);

            if (ch >= 0xAC00 && ch <= 0xD7A3) {
                currentLength += 2;  // 한글은 2로 계산
            } else {
                currentLength += 1;  // 영어 및 기타 문자는 1로 계산
            }

            // 길이가 초과되면 false 반환
            if (currentLength > maxAllowedLength) {
                return false;
            }
        }

        // 중복 확인
        UserEntity user = userRepository.findByUserNickname(nickname);
        if (user != null) {
            return false;  // 중복된 닉네임이 있으면 false 반환
        }

        return true;  // 길이와 특수문자, 중복 체크를 모두 통과한 경우 true 반환
    }

    // userPhone으로 userEmail 조회
    public String getUserEmailByUserPhone(String userPhone) {
        String userEmail = userRepository.findUserEmailByUserPhone(userPhone);
        if (userEmail == null) {
            throw new IllegalArgumentException("해당 핸드폰 번호로 등록된 이메일이 없습니다.");
        }
        return userEmail;
    }

    // 인증 코드 저장을 위한 메모리 캐시
    private final Map<String, String> phoneVerificationMap = new HashMap<>();

    // SMS 인증번호 전송
    public String sendSmsForVerification(String userPhone) {
        String verificationCode = coolSmsService.generateRandomNumber();
        String messageText = "[Healthtart] 아래 인증번호를 입력하세요\n" + verificationCode;
        coolSmsService.sendSms(userPhone, messageText);

        // 인증 번호를 캐시에 저장
        phoneVerificationMap.put(userPhone, verificationCode);

        return verificationCode;
    }

    // 인증 번호 확인 후 이메일 조회
    public String verifyCodeAndFindEmail(String userPhone, String verificationCode) {
        String storedCode = phoneVerificationMap.get(userPhone);

        if (storedCode == null || !storedCode.equals(verificationCode)) {
            throw new CoolSmsException("잘못된 인증번호입니다.");
        }

        // 핸드폰 번호로 이메일 조회
        String userEmail = userRepository.findUserEmailByUserPhone(userPhone);
        if (userEmail == null) {
            throw new CoolSmsException("해당 번호로 등록된 이메일이 없습니다.");
        }

        // 인증이 성공했으면 캐시에서 삭제
        phoneVerificationMap.remove(userPhone);

        return userEmail;
    }


}