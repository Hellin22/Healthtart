package com.dev5ops.healthtart.inbody.aggregate;

import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "inbody")
@Table(name = "inbody")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Inbody {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inbodyCode;

    @Column(name = "inbody_score")
    private Integer inbodyScore;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Column(name = "height", nullable = false)
    private double height;

    @Column(name = "muscle_weight", nullable = false)
    private double muscleWeight;

    @Column(name = "fat_weight", nullable = false)
    private double fatWeight;

    @Column(name = "bmi", nullable = false)
    private double bmi;

    @Column(name = "fat_percentage", nullable = false)
    private double fatPercentage;

    @Column(name = "day_of_inbody", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dayOfInbody;

    @Column(name = "basal_metabolic_rate", nullable = false)
    private Integer basalMetabolicRate;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_code", nullable = false)
    private UserEntity user;
}
