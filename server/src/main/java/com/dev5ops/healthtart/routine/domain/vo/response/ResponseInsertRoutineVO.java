package com.dev5ops.healthtart.routine.domain.vo.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResponseInsertRoutineVO {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
