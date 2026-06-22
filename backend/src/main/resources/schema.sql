-- 南信猫友记 数据库建表脚本
-- 数据库：nanxin_maopu_v2

CREATE DATABASE IF NOT EXISTS nanxin_maopu_v2 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE nanxin_maopu_v2;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `nickname` VARCHAR(50),
    `email` VARCHAR(100),
    `avatar` VARCHAR(255),
    `role` ENUM('USER','VERIFIED','ADMIN') NOT NULL DEFAULT 'USER',
    `status` TINYINT DEFAULT 1,
    `last_login` DATETIME,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 猫咪表
CREATE TABLE IF NOT EXISTS `cat` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `nickname` VARCHAR(50),
    `gender` ENUM('MALE','FEMALE','UNKNOWN'),
    `birth_year` INT,
    `breed` VARCHAR(50),
    `colour` VARCHAR(50),
    `colour_tags` VARCHAR(100),
    `location_area` VARCHAR(50),
    `location_detail` VARCHAR(200),
    `personality_tags` VARCHAR(200),
    `personality_desc` TEXT,
    `health_status` VARCHAR(100),
    `sterilized` BOOLEAN DEFAULT FALSE,
    `adopt_status` ENUM('UNCLAIMED','ADOPTED','SEEKING') DEFAULT 'UNCLAIMED',
    `status` ENUM('ACTIVE','SEEKING_ADOPT','MISSING','DECEASED') DEFAULT 'ACTIVE',
    `weight` DECIMAL(4,1),
    `cover_photo_id` BIGINT,
    `father_id` BIGINT,
    `mother_id` BIGINT,
    `friend_ids` VARCHAR(255),
    `first_sighting_time` DATE,
    `first_sighting_location` VARCHAR(200),
    `missing_time` DATETIME,
    `death_reason` VARCHAR(200),
    `delivery_time` DATE,
    `notes` TEXT,
    `feature_vector` TEXT COMMENT '特征向量缓存（JSON格式）',
    `like_count` INT DEFAULT 0 COMMENT '点赞数（冗余）',
    `follow_count` INT DEFAULT 0 COMMENT '关注数（冗余）',
    `rating_count` INT DEFAULT 0 COMMENT '评分人数（冗余）',
    `avg_rating` DECIMAL(3,2) DEFAULT 0.00 COMMENT '平均评分（冗余）',
    `creator_id` BIGINT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`creator_id`) REFERENCES `user`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`father_id`) REFERENCES `cat`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`mother_id`) REFERENCES `cat`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 照片表
CREATE TABLE IF NOT EXISTS `photo` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `cat_id` BIGINT NOT NULL,
    `uploader_id` BIGINT NOT NULL,
    `file_path` VARCHAR(255) NOT NULL,
    `description` VARCHAR(200),
    `sort_order` INT DEFAULT 0,
    `status` ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
    `reviewer_id` BIGINT,
    `reject_reason` VARCHAR(200),
    `like_count` INT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`cat_id`) REFERENCES `cat`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`uploader_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`reviewer_id`) REFERENCES `user`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `cat_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`cat_id`) REFERENCES `cat`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 行为日志表
CREATE TABLE IF NOT EXISTS `activity_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT,
    `cat_id` BIGINT,
    `action` ENUM('VIEW','SEARCH','COMMENT','LIKE','RATING','FOLLOW') NOT NULL,
    `extra_info` VARCHAR(500),
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`cat_id`) REFERENCES `cat`(`id`) ON DELETE SET NULL,
    INDEX `idx_action_cat` (`action`, `cat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 照片点赞表
CREATE TABLE IF NOT EXISTS `photo_like` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `photo_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_photo_user` (`photo_id`, `user_id`),
    FOREIGN KEY (`photo_id`) REFERENCES `photo`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 猫咪关注表
CREATE TABLE IF NOT EXISTS `cat_follow` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `cat_id` BIGINT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_cat` (`user_id`, `cat_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`cat_id`) REFERENCES `cat`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 猫咪评分表
CREATE TABLE IF NOT EXISTS `cat_rating` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `cat_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `rating` TINYINT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_cat_rating` (`user_id`, `cat_id`),
    FOREIGN KEY (`cat_id`) REFERENCES `cat`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    CHECK (`rating` >= 1 AND `rating` <= 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;