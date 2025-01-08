package com.example.travelshooting.comment.dto;

import com.example.travelshooting.comment.Comment;
import com.example.travelshooting.common.BaseDtoDataType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class CommentResDto implements BaseDtoDataType {

    private Long id;

    @NotBlank(message = "댓글을 입력해주세요.")
    private String comment;

    public CommentResDto(Comment comment) {
        this.id = comment.getId();
        this.comment = comment.getContent();
    }

    public static CommentResDto toDto(Comment comment) {
        return new CommentResDto(comment);
    }
}
