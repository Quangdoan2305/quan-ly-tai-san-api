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
           "(:idDonViQuanLy IS NULL OR t.idDonViQuanLy = :idDonViQuanLy) AND " +
           "(:idCanBoSuDung IS NULL OR t.idCanBoSuDung = :idCanBoSuDung) AND " +
           "(:tinhTrang IS NULL OR t.tinhTrang = :tinhTrang)")
    Page<TaiSan> search(@Param("idDonViQuanLy") String idDonViQuanLy,
                        @Param("idCanBoSuDung") String idCanBoSuDung,
                        @Param("tinhTrang") String tinhTrang,
                        Pageable pageable);
                        
    List<TaiSan> findByIdCanBoSuDung(String idCanBoSuDung);
    List<TaiSan> findByIdDonViQuanLy(String idDonViQuanLy);
}
