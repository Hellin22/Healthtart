package com.dev5ops.healthtart.gpt.controller;

import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.gpt.domain.vo.RequestRegisterRoutineVO;
import com.dev5ops.healthtart.gpt.service.GptService;
import com.dev5ops.healthtart.user.domain.dto.UserDTO;
import com.dev5ops.healthtart.user.service.UserService;
import com.dev5ops.healthtart.exercise_equipment.service.ExerciseEquipmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/gpt")
@RequiredArgsConstructor
public class GptController {

    private final GptService gptService;
    private final UserService userService;
    private final ExerciseEquipmentService exerciseEquipmentService;

    @PostMapping("/generate-routine")
    @Operation(summary = "GPT 운동 루틴 생성")
    public ResponseEntity<String> generateRoutine(@RequestBody RequestRegisterRoutineVO request) {
        try {
            UserDTO userDTO = userService.findById(request.getUserCode());
            if (userDTO == null) {
                return ResponseEntity.badRequest().body(StatusEnum.USER_NOT_FOUND.getMessage());
            }

            var equipmentList = exerciseEquipmentService.findByBodyPart(request.getBodyPart());
            if (equipmentList.isEmpty()) {
                return ResponseEntity.badRequest().body(StatusEnum.EQUIPMENT_NOT_FOUND.getMessage());
            }

            String routine = gptService.generateRoutine(request.getUserCode(), request.getBodyPart(), request.getTime());
            Map<String, Object> workoutData = gptService.routineParser(routine);

            Map<String, Object> response = new HashMap<>();
            response.put("title", workoutData.get("title"));
            response.put("totalTime", request.getTime());  // 클라이언트에서 보낸 시간 사용
            response.put("musicList", workoutData.get("musicList"));
            response.put("bodyPart", request.getBodyPart());  // 클라이언트에서 보낸 bodyPart 사용

            List<Map<String, Object>> exercises = new ArrayList<>();
            int i = 1;
            while (workoutData.containsKey("workoutName" + i)) {
                Map<String, Object> exercise = new HashMap<>();
                exercise.put("workoutName", workoutData.get("workoutName" + i));
                exercise.put("exerciseExplanation", workoutData.get("exerciseExplanation" + i));
                exercise.put("exerciseVideo", workoutData.get("exerciseVideo" + i));
                exercise.put("weightSet", workoutData.get("weightSet" + i));
                exercise.put("numberPerSet", workoutData.get("numberPerSet" + i));
                exercise.put("weightPerSet", workoutData.get("weightPerSet" + i));
                exercise.put("workoutTime", workoutData.get("workoutTime" + i));
                exercises.add(exercise);
                i++;
            }
            response.put("exercises", exercises);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(response);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonResponse);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(StatusEnum.ROUTINES_CREATED_ERROR.getMessage());
        }
    }

    @PostMapping("/process-routine")
    @Operation(summary = "GPT 운동 루틴 저장")
    public ResponseEntity<Map<String, Object>> processRoutine(@RequestBody String response) {
        try {
            Long routineCode = gptService.processRoutine(response);

            // 응답 데이터 구성
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", "운동 루틴이 성공적으로 저장되었습니다.");
            responseData.put("routineCode", routineCode);  // routineCode 포함

            return ResponseEntity.ok(responseData);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "운동 루틴 처리 중 오류가 발생했습니다: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "루틴 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}
