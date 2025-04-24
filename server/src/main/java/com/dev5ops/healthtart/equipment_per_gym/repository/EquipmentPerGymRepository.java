package com.dev5ops.healthtart.equipment_per_gym.repository;

import com.dev5ops.healthtart.equipment_per_gym.domain.entity.EquipmentPerGym;
import com.dev5ops.healthtart.exercise_equipment.domain.entity.ExerciseEquipment;
import com.dev5ops.healthtart.gym.domain.entity.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentPerGymRepository extends JpaRepository<EquipmentPerGym, Long> {
    boolean existsByGymAndExerciseEquipment(Gym gym, ExerciseEquipment exerciseEquipment);

    List<EquipmentPerGym> findByGym_GymCodeAndExerciseEquipment_BodyPart(Long equipmentPerGymCode, String bodyPart);
}
