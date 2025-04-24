package com.dev5ops.healthtart.user.repository;

import com.dev5ops.healthtart.user.domain.dto.ResponseMypageDTO;
import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    UserEntity findByUserEmail(String userEmail);

    UserEntity findByProviderAndProviderId(String provider, String providerId);

    UserEntity findByUserNickname(String userNickname);

    @Query("SELECT new com.dev5ops.healthtart.user.domain.dto.ResponseMypageDTO(u.userName, u.userEmail, u.userPassword, u.userPhone, u.userNickname, " +
            "u.userGender, u.userHeight, u.userWeight, u.updatedAt, g.gymCode, g.gymName, r.rivalUser.userCode, ru.userNickname) " +
            "FROM UserEntity u " +
            "LEFT JOIN u.gym g " +  // UserEntity와 Gym 엔티티를 조인
            "LEFT JOIN Rival r ON r.user.userCode = u.userCode " +
            "LEFT JOIN UserEntity ru ON ru.userCode = r.rivalUser.userCode " +
            "WHERE u.userCode = :userCode")
    ResponseMypageDTO findMypageInfo(@Param("userCode") String userCode);

    // 핸드폰 번호로 이메일 조회
    @Query("SELECT u.userEmail FROM UserEntity u WHERE u.userPhone = :userPhone")
    String findUserEmailByUserPhone(@Param("userPhone") String userPhone);


}
