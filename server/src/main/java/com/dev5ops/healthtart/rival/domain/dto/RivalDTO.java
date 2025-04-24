package com.dev5ops.healthtart.rival.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RivalDTO {

    private Long rivalMatchCode;

    private String userCode;

    private String rivalUserCode;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
