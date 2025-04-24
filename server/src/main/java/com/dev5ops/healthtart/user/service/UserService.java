package com.dev5ops.healthtart.user.service;

import com.dev5ops.healthtart.user.domain.dto.*;
import com.dev5ops.healthtart.user.domain.vo.request.RegisterGymPerUserRequest;
import com.dev5ops.healthtart.user.domain.vo.request.RequestInsertUserVO;
import com.dev5ops.healthtart.user.domain.vo.request.RequestOauth2VO;
import com.dev5ops.healthtart.user.domain.vo.request.RequestResetPasswordVO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService extends UserDetailsService {
    void signUpUser(RequestInsertUserVO request);

    List<UserDTO> findAllUsers();

    UserDTO findUserByEmail(String userEmail);

    UserDTO findById(String userCode);

    void deleteUser(String userCode);

    Boolean checkValideNickname(String userNickname);

    void saveOauth2User(RequestOauth2VO requestOauth2VO);

    ResponseMypageDTO getMypageInfo();

    void editPassword(EditPasswordDTO editPasswordDTO);

    void updateUserGym(RegisterGymPerUserRequest registerGymRequest);

    void deleteUserGym(RegisterGymPerUserRequest registerGymRequest);

    void resetPassword(RequestResetPasswordVO request);

    String sendSmsForVerification(String userPhone);

    String verifyCodeAndFindEmail(String userPhone, String verificationCode);

    void findUserByEmail2(String email);

    void editUserInfo(UserDTO userDTO);
}
