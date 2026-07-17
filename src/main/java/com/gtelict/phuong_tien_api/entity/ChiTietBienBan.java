package com.gtelict.phuong_tien_api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PT_CHI_TIET_BIEN_BAN")
@Data
public class ChiTietBienBan {
    @Id
    private String id;
    
    private String bienBanId;
    private String taiSanId;
    private String tinhTrangLucGiao;
    private String ghiChu;
}
