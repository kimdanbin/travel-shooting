package com.example.travelshooting.comment.service;


import com.example.travelshooting.comment.dto.CommentReqDto;
import com.example.travelshooting.comment.dto.CommentResDto;
import com.example.travelshooting.comment.Comment;
import com.example.travelshooting.comment.repository.CommentRepository;
import com.example.travelshooting.poster.entity.Poster;
import com.example.travelshooting.poster.service.PosterService;
import com.example.travelshooting.user.User;
import com.example.travelshooting.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PosterService posterService;

    @Transactional
    public CommentResDto createComment(Long posterId, CommentReqDto commentReqDto) {
        User user = userService.getUserById(1L);//임시로 만듬
        Poster poster = posterService.findPosterById(posterId);
        Comment comment = new Comment(user, poster, commentReqDto.getComment());
        Comment savedComment = commentRepository.save(comment);
        return CommentResDto.toDto(savedComment);
    }

    public List<CommentResDto> getComments(Long posterId){
        List<Comment> comments = commentRepository.findAllByPosterId(posterId);
        return comments.stream().map(CommentResDto::toDto).collect(Collectors.toList());
    }

    @Transactional
    public CommentResDto updateComment(Long commentId, CommentReqDto commentReqDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("아이디 " + commentId + "에 해당하는 댓글을 찾을 수 없습니다."));;
        comment.updateComment(commentReqDto.getComment());
        return CommentResDto.toDto(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("아이디 " + commentId + "에 해당하는 댓글을 찾을 수 없습니다."));;
        commentRepository.delete(comment);
    }
}
