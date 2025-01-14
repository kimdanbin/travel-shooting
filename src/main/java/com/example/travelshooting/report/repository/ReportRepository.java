package com.example.travelshooting.report.repository;

import com.example.travelshooting.enums.ReportType;
import com.example.travelshooting.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    default Report findByIdOrElseThrow(Long reportId) {
        return findById(reportId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,  "아이디 " + reportId + "에 해당하는 신고를 찾을 수 없습니다."));
    }

    int countByFkIdAndType(Long posterId, ReportType type);

}
