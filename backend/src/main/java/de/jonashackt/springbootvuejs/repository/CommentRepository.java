package de.jonashackt.springbootvuejs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.jonashackt.springbootvuejs.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    long countByPostId(Long postId);
}
