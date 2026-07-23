package com.gtelict.phuong_tien_api.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "pt_cong_dan")
@Data
public class CongDan {
    @Id
    private String id;
    private String soCccd;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String queQuan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dan_toc")
    private DanToc danToc;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ton_giao")
    private TonGiao tonGiao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dghc_thuong_tru")
    private DiaGioiHanhChinh dghcThuongTru;
    
    private String diaChiThuongTru;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dghc_hien_tai")
    private DiaGioiHanhChinh dghcHienTai;
    
    private String diaChiHienTai;
}