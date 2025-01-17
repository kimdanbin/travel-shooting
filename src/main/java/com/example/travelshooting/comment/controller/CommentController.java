package com.example.travelshooting.comment.controller;

import com.example.travelshooting.comment.dto.CommentReqDto;
import com.example.travelshooting.comment.dto.CommentResDto;
import com.example.travelshooting.comment.service.CommentService;
import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.common.CommonResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posters/{posterId}/comments")
public class CommentController {

    private final CommentService commentService;

    //댓글 생성
    @PostMapping
    public ResponseEntity<CommonResDto<CommentResDto>> createComment(
            @PathVariable Long posterId,
            @Valid @RequestBody CommentReqDto commentReqDto
    ) {

        CommentResDto commentResDto  = commentService.createComment(posterId, commentReqDto.getContent());

        return new ResponseEntity<>(new CommonResDto<>("댓글 생성 완료", commentResDto), HttpStatus.CREATED);
    }

    // 댓글 전체 조회
    @GetMapping
    public ResponseEntity<CommonListResDto<CommentResDto>> findComments(@PathVariable Long posterId) {

        List<CommentResDto> comments = commentService.findComments(posterId);

        return new ResponseEntity<>(new CommonListResDto<>("댓글 전체 조회 완료", comments), HttpStatus.OK);
    }

    //댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommonResDto<CommentResDto>> updateComment(
            @PathVariable Long posterId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentReqDto commentReqDto
    ) {

        if (!commentService.isPosterIdValid(posterId, commentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 포스터에 없는 댓글입니다.");
        }

        CommentResDto commentResDto = commentService.updateComment(commentId, commentReqDto.getContent());

        return new ResponseEntity<>(new CommonResDto<>("댓글 수정 완료", commentResDto), HttpStatus.CREATED);
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long posterId,
            @PathVariable Long commentId
    ) {

        if (!commentService.isPosterIdValid(posterId, commentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 포스터에 없는 댓글입니다.");
        }

        commentService.deleteComment(commentId);

        return new ResponseEntity<>("댓글 삭제 완료", HttpStatus.NO_CONTENT);
    }

}
