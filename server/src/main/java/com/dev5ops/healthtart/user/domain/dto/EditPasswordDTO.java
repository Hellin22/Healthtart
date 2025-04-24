package com.dev5ops.healthtart.user.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditPasswordDTO {
    private String currentPassword;
    private String newPassword;
}
