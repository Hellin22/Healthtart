package com.dev5ops.healthtart.user.controller;

import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.CoolSmsException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.user.domain.dto.*;
import com.dev5ops.healthtart.security.JwtUtil;
import com.dev5ops.healthtart.user.domain.vo.EmailVerificationVO;
import com.dev5ops.healthtart.user.domain.vo.ResponseEmailMessageVO;
import com.dev5ops.healthtart.user.domain.vo.request.RegisterGymPerUserRequest;
import com.dev5ops.healthtart.user.domain.vo.request.RequestEditPasswordVO;
import com.dev5ops.healthtart.user.domain.vo.request.RequestInsertUserVO;
import com.dev5ops.healthtart.user.domain.vo.request.RequestOauth2VO;
import com.dev5ops.healthtart.user.domain.vo.response.ResponseEditPasswordVO;
import com.dev5ops.healthtart.user.domain.vo.request.RequestResetPasswordVO;
import com.dev5ops.healthtart.user.domain.vo.request.*;
import com.dev5ops.healthtart.user.domain.vo.response.ResponseFindUserVO;
import com.dev5ops.healthtart.user.domain.vo.response.ResponseMypageVO;
import com.dev5ops.healthtart.user.service.CoolSmsService;
import com.dev5ops.healthtart.user.service.EmailVerificationService;
import com.dev5ops.healthtart.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
@Slf4j
public class UserController {

    private final CoolSmsService coolSmsService;
    private JwtUtil jwtUtil;
    private Environment env;
    private ModelMapper modelMapper;
    private UserService userService;
    private EmailVerificationService emailVerificationService;

    @Autowired
    public UserController(JwtUtil jwtUtil, Environment env, ModelMapper modelMapper, UserService userService, EmailVerificationService emailVerificationService, CoolSmsService coolSmsService) {
        this.jwtUtil = jwtUtil;
        this.env = env;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.emailVerificationService = emailVerificationService;
        this.coolSmsService = coolSmsService;
    }

    //설명. 이메일 전송 API (회원가입전 실행)
    @Operation(summary = "회원가입 전 이메일 전송 API")
    @PostMapping("/verification-email")
    public ResponseEmailDTO<?> sendVerificationEmail(@RequestBody @Validated EmailVerificationVO request) {

        // 이메일 중복체크
        userService.findUserByEmail2(request.getEmail());

        return getResponseEmailDTO(request);
    }

    //설명. 이메일 전송 API -> 비밀번호 재설정시 사용
    @Operation(summary = "비밀번호 재설정시 이메일 전송 API")
    @PostMapping("/verification-email/password")
    public ResponseEmailDTO<?> sendVerificationEmailPassword(@RequestBody @Validated EmailVerificationVO request) {

        // 이메일 존재 확인
        UserDTO userByEmail = userService.findUserByEmail(request.getEmail());
        if(userByEmail == null)
            return ResponseEmailDTO.fail(new CommonException(StatusEnum.EMAIL_NOT_FOUND));

        // 이메일로 인증번호 전송
        return getResponseEmailDTO(request);
    }

    private ResponseEmailDTO<?> getResponseEmailDTO(EmailVerificationVO request) {
        // 이메일로 인증번호 전송
        try {
            emailVerificationService.sendVerificationEmail(request.getEmail());

            ResponseEmailMessageVO responseEmailMessageVO =new ResponseEmailMessageVO();
            responseEmailMessageVO.setMessage("인증 코드가 이메일로 전송되었습니다.");
            return ResponseEmailDTO.ok(responseEmailMessageVO);
        } catch (Exception e) {
            return ResponseEmailDTO.fail(new CommonException(StatusEnum.INTERNAL_SERVER_ERROR));
        }
    }

    //설명. 이메일 인증번호 검증 API (회원가입전 실행)
    @Operation(summary = "회원가입 전 이메일 인증번호 검증")
    @PostMapping("/verification-email/confirmation")
    public ResponseEmailDTO<?> verifyEmail(@RequestBody @Validated EmailVerificationVO request) {
        boolean isVerified = emailVerificationService.verifyCode(request.getEmail(), request.getCode());

        ResponseEmailMessageVO responseEmailMessageVO =new ResponseEmailMessageVO();
        responseEmailMessageVO.setMessage("이메일 인증이 완료되었습니다.");
        if (isVerified) {
            return ResponseEmailDTO.ok(responseEmailMessageVO);
        } else {
            return ResponseEmailDTO.fail(new CommonException(StatusEnum.INVALID_VERIFICATION_CODE));
        }
    }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<String> insertUser(@RequestBody RequestInsertUserVO request) {

        // USER_TYPE이 없는 경우 MEMBER로 설정
        if (request.getUserType() == null)
            request.setUserType("MEMBER");

        userService.signUpUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
    }

    @Operation(summary = "유저 마이페이지 조회")
    @GetMapping("/mypage")
    public ResponseEntity<ResponseMypageVO> getMypageInfo(){

        ResponseMypageDTO mypageInfo = userService.getMypageInfo();

        ResponseMypageVO response = modelMapper.map(mypageInfo, ResponseMypageVO.class);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "유저 마이페이지 비밀번호 수정")
    @PatchMapping("/mypage/edit/password")
    public ResponseEntity<ResponseEditPasswordVO> editPassword(@RequestBody RequestEditPasswordVO request) {
        EditPasswordDTO editPasswordDTO = modelMapper.map(request, EditPasswordDTO.class);

        userService.editPassword(editPasswordDTO);

        ResponseEditPasswordVO response = new ResponseEditPasswordVO("비밀번호가 성공적으로 변경되었습니다.");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "유저 키, 몸무게 수정")
    @PatchMapping("/mypage/edit/userinfo")
    public ResponseEntity<String> editUserInfo(@RequestBody RequestUserInfoVO request) {
        UserDTO userDTO = modelMapper.map(request, UserDTO.class);

        userService.editUserInfo(userDTO);

        return ResponseEntity.status(HttpStatus.OK).body("회원 정보가 성공적으로 변경되었습니다.");
    }

    // 회원 전체 조회
    @Operation(summary = "회원 전체 조회")
    @GetMapping
    public ResponseEntity<List<ResponseFindUserVO>> getAllUsers() {

        List<UserDTO> userDTOList = userService.findAllUsers();
        List<ResponseFindUserVO> userVOList = userDTOList.stream()
                .map(userDTO -> modelMapper.map(userDTO, ResponseFindUserVO.class))
                .collect(Collectors.toList());

        return new ResponseEntity<>(userVOList, HttpStatus.OK);
    }

    // 이메일로 회원 정보 조회
    @Operation(summary = "이메일로 회원 단건 조회")
    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseFindUserVO> findUserByEmail(@PathVariable String email) {
        UserDTO userDTO = userService.findUserByEmail(email);
        ResponseFindUserVO responseFindUserVO = modelMapper.map(userDTO, ResponseFindUserVO.class);

        return new ResponseEntity<>(responseFindUserVO, HttpStatus.OK);
    }

    // 회원 코드로 회원 정보 조회
    @Operation(summary = "회원코드로 회원 단건 조회")
    @GetMapping("/usercode/{userCode}")
    public ResponseEntity<ResponseFindUserVO> findUserById(@PathVariable String userCode) {
        UserDTO userDTO = userService.findById(userCode);
        ResponseFindUserVO responseFindUserVO = modelMapper.map(userDTO, ResponseFindUserVO.class);

        return new ResponseEntity<>(responseFindUserVO, HttpStatus.OK);
    }

    // 회원 탈퇴
    @Operation(summary = "회원 탈퇴")
    @PatchMapping("/delete/{userCode}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userCode) {
        userService.deleteUser(userCode);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @Operation(summary = "닉네임 중복체크")
    @GetMapping("/nickname/check") // users/nickname/check
    public ResponseEntity<Map<String, Boolean>> checkDuplicateNickname(@RequestParam String userNickname){

        Boolean isValid = userService.checkValideNickname(userNickname);

        // JSON 형태로 반환할 Map 생성
        Map<String, Boolean> response = new HashMap<>();
        response.put("isValid", isValid);

        return ResponseEntity.status(HttpStatus.OK).body(response); // false면 사용 불가
    }

    // 여기서 회원가입 시킬 예정
    @Operation(summary = "OAuth2 유저 회원가입")
    @PostMapping("/oauth2")
    public ResponseEntity<String> saveOauth2User(@RequestBody RequestOauth2VO requestOauth2VO){

        // userCode는 여기서 생성해서 저장하자.
        // member type도 여기서
        // flag도 여기서
        // 이것들 모두 oauth 로그인 과정에서 저장되어서 저걸 안해도 됨.
        userService.saveOauth2User(requestOauth2VO);

        return ResponseEntity.status(HttpStatus.OK).body("잘 저장했습니다");
    }

    @Operation(summary = "회원 헬스장 등록")
    @PostMapping("/register-gym")
    public ResponseEntity<String> registerGym(@RequestBody RegisterGymPerUserRequest registerGymRequest) {
        try {
            userService.updateUserGym(registerGymRequest);
            return ResponseEntity.ok("헬스장 등록이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("헬스장 등록에 실패했습니다.");
        }
    }

    @Operation(summary = "회원 헬스장 삭제")
    @PostMapping("/remove-gym")
    public ResponseEntity<String> removeGym(@RequestBody RegisterGymPerUserRequest registerGymRequest) {
        try {
            userService.deleteUserGym(registerGymRequest);
            return ResponseEntity.ok("헬스장이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("헬스장 삭제에 실패했습니다.");
        }
    }

    @Operation(summary = "비밀번호 재설정")
    @PostMapping("/password")
    public ResponseEntity<String> resetPassword(@RequestBody RequestResetPasswordVO request) {

        userService.resetPassword(request);

        return ResponseEntity.status(HttpStatus.OK).body("잘 수정 됐습니다.");
    }

    // 핸드폰 인증번호 전송
    @Operation(summary = "아이디(이메일) 찾기- 유저 핸드폰 인증번호 전송")
    @PostMapping(value = "/send-sms", produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> sendSmsVerification(@RequestBody SmsVerificationRequestVO smsVerificationRequestVO) {
        String verificationCode = userService.sendSmsForVerification(smsVerificationRequestVO.getUserPhone());
        return ResponseEntity.ok("[Healthtart] 다음 인증번호를 입력해주세요\n " + verificationCode);
    }

    // 인증번호 확인 후 이메일 반환
    @Operation(summary = "아이디(이메일) 찾기 - 유저 이메일 반환")
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCodeAndGetEmail(@RequestBody EmailRequestVO emailRequestVO) {
        try {
            String userEmail = userService.verifyCodeAndFindEmail(emailRequestVO.getUserPhone(), emailRequestVO.getVerificationCode());

            // 성공 시 이메일 응답
            Map<String, String> response = new HashMap<>();
            response.put("userEmail", userEmail);
            return ResponseEntity.ok("사용자의 이메일은 " + userEmail + " 입니다.");

        } catch (CoolSmsException e) {

            // 예외 발생 시 예외 메시지 반환
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("errorMessage", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
