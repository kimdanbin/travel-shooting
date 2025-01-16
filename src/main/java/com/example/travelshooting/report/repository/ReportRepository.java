package com.example.travelshooting.report.repository;

import com.example.travelshooting.enums.ReportType;
import com.example.travelshooting.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    default Report findByIdOrElseThrow(Long reportId) {
        return findById(reportId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " + reportId + "에 해당하는 신고를 찾을 수 없습니다."));
    }

    int countByFkIdAndType(Long posterId, ReportType type);


    boolean existsByTypeAndFkIdAndUserId(ReportType reportType, Long posterId, Long id);

    // 삭제 되지 않은 글과 댓글의 신고 내역 조회
    @Query("SELECT r FROM Report r " +
            "LEFT JOIN Poster p ON r.fkId = p.id AND r.type = 'POSTER' " +
            "LEFT JOIN Comment c ON r.fkId = c.id AND r.type = 'COMMENT' " +
            "WHERE (r.type = 'POSTER' AND p.isDeleted = false) " +
            "OR (r.type = 'COMMENT' AND c.isDeleted = false)")
    Page<Report> findAllActiveReports(Pageable pageable);

}
