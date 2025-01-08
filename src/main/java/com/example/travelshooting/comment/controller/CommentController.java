package com.example.travelshooting.comment.controller;

import com.example.travelshooting.comment.dto.CommentReqDto;
import com.example.travelshooting.comment.dto.CommentResDto;
import com.example.travelshooting.comment.service.CommentService;
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
    public ResponseEntity<CommentResDto> createComment(@PathVariable Long posterId,
                                                       @Valid@RequestBody CommentReqDto commentReqDto) {
        CommentResDto commentResDto  = commentService.createComment(posterId, commentReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResDto);

    }

    // 댓글 전체 조회
    @GetMapping
    public ResponseEntity<List<CommentResDto>> getComments(@PathVariable Long posterId){
        List<CommentResDto> comments = commentService.getComments(posterId);
        return ResponseEntity.ok(comments);
    }
    //댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResDto> updateComment(@PathVariable Long commentId,
                                                       @Valid@RequestBody CommentReqDto commentReqDto) {
        CommentResDto commentResDto = commentService.updateComment(commentId, commentReqDto);
        return ResponseEntity.ok(commentResDto);
    }
    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResDto> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }



}
