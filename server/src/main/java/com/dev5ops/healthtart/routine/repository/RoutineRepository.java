package com.dev5ops.healthtart.routine.repository;

import com.dev5ops.healthtart.routine.domain.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
}
