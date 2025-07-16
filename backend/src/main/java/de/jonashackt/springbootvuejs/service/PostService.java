package de.jonashackt.springbootvuejs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.jonashackt.springbootvuejs.domain.Post;
import de.jonashackt.springbootvuejs.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository repo;

    public PostService(PostRepository repo) {
        this.repo = repo;
    }

    public Post create(Post p) {
        return repo.save(p);
    }

    public List<Post> findAll() {
        return repo.findAll();
    }

    public List<Post> search(String q) {
        return repo.findByTitleContainingOrContentContaining(q, q);
    }

    /** 新增：根据 ID 查询并累加浏览量 */
    public Optional<Post> findByIdAndIncrementViews(Long id) {
        Optional<Post> o = repo.findById(id);
        o.ifPresent(p -> {
            p.setViews(p.getViews() + 1);
            repo.save(p);
        });
        return o;
    }
}
