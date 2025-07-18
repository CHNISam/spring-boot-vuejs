package de.jonashackt.springbootvuejs.domain;

import java.time.LocalDateTime;

public class AIResponse {

    private Long id;
    private Long userId;          // 关联用户的ID
    private String responseText;  // AI回复的文本
    private LocalDateTime timestamp;  // 回复时间
    private String emotion;       // AI根据情感分析生成的情感（如开心、安慰等）

    // Getters and Setters
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

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }
}
