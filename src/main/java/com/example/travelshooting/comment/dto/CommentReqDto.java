package com.example.travelshooting.comment.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@RequiredArgsConstructor
@Getter
public class CommentReqDto {
    @NotBlank(message = "댓글을 입력해주세요.")
    private String comment;

    public CommentReqDto(String comment) {
        this.comment = comment;
    }
}