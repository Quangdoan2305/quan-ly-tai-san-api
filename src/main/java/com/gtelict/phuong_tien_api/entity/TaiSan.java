package com.gtelict.phuong_tien_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "pt_tai_san")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class TaiSan {
    @Id
    private String id;
    
    private String maTaiSan;
    private String tenTaiSan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_tai_san")
    private LoaiTaiSan loaiTaiSan;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> thongTinChiTiet;
    
    private String tinhTrang;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_don_vi_quan_ly")
    private DonVi donViQuanLy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_can_bo_su_dung")
    private CanBoChienSi canBoSuDung;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_cap_phat")
    private CanBoChienSi nguoiCapPhat;
    
    private java.time.LocalDate ngayCapPhat;
}
