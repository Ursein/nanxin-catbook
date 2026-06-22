package com.nanxin.catbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "photo")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long catId;

    @Column(nullable = false)
    private Long uploaderId;

    @Column(nullable = false, length = 255)
    private String filePath;

    @Column(length = 200)
    private String description;

    private Integer sortOrder = 0;

    @Enumerated(EnumType.STRING)
    private PhotoStatus status = PhotoStatus.PENDING;

    private Long reviewerId;

    @Column(length = 200)
    private String rejectReason;

    private Integer likeCount = 0;

    private LocalDateTime createdAt;

    public enum PhotoStatus { PENDING, APPROVED, REJECTED }
}