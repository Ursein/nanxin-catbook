package com.nanxin.catbook.service;

import com.nanxin.catbook.entity.Comment;
import com.nanxin.catbook.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Comment create(Long catId, Long userId, String content) {
        Comment comment = new Comment();
        comment.setCatId(catId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> listByCat(Long catId) {
        return commentRepository.findByCatIdOrderByCreatedAtDesc(catId);
    }

    @Transactional
    public void delete(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}