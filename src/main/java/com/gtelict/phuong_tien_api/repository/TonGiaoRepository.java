package com.gtelict.phuong_tien_api.repository;

import com.gtelict.phuong_tien_api.entity.TonGiao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TonGiaoRepository extends JpaRepository<TonGiao, String> {
    @Query("SELECT d FROM TonGiao d WHERE (cast(:updatedAfter as timestamp) IS NULL OR d.ngayCapNhat >= :updatedAfter)")
    List<TonGiao> search(@Param("updatedAfter") LocalDateTime updatedAfter);
}
