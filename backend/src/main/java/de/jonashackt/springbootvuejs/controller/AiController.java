package de.jonashackt.springbootvuejs.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.jonashackt.springbootvuejs.service.BigmodelService;

@RestController
@RequestMapping("/api")
public class AiController {

    private final BigmodelService bigmodel;
    private final Logger log = LoggerFactory.getLogger(AiController.class);

    public AiController(BigmodelService bigmodel) {
        this.bigmodel = bigmodel;
    }

    /**
     * 接收前端传来的 { q, posts: [{ title, excerpt }] }，
     * 返回 { summary }。
     */
    @PostMapping("/ai-summary")
    public ResponseEntity<Map<String, String>> getSummary(@RequestBody AiRequest req) {
        try {
            // 转换格式
            List<Map<String, String>> briefs = new ArrayList<>();
            if (req.posts != null) {
                for (PostBrief pb : req.posts) {
                    Map<String, String> m = new HashMap<>();
                    m.put("title", pb.title);
                    m.put("excerpt", pb.excerpt);
                    briefs.add(m);
                }
            }

            String summary = bigmodel.summarize(req.q, briefs);
            return ResponseEntity.ok(Collections.singletonMap("summary", summary));
        } catch (Exception ex) {
            log.error("AI 摘要失败", ex);
            return ResponseEntity
                .status(503)
                .body(Collections.singletonMap("summary", "AI 服务暂不可用"));
        }
    }

    // ======== DTOs ========
    public static class AiRequest {
        public String q;
        public List<PostBrief> posts;
    }

    public static class PostBrief {
        public String title;
        public String excerpt;
    }
}
