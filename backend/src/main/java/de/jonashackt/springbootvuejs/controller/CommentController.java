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

    /** 获取评论列表（含 replyToId） */
    @GetMapping
    public List<CommentDto> list(@PathVariable Long postId) {
        return commentService.findByPostId(postId);
    }

    /** 获取评论总数 */
    @GetMapping("/count")
    public ResponseEntity<Long> count(@PathVariable Long postId) {
        long cnt = commentService.countByPostId(postId);
        return ResponseEntity.ok(cnt);
    }

    /** 发布或回复评论 */
    @PostMapping
    public ResponseEntity<CommentDto> create(
            @PathVariable Long postId,
            @RequestBody CreateCommentDto dto
    ) {
        CommentDto created = commentService.create(postId, dto);
        return ResponseEntity.ok(created);
    }

    /** 编辑自己的评论 */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CreateCommentDto dto
    ) {
        CommentDto updated = commentService.update(postId, commentId, dto);
        return ResponseEntity.ok(updated);
    }

    /** 删除自己的评论 */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.delete(postId, commentId);
        return ResponseEntity.noContent().build();
    }
}
