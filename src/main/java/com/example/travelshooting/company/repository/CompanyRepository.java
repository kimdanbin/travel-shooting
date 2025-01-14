package com.example.travelshooting.company.repository;

import com.example.travelshooting.company.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    default Company findByIdOrElseThrow(Long companyId) {
        return findById(companyId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " +companyId + "에 해당하는 업체를 찾을 수 없습니다."));
    }

    Page<Company> findAll(Pageable pageable);
}
