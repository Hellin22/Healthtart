package com.dev5ops.healthtart.recommended_workout_history.service;

import com.dev5ops.healthtart.recommended_workout_history.domain.dto.RecommendedWorkoutHistoryDTO;

import java.util.List;
import java.util.Map;

public interface RecommendedWorkoutHistoryService {

    List<Map.Entry<Long, Double>> findByRatingOrder();

    RecommendedWorkoutHistoryDTO registerRating
            (RecommendedWorkoutHistoryDTO recommendedWorkoutHistoryDTO);
}