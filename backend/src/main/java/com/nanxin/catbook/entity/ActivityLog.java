package com.nanxin.catbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "activity_log")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long catId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType action;

    @Column(length = 500)
    private String extraInfo;

    private LocalDateTime createdAt;

    public enum ActionType { VIEW, SEARCH, COMMENT, LIKE, RATING, FOLLOW }
}