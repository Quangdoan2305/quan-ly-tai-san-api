package com.gtelict.phuong_tien_api.repository;

import com.gtelict.phuong_tien_api.entity.BienBanBanGiao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BienBanBanGiaoRepository extends JpaRepository<BienBanBanGiao, String>, JpaSpecificationExecutor<BienBanBanGiao> {
    @Query("SELECT b FROM BienBanBanGiao b JOIN ChiTietBienBan c ON b.id = c.bienBanId WHERE c.taiSanId = :taiSanId ORDER BY b.ngayBanGiao DESC")
    Page<BienBanBanGiao> findByTaiSanId(@Param("taiSanId") String taiSanId, Pageable pageable);
}
