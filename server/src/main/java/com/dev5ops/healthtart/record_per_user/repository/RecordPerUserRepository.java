package com.dev5ops.healthtart.record_per_user.repository;

import com.dev5ops.healthtart.record_per_user.domain.entity.RecordPerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecordPerUserRepository extends JpaRepository<RecordPerUser, Long> {
    @Query("SELECT ru FROM RecordPerUser ru JOIN FETCH ru.user WHERE ru.user.userCode = :userCode")
    List<RecordPerUser> findUserByUserCode(@Param("userCode") String userCode);

    List<RecordPerUser> findByUser_UserCodeAndDayOfExercise(String userCode, LocalDateTime dayOfExercise);

    boolean existsByUser_UserCode(String userCode);
}
