package com.dev5ops.healthtart.gym.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@ToString
public class GymDTO {

    @JsonProperty("gym_code")
    private Long gymCode;

    @JsonProperty("gym_name")
    private String gymName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("business_number")
    private String businessNumber;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
