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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posters/{posterId}/comments")
public class CommentController {

    private final CommentService commentService;

    //댓글 생성
    @PostMapping
    public ResponseEntity<CommonResDto<CommentResDto>> createComment(@PathVariable Long posterId,
                                                      @Valid @RequestBody CommentReqDto commentReqDto) {
        CommentResDto commentResDto  = commentService.createComment(posterId, commentReqDto);

        return new ResponseEntity<>(new CommonResDto<>("댓글 생성 완료", commentResDto), HttpStatus.CREATED);
    }

    // 댓글 전체 조회
    @GetMapping
    public ResponseEntity<CommonListResDto<CommentResDto>> getComments(@PathVariable Long posterId){
        List<CommentResDto> comments = commentService.getComments(posterId);

        return new ResponseEntity<>(new CommonListResDto<>("댓글 전체 조회 완료", comments), HttpStatus.OK);
    }
    //댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommonResDto<CommentResDto>> updateComment(@PathVariable Long commentId,
                                                       @Valid @RequestBody CommentReqDto commentReqDto) {
        CommentResDto commentResDto = commentService.updateComment(commentId, commentReqDto);

        return new ResponseEntity<>(new CommonResDto<>("댓글 수정 완료", commentResDto), HttpStatus.CREATED);
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResDto> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }



}
