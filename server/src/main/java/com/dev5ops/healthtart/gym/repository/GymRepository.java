package com.dev5ops.healthtart.gym.repository;

import com.dev5ops.healthtart.gym.domain.entity.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {

    Optional<Gym> findByBusinessNumber(String businessNumber);
}
