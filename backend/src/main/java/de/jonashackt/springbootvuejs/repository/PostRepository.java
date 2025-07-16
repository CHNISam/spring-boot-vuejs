package de.jonashackt.springbootvuejs.repository;

import de.jonashackt.springbootvuejs.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 根据标题或内容模糊搜索
    List<Post> findByTitleContainingOrContentContaining(String title, String content);
}
