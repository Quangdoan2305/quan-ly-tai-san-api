package com.gtelict.phuong_tien_api.repository;

import com.gtelict.phuong_tien_api.entity.TaiSan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaiSanRepository extends JpaRepository<TaiSan, String>, JpaSpecificationExecutor<TaiSan> {

    @Query(value = "SELECT * FROM pt_tai_san t WHERE " +
            "(:tuKhoa = '' OR " +
            "  LOWER(t.ma_tai_san) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "  LOWER(t.ten_tai_san) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "  CAST(t.thong_tin_chi_tiet AS TEXT) ILIKE CONCAT('%', :tuKhoa, '%')" +
            ") AND " +
            "(:idDonViQuanLy = '' OR t.id_don_vi_quan_ly = :idDonViQuanLy) AND " +
            "(:idCanBoSuDung = '' OR t.id_can_bo_su_dung = :idCanBoSuDung) AND " +
            "(:tinhTrang = '' OR t.tinh_trang = :tinhTrang)",
            countQuery = "SELECT count(*) FROM pt_tai_san t WHERE " +
            "(:tuKhoa = '' OR " +
            "  LOWER(t.ma_tai_san) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "  LOWER(t.ten_tai_san) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "  CAST(t.thong_tin_chi_tiet AS TEXT) ILIKE CONCAT('%', :tuKhoa, '%')" +
            ") AND " +
            "(:idDonViQuanLy = '' OR t.id_don_vi_quan_ly = :idDonViQuanLy) AND " +
            "(:idCanBoSuDung = '' OR t.id_can_bo_su_dung = :idCanBoSuDung) AND " +
            "(:tinhTrang = '' OR t.tinh_trang = :tinhTrang)",
            nativeQuery = true)
    Page<TaiSan> searchNative(
            @Param("tuKhoa") String tuKhoa,
            @Param("idDonViQuanLy") String idDonViQuanLy,
            @Param("idCanBoSuDung") String idCanBoSuDung,
            @Param("tinhTrang") String tinhTrang,
            Pageable pageable);

    List<TaiSan> findByIdCanBoSuDung(String idCanBoSuDung);

    List<TaiSan> findByIdDonViQuanLy(String idDonViQuanLy);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    @Query("SELECT t FROM TaiSan t WHERE t.id IN :ids ORDER BY t.id")
    List<TaiSan> findAllWithLockByIdIn(@Param("ids") List<String> ids);
}
