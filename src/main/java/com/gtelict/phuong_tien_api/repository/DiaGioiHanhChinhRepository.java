package com.gtelict.phuong_tien_api.repository;

import com.gtelict.phuong_tien_api.entity.DiaGioiHanhChinh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DiaGioiHanhChinhRepository extends JpaRepository<DiaGioiHanhChinh, String> {
    @Query("SELECT d FROM DiaGioiHanhChinh d WHERE " +
           "(cast(:updatedAfter as timestamp) IS NULL OR d.ngayCapNhat >= :updatedAfter) AND " +
           "(:cap IS NULL OR d.cap = :cap) AND " +
           "(:parentId IS NULL OR d.parent.id = :parentId)")
    Page<DiaGioiHanhChinh> search(
            @Param("updatedAfter") LocalDateTime updatedAfter,
            @Param("cap") Integer cap,
            @Param("parentId") String parentId,
            Pageable pageable);
}
