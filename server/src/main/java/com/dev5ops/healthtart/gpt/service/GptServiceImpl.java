package com.dev5ops.healthtart.gpt.service;

import com.dev5ops.healthtart.common.config.GptConfig;
import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.exercise_equipment.domain.dto.ExerciseEquipmentDTO;
import com.dev5ops.healthtart.exercise_equipment.domain.entity.ExerciseEquipment;
import com.dev5ops.healthtart.exercise_equipment.service.ExerciseEquipmentService;
import com.dev5ops.healthtart.routine.domain.dto.RoutineDTO;
import com.dev5ops.healthtart.routine.service.RoutineService;
import com.dev5ops.healthtart.user.domain.dto.UserDTO;
import com.dev5ops.healthtart.user.service.UserService;
import com.dev5ops.healthtart.workout_per_routine.domain.dto.WorkoutPerRoutineDTO;
import com.dev5ops.healthtart.workout_per_routine.service.WorkoutPerRoutineService;
import com.dev5ops.healthtart.workoutinfo.domain.dto.WorkoutInfoDTO;
import com.dev5ops.healthtart.workoutinfo.service.WorkoutInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GptServiceImpl implements GptService {

    private final GptConfig gptConfig;
    private final RestTemplate restTemplate;
    private final ExerciseEquipmentService exerciseEquipmentService;
    private final UserService userService;
    private final WorkoutPerRoutineService workoutPerRoutineService;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final RoutineService routineService;
    private final WorkoutInfoService workoutInfoService;

    public String callOpenAI(String prompt, int maxTokens) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(gptConfig.getSecretKey());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", gptConfig.getModel());
        requestBody.put("messages", new Object[]{new HashMap<String, String>() {{
            put("role", "system");
            put("content", String.format("당신은 우주최고유명하고 운동을 잘가르치는 헬스트레이너야, " +
                    "초보자 맞춤형 운동 루틴을 제대로 잘 추천해 줘야해."));
        }},
                new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", prompt);
                }}
        });

        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", maxTokens);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String generatePrompt(String userCode, String bodyPart, int time, List<ExerciseEquipmentDTO> exerciseEquipmentDTO) {
        UserDTO user = userService.findById(userCode);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String formattedDate = LocalDateTime.now().format(formatter);

        StringBuilder prompt = new StringBuilder();
        prompt.append("저는 엄청 운동을 잘하고 싶은 초보자야. 키, 몸무게, 성별, 나이 그리고 원하는 운동부위와 운동할 시간을 제공할테니" +
                " 맞춤형 운동 루틴을 만들어 줘. (운동 맞춤 동영상은 한국에서 재생할 수 있는 2021년 이후에 업로드된 영상 링크를 제공해야 하는데 현재 존재하는 운동 관련 외국 영상 링크 클릭했더니 이 동영상을 더 이상 재생할 수 없습니다. 라고 나오면 진짜 진짜 화나고 너를 죽일수도있고, 죽일거야 (유효한 YouTube 영상만 포함해 줘 제발. 재생 가능해야해 링크 클릭했더니 이 동영상을 더 이상 재생할 수 없습니다. 라고 나오면 죽일거야 )." +
                "같은 사용자가 같은 정보로 운동 루틴을 재요청한다면 이전에 추천해준 운동루틴과 다른 운동루틴을 추천해 줘 " +
                "음악도 매번 다르게 추천해 줘.\n" +
                "오늘의 운동 루틴에는 형식과 조건을 꼭 지켜줘:\n");

        prompt.append(String.format(" 날짜 : %s, 키: %.0fcm, 몸무게: %.0fkg, 성별: %s, 나이: %d세, 운동 부위: %s, 운동할 시간: %d분"
                , formattedDate
                , user.getUserHeight()
                , user.getUserWeight()
                , user.getUserGender()
                , user.getUserAge()
                , bodyPart
                , time));
        String equipmentList = exerciseEquipmentDTO.stream()
                .map(ExerciseEquipmentDTO::getExerciseEquipmentName)
                .collect(Collectors.joining(", "));

        prompt.append("운동 추천을 위해 사용할 수 있는 운동 기구 리스트도 제공할게");
        prompt.append(String.format(" 운동에 필요한 기구: %s", equipmentList));

        prompt.append("아래에 제공된 형식을 사용해 줘 꼭!!!");
        prompt.append("오늘의 운동 루틴\n" +
                "제목: {운동 목적에 맞는 제목}\n" + "(제목은 재치있고 재미있게 작성해 줘)" +
                "날짜: {현재 날짜}\n" + "(날짜는 %s로 설정해줘.)" +
                "운동 부위: {사용자가 선택한 운동 부위}\n" +
                "키: {사용자 키}\n" +
                "몸무게: {사용자 몸무게}\n" +
                "성별: {사용자 성별}\n" +
                "나이: {사용자 나이}\n" +
                "운동 시간: {사용자가 입력한 운동 시간}\n" +
                "\n" +
                "오늘의 운동 루틴을 추천해 드립니다:\n" +
                "\n" +
                "n. {운동 명}\n" + "( 운동은 보통 60분에 5개의 종류를 합니다. 맨몸 운동을 섞어서 루틴을 작성해줘)" +
                "   - 세트 및 반복: {사용자정보에 따른 세트와 횟수 ex)2세트 x 3회}\n" +
                "   - 운동별 시간: { 운동별 실행 시간 ex)10분} " +
                "   - 운동 설명: {운동 설명}\n (운동 설명은 초보자가 이해하기 쉽고, 쉽게 따라할 수 있게 5줄이상 씩 작성해 줘.)" +
                "   - 중량: {사용자 정보에 따른 적절한 중량}\n" + "(중량은 사용자 정보에 따라 적절한 무게를 추천해 줘 (예: 5kg). 근데 만약 중량이 필요 없는 맨몸운동일 경우, '맨몸운동입니다.'라고 출력해 줘)" +
                "   - 추천 영상: {운동 관련 영상 링크}\n (운동에 맞는 설명과 동영상을 주는데 조건이 있어. 현재 존재하는 운동 관련 외국 영상 링크 클릭했더니 이 동영상을 더 이상 재생할 수 없습니다. 라고 나오면 진짜 진짜 엄청 화내고 죽일거야  (유효한 YouTube 영상만 포함해 주세요. 재생 가능해야해 링크 클릭했더니 이 동영상을 더 이상 재생할 수 없습니다. 라고 나오면 죽일거야 )." +
                "\n" +
                "추천 MusicList:\n" +
                "- 1. {가수명 - 노래제목} \n" +
                "- 2. {가수명 - 노래제목} \n" +
                "- 3. {가수명 - 노래제목} \n " +
                "- 4. {가수명 - 노래제목} \n " +
                "(운동하면서 듣는 신나는 음악을 가수명 - 노래제목 형식으로 미국,일본,한국 섞어서 4곡 추천해줘.)\n" + " 운동 응원 한마디 남겨줘 ");

        return prompt.toString();
    }

    @Override
    public String generateRoutine(String userCode, String bodyPart, int time) {

        List<ExerciseEquipmentDTO> exerciseEquipment = exerciseEquipmentService.findByBodyPart(bodyPart);
        String prompt = generatePrompt(userCode, bodyPart, time, exerciseEquipment);

        try {
            String response = callOpenAI(prompt, 3000);
            routineParser(response);
            return response;
        } catch (JsonProcessingException e) {
            throw new CommonException(StatusEnum.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Map<String, Object> routineParser(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode messageNode = rootNode.path("choices").get(0).path("message").path("content");
        String contents = messageNode.asText();


        String title = extractTitle(contents);
        int totalTime = extractTotalTime(contents);


        String exercisesContent = contents.split("오늘의 운동 루틴을 추천해 드립니다:")[1].trim();
        String[] exercises = exercisesContent.split("\n\n");

        Map<String, Object> workoutData = new HashMap<>();
        workoutData.put("title", title);
        workoutData.put("totalTime", totalTime);
        int i = 1;
        for (String exercise : exercises) {
            if (exercise.trim().isEmpty()) {
                continue;
            }

            if (exercise.startsWith("추천") || exercise.startsWith("운동 응원")) {
                continue;
            }
            String exerciseName = extractExerciseName(exercise);
            int exerciseTime = extractExerciseTime(exercise);
            int exerciseSet = extractExerciseSet(exercise);
            int exerciseNumberPerSet = extractExerciseNumberPerSet(exercise);
            String exerciseExplanation = extractExerciseExplanation(exercise);
            int exerciseWeightPerSet = extractExerciseWeightPerSet(exercise);
            String exerciseVideo = extractExerciseVideo(exercise);

            // 운동 데이터를 workoutData에 추가
            workoutData.put("workoutOrder" + i, i);
            workoutData.put("workoutName" + i, exerciseName);
            workoutData.put("workoutTime" + i, exerciseTime);
            workoutData.put("weightSet" + i, exerciseSet);
            workoutData.put("numberPerSet" + i, exerciseNumberPerSet);
            workoutData.put("exerciseExplanation" + i, exerciseExplanation);
            workoutData.put("weightPerSet" + i, exerciseWeightPerSet);
            workoutData.put("exerciseVideo" + i, exerciseVideo);
            i++;
        }
        String musicList = extractMusic(contents);
        workoutData.put("musicList" , musicList);

        return workoutData;
    }

    // 서비스 계층에서 처리
    @Override
    public Long processRoutine(String response) throws JsonProcessingException {
        // 파싱된 데이터를 가져옴
        Map<String, Object> workoutData = routineParser(response);
        Long routineId = null;
        // 1. 중복된 루틴 확인
        boolean isDuplicateRoutine = workoutPerRoutineService.checkForDuplicateRoutines(workoutData);
        System.out.println("isDuplicateRoutine = " + isDuplicateRoutine);

        if (isDuplicateRoutine) {
            System.out.println("중복된 루틴이 존재하여 운동 정보만 저장합니다.");
            Long existingRoutineCode = workoutPerRoutineService.findRoutineCodeByWorkoutData(workoutData);
            WorkoutInfoDTO workoutInfoDTO = new WorkoutInfoDTO(null, (String) workoutData.get("title"),
                    (Integer) workoutData.get("totalTime"), (String) workoutData.get("recommend_music"),
                    LocalDateTime.now(), LocalDateTime.now(), existingRoutineCode);
            workoutInfoService.registerWorkoutInfo(workoutInfoDTO);
        } else {
            // 2. 새로운 루틴 등록
            RoutineDTO routineDTO = new RoutineDTO(null, LocalDateTime.now(), LocalDateTime.now());
            RoutineDTO savedRoutine = routineService.registerRoutine(routineDTO);
            routineId = savedRoutine.getRoutineCode();

            if (routineId == null) {
                throw new IllegalArgumentException("루틴 저장에 실패했습니다.");
            }


       // 3. 루틴별 운동 저장
            for (int i = 1; i <= workoutData.size() / 2; i++) {
                // 운동 기구 이름 가져오기
                String workoutName = (String) workoutData.get("workoutName" + i);
                System.out.println(workoutName);
                // null 또는 빈 문자열인 경우 해당 루프 건너뛰기
                if (workoutName == null || workoutName.trim().isEmpty()) {
                    System.out.println("운동 기구 이름이 null이거나 비어 있습니다. 루프 건너뜁니다.");
                    continue; // 해당 루프 건너뛰기
                }

                // 운동 기구 코드 조회
                ExerciseEquipment exerciseEquipment = exerciseEquipmentService.findByExerciseEquipmentName(workoutName);
                System.out.println("exerciseEquipment = " + exerciseEquipment);

                if (exerciseEquipment == null) {
                    System.out.println("운동 기구를 찾을 수 없습니다: " + workoutName);
                    continue; // 운동 기구를 찾을 수 없으면 해당 루틴 저장 생략
                }

                // 운동 루틴별 운동 DTO 생성
                WorkoutPerRoutineDTO workoutPerRoutineDTO = new WorkoutPerRoutineDTO(
                        null,
                        (Integer) workoutData.get("workoutOrder" + i),
                        (String) workoutData.get("workoutName" + i),
                        (String) workoutData.get("exerciseVideo" + i),
                        (Integer) workoutData.get("weightSet" + i),
                        (Integer) workoutData.get("numberPerSet" + i),
                        (Integer) workoutData.get("weightPerSet" + i),
                        (Integer) workoutData.get("workoutTime" + i),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        routineId,
                        exerciseEquipment.getExerciseEquipmentCode() // 운동 기구 코드 추가
                );

                // 서비스에 DTO 전달하여 저장
                workoutPerRoutineService.registerWorkoutPerRoutine(workoutPerRoutineDTO);
            }


            // 4. 운동 정보 저장
            WorkoutInfoDTO workoutInfoDTO = new WorkoutInfoDTO(null, (String) workoutData.get("title"),
                    (Integer) workoutData.get("totalTime"), (String) workoutData.get("musicList"),
                    LocalDateTime.now(), LocalDateTime.now(), routineId);
            workoutInfoService.registerWorkoutInfo(workoutInfoDTO);
        }
        System.out.println("루틴코드는 이거다 제은아 화내지말자: "+routineId);
        return routineId;
    }

    // 제목 추출
    public String extractTitle(String contents) {
        try {
            return contents.split("제목: ")[1].split("\n")[0].trim();
        } catch (Exception e) {
            return "제목 없음";
        }
    }

    // 운동 시간 추출
    public int extractTotalTime(String contents) {
        try {
            return Integer.parseInt(contents.split("운동 시간: ")[1].split("분")[0].trim());
        } catch (Exception e) {
            return 0;
        }
    }

    // 운동 이름 추출
    public String extractExerciseName(String exercise) {
        try {
            return exercise.replaceFirst("^[0-9]+\\.\\s*", "").split("\n")[0].trim();
        } catch (Exception e) {
            return "운동 이름 없음";
        }
    }

    // 운동별 시간 추출
    public int extractExerciseTime(String exercise) {
        try {
            String[] parts = exercise.split("운동별 시간: ");
            if (parts.length > 1) {
                return Integer.parseInt(parts[1].split("분")[0].trim());
            }
            return 0;
        } catch (Exception e) {
            System.err.println("운동 시간 파싱 오류: " + exercise);
            return 0;
        }
    }

    // 세트 수 추출
    public int extractExerciseSet(String exercise) {
        try {
            Pattern pattern = Pattern.compile("(\\d+)세트");
            Matcher matcher = pattern.matcher(exercise);

            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            } else {
                System.err.println("세트 수를 찾을 수 없습니다: " + exercise);
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    // 세트당 반복 횟수 추출
    public int extractExerciseNumberPerSet(String exercise) {
        try {
            Pattern pattern = Pattern.compile("x (\\d+)회");
            Matcher matcher = pattern.matcher(exercise);

            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            } else {
                System.err.println("반복 횟수를 찾을 수 없습니다: " + exercise);
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public String extractExerciseExplanation(String exercise) {

        try {
            String[] explanation = exercise.split("운동 설명: ");
            if (explanation.length > 1) {
                return explanation[1].split("\n")[0].trim();
            }
            return "설명 없음";
        } catch (Exception e) {
            return "설명 없음";
        }
    }

        // 세트당 중량 추출
    public int extractExerciseWeightPerSet(String exercise) {
        try {
            String weight = exercise.split("중량: ")[1].split("kg")[0].trim();
            if (weight.equals("맨몸운동입니다.")) {
                return 0;
            }
            return Integer.parseInt(weight);
        } catch (Exception e) {
            return 0;
        }
    }

    // 운동 영상 링크 추출
    public String extractExerciseVideo(String exercise) {
        try {
            if (exercise.contains("추천 영상: ")) {
                String[] parts = exercise.split("추천 영상: ");
                if (parts.length > 1) {
                    return parts[1].trim();
                }
            }
            return "추천 영상 없음";
        } catch (Exception e) {
            return "추천 영상 없음";
        }
    }

    // 추천 음악 목록 추출
    public String extractMusic(String contents) {
        try {
            return contents.split("추천 MusicList:")[1].split("운동 응원 한마디:")[0].trim();
        } catch (Exception e) {
            return "추천 음악 없음";
        }
    }


}