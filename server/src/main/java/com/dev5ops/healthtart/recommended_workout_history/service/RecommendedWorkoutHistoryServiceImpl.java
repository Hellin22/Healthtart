package com.dev5ops.healthtart.recommended_workout_history.service;

import com.dev5ops.healthtart.recommended_workout_history.domain.dto.RecommendedWorkoutHistoryDTO;
import com.dev5ops.healthtart.recommended_workout_history.domain.entity.RecommendedWorkoutHistory;
import com.dev5ops.healthtart.recommended_workout_history.repository.RecommendedWorkoutHistoryRepository;

import com.dev5ops.healthtart.workoutinfo.domain.vo.response.ResponseFindWorkoutInfoVO;
import com.dev5ops.healthtart.workoutinfo.service.WorkoutInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service("recommendedWorkoutHistoryService")
public class RecommendedWorkoutHistoryServiceImpl implements RecommendedWorkoutHistoryService {
    private final RecommendedWorkoutHistoryRepository recommendedWorkoutHistoryRepository;
    private final WorkoutInfoService workoutInfoService;

    private final ModelMapper modelMapper;

    // 1. 운동정보를 타고 ~ 운동 루틴으로 가서 ~ 운동루틴 코드별 운동추천내역 조회
    // 2. 유저랑 운동정보의 운동 루틴 번호를 뽑아 -> 운동루틴의 번호를 뽑기 -> 해당 운동 루틴 번호조회
    // 3. 만족도만 다 더해서 평균내기 (1부터5)

    public List<RecommendedWorkoutHistory> getAllRecommendedWorkoutHistories() {
        return recommendedWorkoutHistoryRepository.findAll();
    }

    public List<RecommendedWorkoutHistoryDTO> convertToDTO(List<RecommendedWorkoutHistory> historyList) {
        return historyList.stream()
                .map(history -> modelMapper.map(history, RecommendedWorkoutHistoryDTO.class))
                .collect(Collectors.toList());
    }

    public List<Map.Entry<Long, Double>> sortAverageRatings(Map<Long, Double> averageRatingsByRoutineCode) {
        return averageRatingsByRoutineCode.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .collect(Collectors.toList());
    }

    public Map<Long, Double> calculateAverageRatingsByRoutineCode(
            List<ResponseFindWorkoutInfoVO> workoutInfoList,
            List<RecommendedWorkoutHistoryDTO> dtoList) {
        try {
            // 루틴 코드별 운동 정보 코드 그룹화
            Map<Long, List<Long>> groupedWorkoutInfoCodes = workoutInfoService.groupingWorkoutInfoCodesByRoutineCode(workoutInfoList);
            if (groupedWorkoutInfoCodes.isEmpty()) {
                log.warn("No workout info codes grouped by routine code");
                return new HashMap<>();
            }

            // 루틴 코드별 평균 만족도 계산
            Map<Long, Double> averageRatings = new HashMap<>();

            for (Map.Entry<Long, List<Long>> entry : groupedWorkoutInfoCodes.entrySet()) {
                Long routineCode = entry.getKey();
                List<Long> workoutInfoCodes = entry.getValue();

                Double averageRating = dtoList.stream()
                        .filter(dto -> workoutInfoCodes.contains(dto.getWorkoutInfoCode()))
                        .collect(Collectors.averagingDouble(RecommendedWorkoutHistoryDTO::getRoutineRatings));

                if (!averageRating.isNaN()) {  // NaN 체크 추가
                    averageRatings.put(routineCode, averageRating);
                }
            }

            return averageRatings;
        } catch (Exception e) {
            log.error("Error calculating average ratings: ", e);
            throw new RuntimeException("평균 만족도 계산 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<Map.Entry<Long, Double>> findByRatingOrder() {
        try {
            // 1. 추천 내역 조회
            List<RecommendedWorkoutHistory> recommendedWorkoutHistoryList = getAllRecommendedWorkoutHistories();
            if (recommendedWorkoutHistoryList.isEmpty()) {
                log.warn("No recommended workout histories found");
                return new ArrayList<>();
            }

            // 2. DTO로 변환
            List<RecommendedWorkoutHistoryDTO> recommendedWorkoutHistoryDTOList = convertToDTO(recommendedWorkoutHistoryList);
            if (recommendedWorkoutHistoryDTOList.isEmpty()) {
                log.warn("Failed to convert workout histories to DTOs");
                return new ArrayList<>();
            }

            // 3. 운동 정보 조회
            List<ResponseFindWorkoutInfoVO> workoutInfoList = workoutInfoService.getWorkoutInfos();
            if (workoutInfoList.isEmpty()) {
                log.warn("No workout info found");
                return new ArrayList<>();
            }

            // 4. 운동 루틴 번호별로 만족도 평균 계산
            Map<Long, Double> averageRatingsByRoutineCode = calculateAverageRatingsByRoutineCode(workoutInfoList, recommendedWorkoutHistoryDTOList);

            // 5. 결과를 내림차순으로 정렬
            return sortAverageRatings(averageRatingsByRoutineCode);
        } catch (Exception e) {
            log.error("Error in findByRatingOrder: ", e);
            throw new RuntimeException("운동 루틴별 만족도 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public RecommendedWorkoutHistoryDTO registerRating(RecommendedWorkoutHistoryDTO recommendedWorkoutHistoryDTO) {
        RecommendedWorkoutHistory recommendedWorkoutHistory = RecommendedWorkoutHistory.builder()
                .routineRatings(recommendedWorkoutHistoryDTO.getRoutineRatings())
                .workoutInfoCode(recommendedWorkoutHistoryDTO.getWorkoutInfoCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        recommendedWorkoutHistory = recommendedWorkoutHistoryRepository.save(recommendedWorkoutHistory);

        return RecommendedWorkoutHistoryDTO.builder()
                .historyCode(recommendedWorkoutHistory.getHistoryCode())
                .routineRatings(recommendedWorkoutHistory.getRoutineRatings())
                .workoutInfoCode(recommendedWorkoutHistory.getWorkoutInfoCode())
                .createdAt(recommendedWorkoutHistory.getCreatedAt())
                .updatedAt(recommendedWorkoutHistory.getUpdatedAt())
                .build();
    }
}



