package com.gtelict.phuong_tien_api.repository;
import com.gtelict.phuong_tien_api.entity.CongDan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CongDanRepository extends JpaRepository<CongDan, String> {
}