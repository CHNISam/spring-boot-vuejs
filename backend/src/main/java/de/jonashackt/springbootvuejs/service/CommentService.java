package de.jonashackt.springbootvuejs.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import de.jonashackt.springbootvuejs.domain.Comment;
import de.jonashackt.springbootvuejs.dto.CommentDto;
import de.jonashackt.springbootvuejs.dto.CreateCommentDto;
import de.jonashackt.springbootvuejs.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepo;
    private final UserService userService;  // 用于获取当前登录用户 ID 和用户名

    public CommentService(CommentRepository commentRepo,
                          UserService userService) {
        this.commentRepo = commentRepo;
        this.userService = userService;
    }

    /** 列出某篇帖子的所有评论（包含回复关系） */
    public List<CommentDto> findByPostId(Long postId) {
        return commentRepo.findByPostId(postId).stream()
                          .map(this::toDto)
                          .collect(Collectors.toList());
    }

    /** 发布新评论或对某评论进行回复 */
    public CommentDto create(Long postId, CreateCommentDto dto) {
        Long uid = userService.currentUserId();
        Comment c = new Comment();
        c.setPostId(postId);
        c.setUserId(uid);
        c.setUsername(userService.findUsername(uid));
        c.setReplyToId(dto.getReplyToId());
        c.setText(dto.getText());
        Comment saved = commentRepo.save(c);
        return toDto(saved);
    }

    /** 编辑评论，只有评论作者本人可执行 */
    public CommentDto update(Long postId, Long commentId, CreateCommentDto dto) {
        Long uid = userService.currentUserId();
        Comment c = commentRepo.findById(commentId)
                       .filter(co -> co.getPostId().equals(postId))
                       .orElseThrow(() -> new RuntimeException("评论不存在"));
        if (!c.getUserId().equals(uid)) {
            throw new AccessDeniedException("只能编辑自己的评论");
        }
        c.setText(dto.getText());
        c.setReplyToId(dto.getReplyToId());
        Comment updated = commentRepo.save(c);
        return toDto(updated);
    }

    /** 删除评论，只有评论作者本人可执行 */
    public void delete(Long postId, Long commentId) {
        Long uid = userService.currentUserId();
        Comment c = commentRepo.findById(commentId)
                       .filter(co -> co.getPostId().equals(postId))
                       .orElseThrow(() -> new RuntimeException("评论不存在"));
        if (!c.getUserId().equals(uid)) {
            throw new AccessDeniedException("只能删除自己的评论");
        }
        commentRepo.delete(c);
    }

    /** 获取某篇帖子下的评论总数 */
    public long countByPostId(Long postId) {
        return commentRepo.countByPostId(postId);
    }

    /** 转换为 DTO 并填充展示字段 */
    private CommentDto toDto(Comment c) {
        CommentDto dto = new CommentDto();
        dto.setId(c.getId());
        dto.setUserId(c.getUserId());
        dto.setUsername(c.getUsername());
        dto.setReplyToId(c.getReplyToId());
        dto.setText(c.getText());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}
