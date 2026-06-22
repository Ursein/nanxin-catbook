package com.nanxin.catbook.controller;

import com.nanxin.catbook.config.CurrentUser;
import com.nanxin.catbook.dto.*;
import com.nanxin.catbook.entity.Comment;
import com.nanxin.catbook.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/cats/{catId}/comments")
    public ResponseEntity<ApiResponse<List<CommentItem>>> list(@PathVariable Long catId) {
        List<Comment> comments = commentService.listByCat(catId);
        List<CommentItem> items = comments.stream().map(c -> {
            CommentItem ci = new CommentItem();
            ci.setId(c.getId());
            ci.setContent(c.getContent());
            ci.setCreatedAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
            return ci;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @PostMapping("/cats/{catId}/comments")
    public ResponseEntity<ApiResponse<CommentItem>> create(
            @PathVariable Long catId, @Valid @RequestBody CommentRequest req,
            HttpServletRequest request) {
        Long userId = CurrentUser.getId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        Comment comment = commentService.create(catId, userId, req.getContent());
        CommentItem ci = new CommentItem();
        ci.setId(comment.getId());
        ci.setContent(comment.getContent());
        ci.setCreatedAt(comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null);
        return ResponseEntity.ok(ApiResponse.success(ci));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}