package com.gtelict.phuong_tien_api.repository;

import com.gtelict.phuong_tien_api.entity.DonVi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DonViRepository extends JpaRepository<DonVi, String> {
    @Query("SELECT d FROM DonVi d WHERE " +
           "(cast(:updatedAfter as timestamp) IS NULL OR d.ngayCapNhat >= :updatedAfter) AND " +
           "(:parentId IS NULL OR d.parent.id = :parentId)")
    Page<DonVi> search(
            @Param("updatedAfter") LocalDateTime updatedAfter,
            @Param("parentId") String parentId,
            Pageable pageable);
}
