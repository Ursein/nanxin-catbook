package com.nanxin.catbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cat_rating", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "catId"})
})
public class CatRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long catId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer r1; // 猫德

    @Column(nullable = false)
    private Integer r2; // 颜值

    @Column(nullable = false)
    private Integer r3; // 社交

    @Column(nullable = false)
    private Integer r4; // 干饭

    @Column(nullable = false)
    private Integer r5; // 活力

    private LocalDateTime createdAt;
}