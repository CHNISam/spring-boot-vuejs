package de.jonashackt.springbootvuejs.dto;

public class CreateCommentDto {
    private String text;

    public CreateCommentDto() { }

    public CreateCommentDto(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
