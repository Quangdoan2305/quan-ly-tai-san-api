package com.gtelict.phuong_tien_api.repository;

import com.gtelict.phuong_tien_api.entity.DanToc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DanTocRepository extends JpaRepository<DanToc, String> {
    @Query("SELECT d FROM DanToc d WHERE (cast(:updatedAfter as timestamp) IS NULL OR d.ngayCapNhat >= :updatedAfter)")
    List<DanToc> search(@Param("updatedAfter") LocalDateTime updatedAfter);
}
