package de.jonashackt.springbootvuejs.dto;

/**
 * 前端发起创建/回复评论时的请求体
 */
public class CreateCommentDto {
    private String text;
    // 新增：如果是回复某条评论，就在这里传 replyToId
    private Long replyToId;

    public CreateCommentDto() { }

    public CreateCommentDto(String text, Long replyToId) {
        this.text = text;
        this.replyToId = replyToId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(Long replyToId) {
        this.replyToId = replyToId;
    }
}
