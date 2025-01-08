package com.example.travelshooting.comment.dto;

import lombok.*;

@RequiredArgsConstructor
@Getter
public class CommentReqDto {
    private String comment;

    public CommentReqDto(String comment) {
        this.comment = comment;
    }

}