package de.jonashackt.springbootvuejs.dto;

import java.time.Instant;

/**
 * 返回给前端的评论数据
 */
public class CommentDto {
    private Long id;
    private Long userId;      // 评论者 ID，用于前端权限判断
    private String username;  // 评论者展示名
    private Long replyToId;   // 如果非 null，表示这是对某条评论的回复
    private String text;
    private Instant createdAt;

    public CommentDto() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
