package com.dev5ops.healthtart.equipment_per_gym.controller;

import com.dev5ops.healthtart.equipment_per_gym.domain.vo.request.RequestEditEquipmentPerGymVO;
import com.dev5ops.healthtart.equipment_per_gym.domain.vo.request.RequestRegisterEquipmentPerGymVO;
import com.dev5ops.healthtart.equipment_per_gym.domain.vo.response.ResponseEditEquipmentPerGymVO;
import com.dev5ops.healthtart.equipment_per_gym.domain.vo.response.ResponseFindEquipmentPerGymVO;
import com.dev5ops.healthtart.equipment_per_gym.domain.vo.response.ResponseRegisterEquipmentPerGymVO;
import com.dev5ops.healthtart.equipment_per_gym.domain.dto.EquipmentPerGymDTO;
import com.dev5ops.healthtart.equipment_per_gym.service.EquipmentPerGymService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController("equipmentPerGymController")
@RequestMapping("equipment_per_gym")
@Slf4j
public class EquipmentPerGymController {

    private final EquipmentPerGymService equipmentPerGymService;
    private final ModelMapper modelMapper;

    @Autowired
    public EquipmentPerGymController(EquipmentPerGymService equipmentPerGymService, ModelMapper modelMapper) {
        this.equipmentPerGymService = equipmentPerGymService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "관리자 - 헬스장 별 운동기구 등록")
    @PostMapping("/register")
    public ResponseEntity<ResponseRegisterEquipmentPerGymVO> registerEquipmentPerGym(@RequestBody RequestRegisterEquipmentPerGymVO request) {
        EquipmentPerGymDTO equipmentPerGymDTO = modelMapper.map(request, EquipmentPerGymDTO.class);
        System.out.println(equipmentPerGymDTO.toString());
        EquipmentPerGymDTO registeredEquipment = equipmentPerGymService.registerEquipmentPerGym(equipmentPerGymDTO);

        ResponseRegisterEquipmentPerGymVO response = new ResponseRegisterEquipmentPerGymVO(
                registeredEquipment.getEquipmentPerGymCode(),
                registeredEquipment.getCreatedAt(),
                registeredEquipment.getUpdatedAt(),
                registeredEquipment.getGym(),
                registeredEquipment.getExerciseEquipment()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "관리자 - 헬스장 별 운동기구 수정")
    @PatchMapping("/{equipmentPerGymCode}/edit")
    public ResponseEntity<ResponseEditEquipmentPerGymVO> editEquipmentPerGym(@PathVariable("equipmentPerGymCode") Long equipmentPerGymCode, @RequestBody RequestEditEquipmentPerGymVO request) {
        EquipmentPerGymDTO newEquipmentPerGymDTO = equipmentPerGymService.editEquipmentPerGym(equipmentPerGymCode, request);

        ResponseEditEquipmentPerGymVO response = new ResponseEditEquipmentPerGymVO(
                newEquipmentPerGymDTO.getUpdatedAt(),
                newEquipmentPerGymDTO.getGym(),
                newEquipmentPerGymDTO.getExerciseEquipment()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 - 헬스장 별 운동기구 삭제")
    @DeleteMapping("/{equipmentPerGymCode}/delete")
    public ResponseEntity<String> deleteEquipmentPerGym(@PathVariable Long equipmentPerGymCode) {
        equipmentPerGymService.deleteEquipmentPerGym(equipmentPerGymCode);

        return ResponseEntity.ok("헬스장의 운동기구가 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "관리자, 유저 - 헬스장 별 운동기구 단 건 조회")
    @GetMapping("/{equipmentPerGymCode}")
    public ResponseEntity<ResponseFindEquipmentPerGymVO> getEquipmentPerGym(@PathVariable Long equipmentPerGymCode) {
        EquipmentPerGymDTO equipmentPerGymDTO =  equipmentPerGymService.findEquipmentPerGymByCode(equipmentPerGymCode);

        ResponseFindEquipmentPerGymVO response = new ResponseFindEquipmentPerGymVO(
                equipmentPerGymDTO.getGym(),
                equipmentPerGymDTO.getExerciseEquipment()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "관리자, 유저 - 헬스장 별 운동기구 전체 조회")
    @GetMapping("/equipment_per_gym_list")
    public ResponseEntity<List<ResponseFindEquipmentPerGymVO>> getEquipmentPerGymList() {
        List<EquipmentPerGymDTO> equipmentPerGymDTOList =  equipmentPerGymService.findAllEquipmentPer();
        List<ResponseFindEquipmentPerGymVO> responseList = new ArrayList<>();

        for (EquipmentPerGymDTO equipmentPerGymDTO : equipmentPerGymDTOList) {
            ResponseFindEquipmentPerGymVO response = new ResponseFindEquipmentPerGymVO(
                    equipmentPerGymDTO.getGym(),
                    equipmentPerGymDTO.getExerciseEquipment()
            );

            responseList.add(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @Operation(summary = "관리자, 유저 - 특정한 헬스장 내 부위 별 운동기구 조회")
    @GetMapping("/{equipmentPerGymCode}/body_part")
    public ResponseEntity<List<ResponseFindEquipmentPerGymVO>> getEquipmentPerGymByBodyPart(@PathVariable Long equipmentPerGymCode, @RequestParam("bodyPart") String bodyPart) {
        List<EquipmentPerGymDTO> equipmentPerGymDTOList = equipmentPerGymService.findEquipmentByBodyPart(equipmentPerGymCode, bodyPart);
        List<ResponseFindEquipmentPerGymVO> responseList = new ArrayList<>();

        for (EquipmentPerGymDTO equipmentPerGymDTO : equipmentPerGymDTOList) {
            ResponseFindEquipmentPerGymVO response = new ResponseFindEquipmentPerGymVO(
                    equipmentPerGymDTO.getGym(),
                    equipmentPerGymDTO.getExerciseEquipment()
            );

            responseList.add(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }
}
