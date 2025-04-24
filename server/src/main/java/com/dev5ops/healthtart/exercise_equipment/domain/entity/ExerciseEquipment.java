package com.dev5ops.healthtart.exercise_equipment.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "exercise_equipment")
@Table(name = "exercise_equipment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ExerciseEquipment {

    @Id
    @Column(name = "exercise_equipment_code", nullable = false, unique = true)
    private Long exerciseEquipmentCode;

    @Column(name = "exercise_equipment_name", nullable = false)
    private String exerciseEquipmentName;

    @Column(name = "body_part", nullable = false)
    private String bodyPart;

    @Column(name = "exercise_description", nullable = false)
    private String exerciseDescription;

    @Column(name = "exercise_image")
    private String exerciseImage;

    @Column(name = "recommended_video")
    private String recommendedVideo;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
