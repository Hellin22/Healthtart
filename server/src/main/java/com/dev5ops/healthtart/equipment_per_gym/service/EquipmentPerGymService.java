package com.dev5ops.healthtart.equipment_per_gym.service;

import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.equipment_per_gym.domain.entity.EquipmentPerGym;
import com.dev5ops.healthtart.equipment_per_gym.domain.vo.request.RequestEditEquipmentPerGymVO;
import com.dev5ops.healthtart.equipment_per_gym.domain.dto.EquipmentPerGymDTO;
import com.dev5ops.healthtart.equipment_per_gym.repository.EquipmentPerGymRepository;
import com.dev5ops.healthtart.exercise_equipment.domain.entity.ExerciseEquipment;
import com.dev5ops.healthtart.exercise_equipment.repository.ExerciseEquipmentRepository;
import com.dev5ops.healthtart.gym.domain.entity.Gym;
import com.dev5ops.healthtart.gym.repository.GymRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service("equipmentPerGymService")
@RequiredArgsConstructor
@Slf4j
public class EquipmentPerGymService {
    private final EquipmentPerGymRepository equipmentPerGymRepository;
    private final ModelMapper modelMapper;
    private final GymRepository gymRepository;
    private final ExerciseEquipmentRepository exerciseEquipmentRepository;

    @Transactional
    public EquipmentPerGymDTO registerEquipmentPerGym(EquipmentPerGymDTO equipmentPerGymDTO) {
        if (equipmentPerGymDTO.getGym() == null || equipmentPerGymDTO.getGym().getGymCode() == null) {
            throw new CommonException(StatusEnum.GYM_NOT_FOUND);
        }
        if (equipmentPerGymDTO.getExerciseEquipment() == null || equipmentPerGymDTO.getExerciseEquipment().getExerciseEquipmentCode() == null) {
            throw new CommonException(StatusEnum.EQUIPMENT_NOT_FOUND);
        }

        Gym gym = gymRepository.findById(equipmentPerGymDTO.getGym().getGymCode()).orElseThrow(() -> new CommonException(StatusEnum.GYM_NOT_FOUND));
        ExerciseEquipment exerciseEquipment = exerciseEquipmentRepository.findById(equipmentPerGymDTO.getExerciseEquipment().getExerciseEquipmentCode()).orElseThrow(() -> new CommonException(StatusEnum.EQUIPMENT_NOT_FOUND));
        boolean isAlreadyRegistered = equipmentPerGymRepository.existsByGymAndExerciseEquipment(gym, exerciseEquipment);
        if (isAlreadyRegistered) {
            throw new CommonException(StatusEnum.EQUIPMENT_ALREADY_REGISTERED); // 이미 등록된 헬스 기구 예외 발생
        }

        EquipmentPerGym equipmentPerGym = modelMapper.map(equipmentPerGymDTO, EquipmentPerGym.class);
        equipmentPerGym.setGym(gym);
        equipmentPerGym.setExerciseEquipment(exerciseEquipment);
        equipmentPerGym.setCreatedAt(LocalDateTime.now());
        equipmentPerGym.setUpdatedAt(LocalDateTime.now());

        EquipmentPerGym savedEquipmentPerGym = equipmentPerGymRepository.save(equipmentPerGym);

        return modelMapper.map(savedEquipmentPerGym, EquipmentPerGymDTO.class);
    }

    @Transactional
    public EquipmentPerGymDTO editEquipmentPerGym(Long equipmentPerGymCode, RequestEditEquipmentPerGymVO request) {
        gymRepository.findById(request.getGym().getGymCode()).orElseThrow(() -> new CommonException(StatusEnum.GYM_NOT_FOUND));
        exerciseEquipmentRepository.findById(request.getExerciseEquipment().getExerciseEquipmentCode()).orElseThrow(() -> new CommonException(StatusEnum.EQUIPMENT_NOT_FOUND));

        EquipmentPerGym equipmentPerGym = equipmentPerGymRepository.findById(equipmentPerGymCode).orElseThrow(() -> new CommonException(StatusEnum.EQUIPMENT_PER_GYM_NOT_FOUND));

        equipmentPerGym.setGym(request.getGym());
        equipmentPerGym.setExerciseEquipment(request.getExerciseEquipment());
        equipmentPerGym.setUpdatedAt(LocalDateTime.now());

        EquipmentPerGym savedEquipmentPerGym = equipmentPerGymRepository.save(equipmentPerGym);

        return modelMapper.map(savedEquipmentPerGym, EquipmentPerGymDTO.class);
    }

    @Transactional
    public void deleteEquipmentPerGym(Long equipmentPerGymCode) {
        EquipmentPerGym equipmentPerGym = equipmentPerGymRepository.findById(equipmentPerGymCode).get();

        gymRepository.findById(equipmentPerGym.getGym().getGymCode()).orElseThrow(() -> new CommonException(StatusEnum.GYM_NOT_FOUND));
        exerciseEquipmentRepository.findById(equipmentPerGym.getExerciseEquipment().getExerciseEquipmentCode()).orElseThrow(() -> new CommonException(StatusEnum.EQUIPMENT_NOT_FOUND));

        equipmentPerGymRepository.delete(equipmentPerGym);
    }

    public EquipmentPerGymDTO findEquipmentPerGymByCode(Long equipmentPerGymCode) {
        EquipmentPerGym equipmentPerGym = equipmentPerGymRepository.findById(equipmentPerGymCode).orElseThrow(() -> new CommonException(StatusEnum.EQUIPMENT_PER_GYM_NOT_FOUND));

        return modelMapper.map(equipmentPerGym, EquipmentPerGymDTO.class);
    }

    public List<EquipmentPerGymDTO> findAllEquipmentPer() {
        List<EquipmentPerGym> equipmentPerGyms = equipmentPerGymRepository.findAll();

        return equipmentPerGyms.stream()
                .map(equipmentPerGym -> modelMapper.map(equipmentPerGym, EquipmentPerGymDTO.class))
                .collect(Collectors.toList());
    }

    public List<EquipmentPerGymDTO> findEquipmentByBodyPart(Long equipmentPerGymCode, String bodyPart) {
        List<EquipmentPerGym> equipmentPerGyms = equipmentPerGymRepository.findByGym_GymCodeAndExerciseEquipment_BodyPart(equipmentPerGymCode, bodyPart);

        return equipmentPerGyms.stream()
                .map(equipmentPerGym -> modelMapper.map(equipmentPerGym, EquipmentPerGymDTO.class))
                .collect(Collectors.toList());
    }
}