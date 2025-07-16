package de.jonashackt.springbootvuejs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.jonashackt.springbootvuejs.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 根据标题或内容模糊搜索
    List<Post> findByTitleContainingOrContentContaining(String title, String content);
}
