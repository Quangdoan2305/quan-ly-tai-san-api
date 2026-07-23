package com.gtelict.phuong_tien_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pt_tai_san_vu_khi")
@Data
@EqualsAndHashCode(callSuper = true)
public class TaiSanVuKhi extends TaiSan {

    @Column(name = "so_hieu", unique = true)
    private String soHieu;

    @Column(name = "nam_san_xuat")
    private String namSanXuat;
}
