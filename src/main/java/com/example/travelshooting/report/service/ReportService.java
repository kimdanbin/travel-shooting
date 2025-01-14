package com.example.travelshooting.report.service;

import com.example.travelshooting.comment.dto.CommentResDto;
import com.example.travelshooting.comment.service.CommentService;
import com.example.travelshooting.enums.ReportType;
import com.example.travelshooting.poster.service.PosterService;
import com.example.travelshooting.report.Report;
import com.example.travelshooting.report.dto.ReportReqDto;
import com.example.travelshooting.report.dto.ReportResDto;
import com.example.travelshooting.report.repository.ReportRepository;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ReportResDto reportPoster(Long posterId, ReportReqDto reportReqDto) {

        User user = userService.getAuthenticatedUser();
        Report report = new Report(user, ReportType.POSTER, posterId, reportReqDto.getReason());
        reportRepository.save(report);

        // 특정 포스터의 누적 신고가 5번일 경우, 포스터 삭제 처리
        int reportCount = reportRepository.countByFkIdAndType(posterId, ReportType.POSTER);

        if (reportCount >= 5 && report.getType().equals(ReportType.POSTER)){
            List<CommentResDto> comments = commentService.getComments(posterId);
            for (CommentResDto comment : comments) {
                commentService.deleteComment(comment.getId()); // 관련 댓글 먼저 soft delete 처리
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
    public ReportResDto reportComment(Long commentId, ReportReqDto reportReqDto) {

        User user = userService.getAuthenticatedUser();
        Report report = new Report(user, ReportType.COMMENT, commentId, reportReqDto.getReason());
        reportRepository.save(report);

        // 특정 댓글의 누적 신고가 5번일 경우, 댓글 삭제 처리
        int reportCount = reportRepository.countByFkIdAndType(commentId,ReportType.COMMENT);

        if (reportCount >= 5 && report.getType().equals(ReportType.COMMENT)) {
            commentService.deleteComment(commentId);
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
        Page<Report> reportPage = reportRepository.findAll(pageable);

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
