package com.example.travelshooting.comment.service;


import com.example.travelshooting.comment.dto.CommentReqDto;
import com.example.travelshooting.comment.dto.CommentResDto;
import com.example.travelshooting.comment.Comment;
import com.example.travelshooting.comment.repository.CommentRepository;
import com.example.travelshooting.poster.Poster;
import com.example.travelshooting.poster.repository.PosterRepository;
import com.example.travelshooting.poster.service.PosterService;
import com.example.travelshooting.user.User;
import com.example.travelshooting.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        Poster poster = posterService.getPosterById(posterId);
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
        Comment comment = commentRepository.findByIdOrElseThrow(commentId);
        comment.updateComment(commentReqDto.getComment());
        return CommentResDto.toDto(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findByIdOrElseThrow(commentId);
        comment.deleteComment();
    }
}
