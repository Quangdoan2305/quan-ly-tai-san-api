package com.gtelict.phuong_tien_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pt_tai_san_thiet_bi_cntt")
@Data
@EqualsAndHashCode(callSuper = true)
public class TaiSanThietBiCntt extends TaiSan {

    @Column(name = "so_serial", unique = true)
    private String soSerial;

    @Column(name = "dia_chi_mac")
    private String diaChiMac;

    @Column(name = "cau_hinh")
    private String cauHinh;
}
