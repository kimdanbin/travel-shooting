package com.example.travelshooting.report.repository;

import com.example.travelshooting.enums.ReportType;
import com.example.travelshooting.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    default Report findByIdOrElseThrow(Long reportId) {
        return findById(reportId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "신고 내역이 존재하지 않습니다."));
    }

    int countByFkIdAndType(Long posterId, ReportType type);

}
