package de.jonashackt.springbootvuejs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import de.jonashackt.springbootvuejs.domain.Post;
import de.jonashackt.springbootvuejs.repository.PostRepository;  // ← 别忘了导入

@Service
public class PostService {

    private final PostRepository repo;

    public PostService(PostRepository repo) {
        this.repo = repo;
    }

    // 创建并保存一个帖子
    public Post create(Post p) {
        return repo.save(p);
    }

    // 新增：获取所有帖子
    public List<Post> findAll() {
        return repo.findAll();
    }

    // 根据关键字搜索帖子
    public List<Post> search(String q) {
        return repo.findByTitleContainingOrContentContaining(q, q);
    }
}
