package com.example.travelshooting.report.service;

import com.example.travelshooting.comment.dto.CommentResDto;
import com.example.travelshooting.comment.entity.Comment;
import com.example.travelshooting.comment.service.CommentService;
import com.example.travelshooting.common.Const;
import com.example.travelshooting.enums.DomainType;
import com.example.travelshooting.poster.entity.Poster;
import com.example.travelshooting.poster.service.PosterService;
import com.example.travelshooting.report.dto.ReportResDto;
import com.example.travelshooting.report.entity.Report;
import com.example.travelshooting.report.repository.ReportRepository;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PosterService posterService;
    private final UserService userService;
    private final CommentService commentService;

    @Transactional
    public ReportResDto reportPoster(Long posterId, String reason) {

        Poster findPoster = posterService.findPosterById(posterId);
        User user = userService.findAuthenticatedUser();
        // 본인이 작성한 글을 본인이 신고하려는 경우
        if(findPoster.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 글은 신고할 수 없습니다.");
        }
        // 신고한 글을 또 신고하려는 경우
        if(reportRepository.existsByTypeAndFkIdAndUserId(DomainType.POSTER, posterId, user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 신고한 포스터입니다.");
        }
        Report report = new Report(user, DomainType.POSTER, posterId, reason);
        reportRepository.save(report);

        // 특정 포스터의 누적 신고가 5번일 경우, 포스터 삭제 처리
        int reportCount = reportRepository.countByFkIdAndType(posterId, DomainType.POSTER);

        if (reportCount >= Const.REPORT_COUNT && report.getType().equals(DomainType.POSTER)){
            List<CommentResDto> comments = commentService.findComments(posterId);
            for (CommentResDto comment : comments) {
                commentService.deleteComment(posterId, comment.getId()); // 관련 댓글 먼저 soft delete 처리
            }
            posterService.deletePoster(posterId);
        }

        return new ReportResDto(
                report.getId(),
                report.getUser().getId(),
                report.getType(),
                report.getFkId(),
                report.getReason(),
                report.getCreatedAt()
                );
    }

    @Transactional
    public ReportResDto reportComment(Long commentId, String reason) {
        Comment findComment = commentService.findCommentById(commentId);
        User user = userService.findAuthenticatedUser();
        // 본인이 작성한 댓글을 본인이 신고하려는 경우
        if(findComment.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 댓글은 신고할 수 없습니다.");
        }
        // 신고한 댓글을 또 신고하려는 경우
        if(reportRepository.existsByTypeAndFkIdAndUserId(DomainType.COMMENT, commentId, user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 신고한 댓글입니다.");
        }
        Report report = new Report(user, DomainType.COMMENT, commentId, reason);
        reportRepository.save(report);

        // 특정 댓글의 누적 신고가 5번일 경우, 댓글 삭제 처리
        int reportCount = reportRepository.countByFkIdAndType(commentId, DomainType.COMMENT);

        if (reportCount >= Const.REPORT_COUNT && report.getType().equals(DomainType.COMMENT)) {
            commentService.deleteComment(findComment.getPoster().getId() , commentId);
        }

        return new ReportResDto(
                report.getId(),
                report.getUser().getId(),
                report.getType(),
                report.getFkId(),
                report.getReason(),
                report.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ReportResDto> findAllReports(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // isDeleted = false인 글과 댓글 신고 내역 조회
        Page<Report> reportPage = reportRepository.findAllActiveReports(pageable);
        return reportPage.stream()
                .map(Report -> new ReportResDto(
                        Report.getId(),
                        Report.getUser().getId(),
                        Report.getType(),
                        Report.getFkId(),
                        Report.getReason(),
                        Report.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReportResDto findReport(Long reportId) {
        Report findReport = reportRepository.findByIdOrElseThrow(reportId);

        // 타입이 POSTER 인 경우, 해당 포스터 삭제 유무 확인
        if (findReport.getType() == DomainType.POSTER) {
            Poster poster = posterService.findByIdIncludeDeleted((findReport.getFkId()))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + findReport.getFkId() + "에 해당하는 포스터를 찾을 수 없습니다."));
            if(poster.isDeleted()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이미 삭제된 포스터입니다.");
            }
        }

        // 타입이 COMMENT 인 경우, 해당 댓글 삭제 유무 확인
        if (findReport.getType() == DomainType.COMMENT) {
            Comment comment = commentService.findByIdIncludeDeleted((findReport.getFkId()))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + findReport.getFkId() + "에 해당하는 댓글을 찾을 수 없습니다."));
            if(comment.isDeleted()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이미 삭제된 댓글입니다.");
            }
        }

        return new ReportResDto(
                findReport.getId(),
                findReport.getUser().getId(),
                findReport.getType(),
                findReport.getFkId(),
                findReport.getReason(),
                findReport.getCreatedAt()
        );
    }
}
