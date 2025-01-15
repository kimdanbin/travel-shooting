package com.example.travelshooting.comment.dto;

import com.example.travelshooting.comment.entity.Comment;
import com.example.travelshooting.common.BaseDtoDataType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentResDto implements BaseDtoDataType {

    private Long id;

    @NotBlank(message = "댓글을 입력해주세요.")
    private String comment;

    public CommentResDto(Comment comment) {
        this.id = comment.getId();
        this.comment = comment.getContent();
    }

}
