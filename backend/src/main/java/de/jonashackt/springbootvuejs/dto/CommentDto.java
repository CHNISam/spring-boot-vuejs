package de.jonashackt.springbootvuejs.dto;

import java.time.Instant;

public class CommentDto {
    private Long id;
    private String username;      // 同步改名
    private String text;
    private Instant createdAt;

    public CommentDto() { }

    public CommentDto(Long id, String username, String text, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
