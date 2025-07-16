package de.jonashackt.springbootvuejs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
     * 请求：POST /api/posts
     * Body: { "title": "...", "content": "..." }
     * 返回：刚创建好的 Post 对象（带 id 和 createdAt）
     */
    @PostMapping
    public ResponseEntity<Post> create(@RequestBody Post post) {
        Post saved = service.create(post);
        // 返回 201 Created + body
        return ResponseEntity
                .created(null)      // 你可以填 Location URI：URI.create("/api/posts/" + saved.getId())
                .body(saved);
    }

    /**
     * 搜索帖子
     * 请求：GET /api/posts/search?q=关键字
     * 返回：匹配的帖子列表（每个都带 createdAt）
     */
    @GetMapping("/search")
    public ResponseEntity<List<Post>> search(@RequestParam("q") String q) {
        List<Post> results = service.search(q);
        return ResponseEntity.ok(results);
    }

    /**
     * 列出所有帖子
     * 请求：GET /api/posts
     * 返回：所有帖子列表（每个都带 createdAt）
     */
    @GetMapping
    public ResponseEntity<List<Post>> listAll() {
        List<Post> all = service.findAll();
        return ResponseEntity.ok(all);
    }
}
