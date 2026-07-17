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
           "(:soBienBan = '' OR LOWER(b.soBienBan) LIKE LOWER(CONCAT('%', :soBienBan, '%'))) AND " +
           "(b.ngayBanGiao >= :tuNgay) AND " +
           "(b.ngayBanGiao <= :denNgay) AND " +
           "(:loaiBenNhan = '' OR b.loaiBenNhan = :loaiBenNhan) AND " +
           "(:idBenNhan = '' OR b.idBenNhan = :idBenNhan) AND " +
           "(:loaiBenGiao = '' OR b.loaiBenGiao = :loaiBenGiao) AND " +
           "(:idBenGiao = '' OR b.idBenGiao = :idBenGiao) AND " +
           "(:nguoiKyId = '' OR b.nguoiKyId = :nguoiKyId) AND " +
           "(:maDonVi = '' OR (b.loaiBenNhan = 'DON_VI' AND b.idBenNhan = :maDonVi) OR (b.loaiBenGiao = 'DON_VI' AND b.idBenGiao = :maDonVi)) " +
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
