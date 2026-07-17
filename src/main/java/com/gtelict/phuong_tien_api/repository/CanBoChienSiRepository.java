package com.gtelict.phuong_tien_api.repository;
import com.gtelict.phuong_tien_api.entity.CanBoChienSi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CanBoChienSiRepository extends JpaRepository<CanBoChienSi, String> {
    
    @Query("SELECT c FROM CanBoChienSi c WHERE " +
           "(:maDonVi IS NULL OR c.idDonVi = :maDonVi) AND " +
           "(:maCanBo IS NULL OR c.maCanBo = :maCanBo) AND " +
           "(:capBac IS NULL OR c.capBac = :capBac) AND " +
           "(:hoTen IS NULL OR c.congDan.hoTen LIKE %:hoTen%) AND " +
           "(:soCccd IS NULL OR c.congDan.soCccd = :soCccd)")
    Page<CanBoChienSi> search(@Param("maDonVi") String maDonVi, 
                              @Param("maCanBo") String maCanBo, 
                              @Param("capBac") String capBac, 
                              @Param("hoTen") String hoTen, 
                              @Param("soCccd") String soCccd, 
                              Pageable pageable);
}