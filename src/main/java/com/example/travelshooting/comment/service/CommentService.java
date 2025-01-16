package com.example.travelshooting.comment.service;


import com.example.travelshooting.comment.dto.CommentReqDto;
import com.example.travelshooting.comment.dto.CommentResDto;
import com.example.travelshooting.comment.entity.Comment;
import com.example.travelshooting.comment.repository.CommentRepository;
import com.example.travelshooting.poster.entity.Poster;
import com.example.travelshooting.poster.service.PosterService;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PosterService posterService;

    //댓글 생성
    @Transactional
    public CommentResDto createComment(Long posterId, CommentReqDto commentReqDto) {

        User user = userService.findAuthenticatedUser();
        Poster poster = posterService.findPosterById(posterId);

        Comment comment = new Comment(user, poster, commentReqDto.getComment());
        Comment savedComment = commentRepository.save(comment);

        return new CommentResDto(savedComment);
    }

    // 댓글 전체 조회
    public List<CommentResDto> findComments(Long posterId){

        List<Comment> comments = commentRepository.findAllByPosterId(posterId);

        return comments.stream().map(CommentResDto::new).toList();
    }

    //댓글 수정
    @Transactional
    public CommentResDto updateComment(Long commentId, CommentReqDto commentReqDto) {

        User user = userService.findAuthenticatedUser();
        Comment comment = findCommentById(commentId);

        if (!user.getId().equals(comment.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 댓글만 수정 가능합니다.");
        }

        comment.updateComment(commentReqDto.getComment());

        return new CommentResDto(comment);
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {

        User user = userService.findAuthenticatedUser();
        Comment comment = findCommentById(commentId);

        if (!user.getId().equals(comment.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 댓글만 삭제 가능합니다.");
        }

        commentRepository.delete(comment);
    }

    // 댓글 id로 댓글 찾기
    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + commentId + "에 해당하는 댓글을 찾을 수 없습니다."));
    }

    // isDeleted 가 true 인 댓글도 포함해서 찾기
    public Optional<Comment> findByIdIncludeDeleted(Long commentId) {
        return commentRepository.findByIdIncludeDeleted(commentId);
    }
}
