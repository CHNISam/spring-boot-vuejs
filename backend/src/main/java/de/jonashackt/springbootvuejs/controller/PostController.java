package de.jonashackt.springbootvuejs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.jonashackt.springbootvuejs.domain.Post;
import de.jonashackt.springbootvuejs.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    /** 发布新帖子 */
    @PostMapping
    public ResponseEntity<Post> create(@RequestBody Post post) {
        Post saved = service.create(post);
        return ResponseEntity
                .created(null)   // 可指定 Location，如 URI.create("/api/posts/" + saved.getId())
                .body(saved);
    }

    /** 搜索帖子 */
    @GetMapping("/search")
    public ResponseEntity<List<Post>> search(@RequestParam("q") String q) {
        return ResponseEntity.ok(service.search(q));
    }

    /** 列出所有帖子 */
    @GetMapping
    public ResponseEntity<List<Post>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /** 根据 ID 查询单个帖子并累加浏览量 */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getById(@PathVariable Long id) {
        return service.findByIdAndIncrementViews(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
