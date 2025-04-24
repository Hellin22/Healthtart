package com.dev5ops.healthtart.exercise_equipment.repository;

import com.dev5ops.healthtart.exercise_equipment.domain.entity.ExerciseEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseEquipmentRepository extends JpaRepository<ExerciseEquipment, Long> {

    Optional<ExerciseEquipment> findByExerciseEquipmentName(String exerciseEquipmentName);

    List<ExerciseEquipment> findByBodyPart(String bodyPart);
}
