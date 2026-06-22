package com.nanxin.catbook.repository;

import com.nanxin.catbook.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByCatIdOrderByCreatedAtDesc(Long catId);
    void deleteByCatId(Long catId);
}