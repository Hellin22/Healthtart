package com.dev5ops.healthtart.inbody.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequestDTO {
    private String gender;
    private Range heightRange;
    private Range weightRange;
    private Range muscleWeightRange;
    private Range fatWeightRange;
    private Range bmiRange;
    private Range fatPercentageRange;
    private Range basalMetabolicRateRange;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Range {
        private double min;
        private double max;
    }
}
