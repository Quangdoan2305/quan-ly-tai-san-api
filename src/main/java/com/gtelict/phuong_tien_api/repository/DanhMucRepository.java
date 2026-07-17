package com.gtelict.phuong_tien_api.repository;
import com.gtelict.phuong_tien_api.entity.DanhMuc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, String> {
    Page<DanhMuc> findByLoai(String loai, Pageable pageable);
}