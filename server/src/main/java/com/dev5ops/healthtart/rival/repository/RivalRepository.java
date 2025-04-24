package com.dev5ops.healthtart.rival.repository;

import com.dev5ops.healthtart.rival.domain.dto.RivalDTO;
import com.dev5ops.healthtart.rival.domain.dto.RivalUserInbodyDTO;
import com.dev5ops.healthtart.rival.domain.dto.RivalUserInbodyScoreDTO;
import com.dev5ops.healthtart.rival.domain.entity.Rival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RivalRepository extends JpaRepository<Rival, Long> {

    // Rival 엔티티에서 user 필드의 userCode 값을 기준으로 데이터를 찾는다는 의미.
    @Query("SELECT new com.dev5ops.healthtart.rival.domain.dto.RivalDTO(r.rivalMatchCode, r.user.userCode, r.rivalUser.userCode, r.createdAt, r.updatedAt) FROM Rival r WHERE r.user.userCode = :userCode")
    RivalDTO findByUser_UserCode(String userCode);

    @Query("SELECT new com.dev5ops.healthtart.rival.domain.dto.RivalUserInbodyScoreDTO(r.rivalMatchCode, ru.userCode, ru.userName, ru.userGender, ru.userHeight, ru.userWeight, ru.userAge, ru.userFlag, i.inbodyScore) " +
            "FROM Rival r " +
            "JOIN r.user u " +
            "JOIN r.rivalUser ru " +
            "JOIN FETCH inbody i ON i.user.userCode = ru.userCode " +  // Fetch join을 사용하여 즉시 데이터 로드
            "WHERE u.userCode = :userCode " +
            "AND i.createdAt = (SELECT MAX(i2.createdAt) FROM inbody i2 WHERE i2.user.userCode = ru.userCode)")
    List<RivalUserInbodyScoreDTO> findRivalUsersInbodyScoreByUserCode(@Param("userCode") String userCode);

    // rival match code 가져오는 메서드
    @Query("SELECT r.rivalMatchCode " +
            "FROM Rival r " +
            "WHERE r.user.userCode = :userCode AND r.rivalUser.userCode = :rivalUserCode")
    Long findRivalMatchCode(@Param("userCode") String userCode, @Param("rivalUserCode") String rivalUserCode);

    // 인바디 정보 가져오는 메서드
    @Query("SELECT new com.dev5ops.healthtart.rival.domain.dto.RivalUserInbodyDTO(" +
            "null, u.userCode, u.userName, u.userGender, u.userHeight, u.userWeight, u.userAge, u.userFlag, " +
            "i.inbodyScore, i.height, i.weight, i.muscleWeight, i.fatWeight, i.bmi, i.fatPercentage, i.basalMetabolicRate) " +
            "FROM UserEntity u " +
            "JOIN inbody i ON i.user.userCode = u.userCode " +
            "WHERE u.userCode = :userCode " +
            "AND i.createdAt = (SELECT MAX(i2.createdAt) FROM inbody i2 WHERE i2.user.userCode = u.userCode)")
    RivalUserInbodyDTO findUserInbodyByUserCode(@Param("userCode") String userCode);
}
