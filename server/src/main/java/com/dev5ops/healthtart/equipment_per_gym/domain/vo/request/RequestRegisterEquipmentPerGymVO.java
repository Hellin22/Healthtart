package com.dev5ops.healthtart.equipment_per_gym.domain.vo.request;

import com.dev5ops.healthtart.exercise_equipment.domain.entity.ExerciseEquipment;
import com.dev5ops.healthtart.gym.domain.entity.Gym;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequestRegisterEquipmentPerGymVO {
    private Gym gym;
    private ExerciseEquipment exerciseEquipment;
}
