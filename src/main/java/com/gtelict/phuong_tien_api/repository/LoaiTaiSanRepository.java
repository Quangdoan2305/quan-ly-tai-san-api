package com.gtelict.phuong_tien_api.repository;

import com.gtelict.phuong_tien_api.entity.LoaiTaiSan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LoaiTaiSanRepository extends JpaRepository<LoaiTaiSan, String> {
    
    @Query("SELECT d FROM LoaiTaiSan d WHERE (:updatedAfter IS NULL OR d.ngayCapNhat >= :updatedAfter)")
    Page<LoaiTaiSan> findAllWithDeltaSync(@Param("updatedAfter") LocalDateTime updatedAfter, Pageable pageable);
}
