package com.gtelict.phuong_tien_api.repository;

import com.gtelict.phuong_tien_api.entity.BienBanBanGiao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface BienBanBanGiaoRepository extends JpaRepository<BienBanBanGiao, String> {
    @Query("SELECT b FROM BienBanBanGiao b JOIN ChiTietBienBan c ON b.id = c.bienBanId WHERE c.taiSanId = :taiSanId ORDER BY b.ngayBanGiao DESC")
    Page<BienBanBanGiao> findByTaiSanId(@Param("taiSanId") String taiSanId, Pageable pageable);

    @Query("SELECT b FROM BienBanBanGiao b WHERE " +
           "(:soBienBan IS NULL OR LOWER(b.soBienBan) LIKE LOWER(CONCAT('%', :soBienBan, '%'))) AND " +
           "(CAST(:tuNgay AS date) IS NULL OR b.ngayBanGiao >= :tuNgay) AND " +
           "(CAST(:denNgay AS date) IS NULL OR b.ngayBanGiao <= :denNgay) AND " +
           "(:loaiBenNhan IS NULL OR b.loaiBenNhan = :loaiBenNhan) AND " +
           "(:idBenNhan IS NULL OR b.idBenNhan = :idBenNhan) AND " +
           "(:loaiBenGiao IS NULL OR b.loaiBenGiao = :loaiBenGiao) AND " +
           "(:idBenGiao IS NULL OR b.idBenGiao = :idBenGiao) AND " +
           "(:nguoiKyId IS NULL OR b.nguoiKyId = :nguoiKyId) AND " +
           "(:maDonVi IS NULL OR (b.loaiBenNhan = 'DON_VI' AND b.idBenNhan = :maDonVi) OR (b.loaiBenGiao = 'DON_VI' AND b.idBenGiao = :maDonVi)) " +
           "ORDER BY b.ngayBanGiao DESC")
    Page<BienBanBanGiao> search(
        @Param("soBienBan") String soBienBan,
        @Param("tuNgay") java.time.LocalDate tuNgay,
        @Param("denNgay") java.time.LocalDate denNgay,
        @Param("loaiBenNhan") String loaiBenNhan,
        @Param("idBenNhan") String idBenNhan,
        @Param("loaiBenGiao") String loaiBenGiao,
        @Param("idBenGiao") String idBenGiao,
        @Param("nguoiKyId") String nguoiKyId,
        @Param("maDonVi") String maDonVi,
        Pageable pageable
    );
}
