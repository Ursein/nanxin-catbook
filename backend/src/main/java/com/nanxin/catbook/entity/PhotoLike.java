package com.nanxin.catbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "photo_like", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"photoId", "userId"})
})
public class PhotoLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long photoId;

    @Column(nullable = false)
    private Long userId;

    private LocalDateTime createdAt;
}