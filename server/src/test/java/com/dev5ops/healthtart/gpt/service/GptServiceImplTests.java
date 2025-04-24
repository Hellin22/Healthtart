//package com.dev5ops.healthtart.gpt.service;
//
//import com.dev5ops.healthtart.exercise_equipment.domain.dto.ExerciseEquipmentDTO;
//import com.dev5ops.healthtart.exercise_equipment.service.ExerciseEquipmentService;
//import com.dev5ops.healthtart.routine.domain.dto.RoutineDTO;
//import com.dev5ops.healthtart.routine.service.RoutineService;
//import com.dev5ops.healthtart.user.domain.dto.UserDTO;
//import com.dev5ops.healthtart.user.service.UserService;
//import com.dev5ops.healthtart.workout_per_routine.domain.dto.WorkoutPerRoutineDTO;
//import com.dev5ops.healthtart.workout_per_routine.service.WorkoutPerRoutineService;
//import com.dev5ops.healthtart.workoutinfo.domain.dto.WorkoutInfoDTO;
//import com.dev5ops.healthtart.workoutinfo.service.WorkoutInfoService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class GptServiceImplTests {
//
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    @Mock
//    private ExerciseEquipmentService exerciseEquipmentService;
//
//    @Autowired
//    @InjectMocks
//    private GptServiceImpl gptServiceImpl;
//
//    @Mock
//    private RoutineService routineService;
//
//    @Mock
//    private WorkoutPerRoutineService workoutPerRoutineService;
//
//    @Mock
//    private WorkoutInfoService workoutInfoService;
//
//
//    private String userCode;
//    private String bodyPart;
//    private int workoutTime;
//    private String gptResponse;
//
//    @BeforeEach
//    public void setUp() {
//        userCode = "20241007-05bfb06b-8eda-4857-8681-40d1eccb829d";
//        bodyPart = "하체";
//        workoutTime = 90;
//    }
//
//    @Test
//    @DisplayName("유저 정보와 헬스기구 정보를 통한 GPT 루틴 생성 후 3개 도메인에 저장 테스트")
//    public void testGenerateRoutine() throws JsonProcessingException {
//        UserDTO userDTO = userService.findById(userCode);
//        assertNotNull(userDTO, "유저 정보를 찾을 수 없습니다.");
//
//        List<ExerciseEquipmentDTO> equipmentList = exerciseEquipmentService.findByBodyPart(bodyPart);
//        assertNotNull(equipmentList, "헬스 기구 정보가 조회되지 않았습니다.");
//        assertFalse(equipmentList.isEmpty(), "해당 운동 부위에 대한 헬스 기구가 존재하지 않습니다.");
//
//        String response = gptServiceImpl.generateRoutine(userCode, bodyPart, workoutTime);
//        assertNotNull(response, "GPT 응답이 null입니다.");
//        assertTrue(response.contains("하체"), "응답에 운동 부위 정보가 포함되지 않았습니다.");
//        assertTrue(response.contains("레그 프레스"), "응답에 레그 프레스 정보가 포함되지 않았습니다.");
//
//        gptServiceImpl.processRoutine(response);
//
//    }
//
//}
