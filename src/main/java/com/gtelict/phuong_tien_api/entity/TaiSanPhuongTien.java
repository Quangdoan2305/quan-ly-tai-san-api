package com.gtelict.phuong_tien_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pt_tai_san_phuong_tien")
@Data
@EqualsAndHashCode(callSuper = true)
public class TaiSanPhuongTien extends TaiSan {

    @Column(name = "bien_so", unique = true)
    private String bienSo;

    @Column(name = "so_khung")
    private String soKhung;

    @Column(name = "so_may")
    private String soMay;

    @Column(name = "nhan_hieu")
    private String nhanHieu;
}
