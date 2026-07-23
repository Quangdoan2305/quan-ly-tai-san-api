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
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaiSanRepository extends JpaRepository<TaiSan, String>, JpaSpecificationExecutor<TaiSan> {

    @EntityGraph(attributePaths = {"loaiTaiSan", "donViQuanLy", "canBoSuDung", "canBoSuDung.congDan", "nguoiCapPhat", "nguoiCapPhat.congDan"})
    @Query("SELECT t FROM TaiSan t WHERE " +
            "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
            "  LOWER(t.maTaiSan) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "  LOWER(t.tenTaiSan) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "  LOWER(CAST(t.thongTinChiTiet AS string)) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "  LOWER(TREAT(t as TaiSanPhuongTien).bienSo) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "  LOWER(TREAT(t as TaiSanPhuongTien).soKhung) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "  LOWER(TREAT(t as TaiSanPhuongTien).soMay) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "  LOWER(TREAT(t as TaiSanThietBiCntt).soSerial) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "  LOWER(TREAT(t as TaiSanVuKhi).soHieu) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))" +
            ") AND " +
            "(:idDonViQuanLy IS NULL OR :idDonViQuanLy = '' OR t.donViQuanLy.id = :idDonViQuanLy) AND " +
            "(:idCanBoSuDung IS NULL OR :idCanBoSuDung = '' OR t.canBoSuDung.id = :idCanBoSuDung) AND " +
            "(:tinhTrang IS NULL OR :tinhTrang = '' OR t.tinhTrang = :tinhTrang)")
    Page<TaiSan> search(
            @Param("tuKhoa") String tuKhoa,
            @Param("idDonViQuanLy") String idDonViQuanLy,
            @Param("idCanBoSuDung") String idCanBoSuDung,
            @Param("tinhTrang") String tinhTrang,
            Pageable pageable);

    List<TaiSan> findByCanBoSuDungId(String idCanBoSuDung);

    List<TaiSan> findByDonViQuanLyId(String idDonViQuanLy);

    @Query(value = "SELECT id FROM pt_tai_san WHERE id IN :ids ORDER BY id FOR UPDATE", nativeQuery = true)
    List<String> lockTaiSanIds(@Param("ids") List<String> ids);

    @Query("SELECT t FROM TaiSan t WHERE t.id IN :ids ORDER BY t.id")
    List<TaiSan> findAllByIdWithSubclass(@Param("ids") List<String> ids);
}
