package com.dev5ops.healthtart.routine.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EditRoutineVO {
    private LocalDateTime updatedAt;
}
