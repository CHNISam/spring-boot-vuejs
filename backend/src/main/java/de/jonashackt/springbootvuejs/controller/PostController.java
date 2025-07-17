package de.jonashackt.springbootvuejs.controller;

import java.net.URI;
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

    /**
     * 发布新帖子
     * POST /api/posts
     * 返回 201 Created，并在 Location 头中指向新资源的 URL
     */
    @PostMapping
    public ResponseEntity<Post> create(@RequestBody Post post) {
        Post saved = service.create(post);
        URI location = URI.create("/api/posts/" + saved.getId());
        return ResponseEntity
                .created(location)
                .body(saved);
    }

    /**
     * 搜索帖子
     * GET /api/posts/search?q=关键字
     * 返回匹配标题或内容包含关键字的帖子列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<Post>> search(@RequestParam("q") String q) {
        List<Post> results = service.search(q);
        return ResponseEntity.ok(results);
    }

    /**
     * 列出所有帖子
     * GET /api/posts
     */
    @GetMapping
    public ResponseEntity<List<Post>> listAll() {
        List<Post> all = service.findAll();
        return ResponseEntity.ok(all);
    }

    /**
     * 根据 ID 查询单个帖子并累加浏览量
     * GET /api/posts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getById(@PathVariable Long id) {
        return service.findByIdAndIncrementViews(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
