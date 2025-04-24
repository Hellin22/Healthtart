package com.dev5ops.healthtart.rival.domain.entity;

import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "Rival")
@Table(name = "rival")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder
public class Rival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rival_match_code", nullable = false, unique = true)
    private Long rivalMatchCode;

    @ManyToOne
    @JoinColumn(name = "user_code", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "rival_user_code", nullable = false)
    private UserEntity rivalUser;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
