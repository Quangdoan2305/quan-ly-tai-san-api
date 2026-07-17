package com.gtelict.phuong_tien_api.repository;

import com.gtelict.phuong_tien_api.entity.TaiSan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaiSanRepository extends JpaRepository<TaiSan, String> {
    
    @Query("SELECT t FROM TaiSan t WHERE " +
           "(:maDonViQuanLy IS NULL OR t.maDonViQuanLy = :maDonViQuanLy) AND " +
           "(:maCanBoSuDung IS NULL OR t.maCanBoSuDung = :maCanBoSuDung) AND " +
           "(:tinhTrang IS NULL OR t.tinhTrang = :tinhTrang)")
    Page<TaiSan> search(@Param("maDonViQuanLy") String maDonViQuanLy,
                        @Param("maCanBoSuDung") String maCanBoSuDung,
                        @Param("tinhTrang") String tinhTrang,
                        Pageable pageable);
                        
    List<TaiSan> findByMaCanBoSuDung(String maCanBoSuDung);
    List<TaiSan> findByMaDonViQuanLy(String maDonViQuanLy);
}
