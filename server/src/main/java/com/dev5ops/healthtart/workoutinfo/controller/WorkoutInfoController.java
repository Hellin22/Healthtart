package com.dev5ops.healthtart.workoutinfo.controller;

import com.dev5ops.healthtart.routine.domain.vo.request.RequestModifyRoutineVO;
import com.dev5ops.healthtart.workoutinfo.domain.dto.WorkoutInfoDTO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.EditWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.request.RequestInsertWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.request.RequestModifyWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.response.ResponseDeleteWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.response.ResponseFindWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.response.ResponseInsertWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.domain.vo.response.ResponseModifyWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.service.WorkoutInfoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workoutInfos")
@RequiredArgsConstructor
public class WorkoutInfoController {


    private final WorkoutInfoServiceImpl workoutInfoService;
    private final ModelMapper modelMapper;

    @GetMapping
    @Operation(summary = "운동 정보 전체 조회")
    public ResponseEntity<List<ResponseFindWorkoutInfoVO>> getAllWorkoutInfos() {
        List<ResponseFindWorkoutInfoVO> workoutInfos = workoutInfoService.getWorkoutInfos();
        return new ResponseEntity<>(workoutInfos, HttpStatus.OK);
    }

    @GetMapping("/{workoutInfoCode}")
    @Operation(summary = "운동 정보 단일 조회")
    public ResponseEntity<ResponseFindWorkoutInfoVO> getWorkoutInfoByCode(@PathVariable Long workoutInfoCode) {
        ResponseFindWorkoutInfoVO workoutInfo = workoutInfoService.findWorkoutInfoByCode(workoutInfoCode);
        return new ResponseEntity<>(workoutInfo, HttpStatus.OK);
    }

    @GetMapping("/detail/{routineCode}")
    @Operation(summary = "루틴 코드별 운동 정보 단일 조회")
    public ResponseEntity<?> getWorkoutInfoByRoutineCode(@PathVariable Long routineCode) {
        try {
            ResponseFindWorkoutInfoVO workoutInfo = workoutInfoService.getWorkoutInfoByRoutineCode(routineCode);
            return new ResponseEntity<>(workoutInfo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @Operation(summary = "운동 정보 등록")
    public ResponseEntity<ResponseInsertWorkoutInfoVO> registerWorkoutInfo(@RequestBody RequestInsertWorkoutInfoVO requestInsertWorkoutInfoVO) {
        WorkoutInfoDTO workoutInfoDTO = modelMapper.map(requestInsertWorkoutInfoVO, WorkoutInfoDTO.class);
        ResponseInsertWorkoutInfoVO response = workoutInfoService.registerWorkoutInfo(workoutInfoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{workoutInfoCode}")
    @Operation(summary = "운동 정보 수정")
    public ResponseEntity<ResponseModifyWorkoutInfoVO> modifyWorkoutInfo(@PathVariable("workoutInfoCode") Long workoutInfoCode,
                                                                         @RequestBody RequestModifyRoutineVO requestModify) {
        EditWorkoutInfoVO editworkoutInfoVO = modelMapper.map(requestModify, EditWorkoutInfoVO.class);
        ResponseModifyWorkoutInfoVO response = workoutInfoService.modifyWorkoutInfo(workoutInfoCode, editworkoutInfoVO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{workoutInfoCode}")
    @Operation(summary = "운동 정보 삭제")
    public ResponseEntity<ResponseDeleteWorkoutInfoVO> deleteWorkoutInfo(@RequestBody RequestModifyWorkoutInfoVO requestModifyWorkoutInfoVO) {
        WorkoutInfoDTO workoutInfoDTO = modelMapper.map(requestModifyWorkoutInfoVO, WorkoutInfoDTO.class);
        ResponseDeleteWorkoutInfoVO response = workoutInfoService.deleteWorkoutInfo(workoutInfoDTO.getRoutineCode());
        return ResponseEntity.ok(response);
    }

}
