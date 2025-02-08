package com.example.travelshooting.comment.service;


import com.example.travelshooting.comment.dto.CommentResDto;
import com.example.travelshooting.comment.entity.Comment;
import com.example.travelshooting.comment.repository.CommentRepository;
import com.example.travelshooting.enums.UserRole;
import com.example.travelshooting.poster.entity.Poster;
import com.example.travelshooting.poster.service.PosterService;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public CommentResDto createComment(Long posterId, String content) {

        User user = userService.findAuthenticatedUser();
        Poster poster = posterService.findPosterById(posterId);

        Comment comment = new Comment(user, poster, content);
        Comment savedComment = commentRepository.save(comment);

        return new CommentResDto(savedComment);
    }

    // 댓글 전체 조회
    @Transactional(readOnly = true)
    public List<CommentResDto> findComments(Long posterId){

        List<Comment> comments = commentRepository.findAllByPosterId(posterId);

        return comments.stream().map(CommentResDto::new).toList();
    }

    //댓글 수정
    @Transactional
    public CommentResDto updateComment(Long posterId, Long commentId, String content) {

        User user = userService.findAuthenticatedUser();
        Comment comment = findCommentById(commentId);

        if (!isPosterIdValid(posterId, commentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 포스터에 없는 댓글입니다.");
        }

        if (!user.getId().equals(comment.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 댓글만 수정 가능합니다.");
        }

        comment.updateComment(content);

        return new CommentResDto(comment);
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long posterId, Long commentId) {

        User user = userService.findAuthenticatedUser();
        Comment comment = findCommentById(commentId);

        if (!isPosterIdValid(posterId, commentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 포스터에 없는 댓글입니다.");
        }

        // 관리자가 아니거나 본인이 작성한 포스터가 아닐 경우
        if (!user.getRole().equals(UserRole.ADMIN) && !user.getId().equals(comment.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 댓글만 삭제 가능합니다.");
        }

        commentRepository.delete(comment);
    }

    public boolean isPosterIdValid(Long posterId, Long commentId) {
        return findCommentById(commentId).getPoster().getId().equals(posterId);
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

    // posterId로 댓글 삭제
    public void deleteCommentsByPosterId(Long posterId) {
        commentRepository.deleteByPosterId(posterId);
    }

    // commentId로 댓글 삭제
    public void deleteCommentsById(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
