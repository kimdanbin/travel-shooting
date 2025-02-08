package com.example.travelshooting.company.repository;

import com.example.travelshooting.company.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    default Company findCompanyById(Long companyId) {
        return findById(companyId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디 " +companyId + "에 해당하는 업체를 찾을 수 없습니다."));
    }

    Page<Company> findAll(Pageable pageable);

    boolean existsByName(String name);

    Company findCompanyByIdAndUserId(Long companyId, Long userId);

    @Query("SELECT c FROM Company c JOIN FETCH c.products p WHERE c.id = :companyId ")
    Company findProductById(@Param("companyId") Long companyId);

    @Query(value = "SELECT * FROM leisure_company c WHERE c.name = :name AND c.is_deleted = true", nativeQuery = true)
    Optional<Company> findByNameAndIsDeletedTrue(@Param("name") String name);
}