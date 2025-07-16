// src/main/java/de/jonashackt/springbootvuejs/controller/CommentController.java
package de.jonashackt.springbootvuejs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.jonashackt.springbootvuejs.dto.CommentDto;
import de.jonashackt.springbootvuejs.dto.CreateCommentDto;
import de.jonashackt.springbootvuejs.service.CommentService;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentDto> list(@PathVariable Long postId) {
        return commentService.findByPostId(postId);
    }

    @PostMapping
    public ResponseEntity<CommentDto> create(
            @PathVariable Long postId,
            @RequestBody CreateCommentDto dto
    ) {
        CommentDto created = commentService.create(postId, dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CreateCommentDto dto
    ) {
        CommentDto updated = commentService.update(postId, commentId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.delete(postId, commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countComments(@PathVariable Long postId) {
        long cnt = commentService.countByPostId(postId);
        return ResponseEntity.ok(cnt);
    }
}
