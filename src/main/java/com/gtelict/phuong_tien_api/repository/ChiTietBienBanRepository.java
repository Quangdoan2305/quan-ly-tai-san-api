package com.gtelict.phuong_tien_api.repository;

import com.gtelict.phuong_tien_api.entity.ChiTietBienBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietBienBanRepository extends JpaRepository<ChiTietBienBan, String> {
    List<ChiTietBienBan> findByBienBanId(String bienBanId);
    List<ChiTietBienBan> findByTaiSanId(String taiSanId);
}
