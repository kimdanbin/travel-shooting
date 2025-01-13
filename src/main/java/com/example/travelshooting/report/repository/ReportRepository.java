package com.example.travelshooting.report.repository;

import com.example.travelshooting.enums.ReportType;
import com.example.travelshooting.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    int countByFkIdAndType(Long posterId, ReportType type);
}
