package com.dev5ops.healthtart.exercise_equipment.controller;

import com.dev5ops.healthtart.exercise_equipment.domain.vo.request.RequestEditEquipmentVO;
import com.dev5ops.healthtart.exercise_equipment.domain.vo.request.RequestRegisterEquipmentVO;
import com.dev5ops.healthtart.exercise_equipment.domain.vo.response.ResponseEditEquipmentVO;
import com.dev5ops.healthtart.exercise_equipment.domain.vo.response.ResponseFindEquipmentVO;
import com.dev5ops.healthtart.exercise_equipment.domain.vo.response.ResponseRegisterEquipmentVO;
import com.dev5ops.healthtart.exercise_equipment.domain.dto.ExerciseEquipmentDTO;
import com.dev5ops.healthtart.exercise_equipment.service.ExerciseEquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController("exerciseEquipmentController")
@RequestMapping("exercise_equipment")
@Slf4j
public class ExerciseEquipmentController {
    private final ExerciseEquipmentService exerciseEquipmentService;
    private final ModelMapper modelMapper;

    @Autowired
    public ExerciseEquipmentController(ExerciseEquipmentService exerciseEquipmentService, ModelMapper modelMapper) {
        this.exerciseEquipmentService = exerciseEquipmentService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "관리자 - 운동기구 등록")
    @PostMapping("/register")
    public ResponseEntity<ResponseRegisterEquipmentVO> registerEquipment(@RequestBody RequestRegisterEquipmentVO request) {
        ExerciseEquipmentDTO equipmentDTO = modelMapper.map(request, ExerciseEquipmentDTO.class);
        ExerciseEquipmentDTO registerEquipment = exerciseEquipmentService.registerEquipment(equipmentDTO);

        ResponseRegisterEquipmentVO response = new ResponseRegisterEquipmentVO(
                registerEquipment.getExerciseEquipmentCode(),
                registerEquipment.getExerciseEquipmentName(),
                registerEquipment.getBodyPart(),
                registerEquipment.getExerciseDescription(),
                registerEquipment.getExerciseImage(),
                registerEquipment.getRecommendedVideo(),
                registerEquipment.getCreatedAt(),
                registerEquipment.getUpdatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "관리자 - 운동기구 정보 수정")
    @PatchMapping("/{exerciseEquipmentCode}/edit")
    public ResponseEntity<ResponseEditEquipmentVO> editEquipment(@PathVariable("exerciseEquipmentCode") Long exerciseEquipmentCode, @RequestBody RequestEditEquipmentVO request) {
        ExerciseEquipmentDTO updatedEquipment = exerciseEquipmentService.editEquipment(exerciseEquipmentCode, request);
        ResponseEditEquipmentVO response = new ResponseEditEquipmentVO(
                updatedEquipment.getExerciseEquipmentName(),
                updatedEquipment.getBodyPart(),
                updatedEquipment.getExerciseDescription(),
                updatedEquipment.getExerciseImage(),
                updatedEquipment.getRecommendedVideo(),
                updatedEquipment.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 - 운동기구 정보 삭제")
    @DeleteMapping("/{exerciseEquipmentCode}/delete")
    public ResponseEntity<String> deleteEquipment(@PathVariable("exerciseEquipmentCode") Long exerciseEquipmentCode) {
        exerciseEquipmentService.deleteEquipment(exerciseEquipmentCode);

        return ResponseEntity.ok("운동기구가 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "관리자, 유저 - 운동기구 단 건 조회")
    @GetMapping("/{exerciseEquipmentCode}")
    public ResponseEntity<ResponseFindEquipmentVO> getEquipment(@PathVariable("exerciseEquipmentCode") Long exerciseEquipmentCode) {
        ExerciseEquipmentDTO equipmentDTO = exerciseEquipmentService.findEquipmentByEquipmentCode(exerciseEquipmentCode);

        ResponseFindEquipmentVO response = new ResponseFindEquipmentVO(
                equipmentDTO.getExerciseEquipmentName(),
                equipmentDTO.getBodyPart(),
                equipmentDTO.getExerciseDescription(),
                equipmentDTO.getExerciseImage(),
                equipmentDTO.getRecommendedVideo()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "관리자, 유저 - 운동기구 전체 조회")
    @GetMapping("/equipment_list")
    public ResponseEntity<List<ResponseFindEquipmentVO>> getEquipmentList() {
        List<ExerciseEquipmentDTO> equipmentDTOList = exerciseEquipmentService.findAllEquipment();
        List<ResponseFindEquipmentVO> responseList = new ArrayList<>();

        for (ExerciseEquipmentDTO equipmentDTO : equipmentDTOList) {
            ResponseFindEquipmentVO response = new ResponseFindEquipmentVO(
                    equipmentDTO.getExerciseEquipmentName(),
                    equipmentDTO.getBodyPart(),
                    equipmentDTO.getExerciseDescription(),
                    equipmentDTO.getExerciseImage(),
                    equipmentDTO.getRecommendedVideo()
            );

            responseList.add(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @Operation(summary = "관리자, 유저 - 운동 부위별 운동기구 조회")
    @GetMapping("/by_body_part")
    public ResponseEntity<List<ResponseFindEquipmentVO>> getEquipmentByBodyPart(@RequestParam("bodyPart") String bodyPart) {
        List<ExerciseEquipmentDTO> equipmentDTOList = exerciseEquipmentService.findByBodyPart(bodyPart);
        List<ResponseFindEquipmentVO> responseList = new ArrayList<>();

        for (ExerciseEquipmentDTO equipmentDTO : equipmentDTOList) {
            ResponseFindEquipmentVO response = new ResponseFindEquipmentVO(
                    equipmentDTO.getExerciseEquipmentName(),
                    equipmentDTO.getBodyPart(),
                    equipmentDTO.getExerciseDescription(),
                    equipmentDTO.getExerciseImage(),
                    equipmentDTO.getRecommendedVideo()
            );
            responseList.add(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }
}
