package de.jonashackt.springbootvuejs.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.jonashackt.springbootvuejs.domain.Comment;
import de.jonashackt.springbootvuejs.dto.CommentDto;
import de.jonashackt.springbootvuejs.dto.CreateCommentDto;
import de.jonashackt.springbootvuejs.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepo;

    public CommentService(CommentRepository commentRepo) {
        this.commentRepo = commentRepo;
    }

    public List<CommentDto> findByPostId(Long postId) {
        return commentRepo.findByPostId(postId)
                          .stream()
                          .map(this::toDto)
                          .collect(Collectors.toList());
    }

    public CommentDto create(Long postId, CreateCommentDto dto) {
        Comment c = new Comment();
        c.setPostId(postId);
        c.setUsername("匿名");               // 改为 setUsername
        c.setText(dto.getText());
        Comment saved = commentRepo.save(c);
        return toDto(saved);
    }

    public CommentDto update(Long postId, Long commentId, CreateCommentDto dto) {
        Comment c = commentRepo.findById(commentId)
                               .filter(co -> co.getPostId().equals(postId))
                               .orElseThrow(() -> new RuntimeException("评论不存在"));
        c.setText(dto.getText());
        Comment updated = commentRepo.save(c);
        return toDto(updated);
    }

    public void delete(Long postId, Long commentId) {
        Comment c = commentRepo.findById(commentId)
                               .filter(co -> co.getPostId().equals(postId))
                               .orElseThrow(() -> new RuntimeException("评论不存在"));
        commentRepo.delete(c);
    }

    public long countByPostId(Long postId) {
        return commentRepo.countByPostId(postId);
    }

    private CommentDto toDto(Comment c) {
        CommentDto dto = new CommentDto();
        dto.setId(c.getId());
        dto.setUsername(c.getUsername());   // 改为 getUsername
        dto.setText(c.getText());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}
