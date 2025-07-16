package de.jonashackt.springbootvuejs.domain;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联的 Post
    @Column(name = "post_id", nullable = false)
    private Long postId;

    // 评论者用户 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 避免与 SQL 关键字冲突，用 username 存展示名
    @Column(name = "username", nullable = false)
    private String username;

    // 回复目标评论 ID，可空
    @Column(name = "reply_to_id")
    private Long replyToId;

    // 评论内容
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    // 创建时间
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Comment() { }

    // —— getters & setters ——

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(Long replyToId) {
        this.replyToId = replyToId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    // createdAt 不需要 setter
}
