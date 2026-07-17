package com.gtelict.phuong_tien_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "PT_TAI_SAN")
@Data
public class TaiSan {
    @Id
    private String id;
    
    private String maTaiSan;
    private String tenTaiSan;
    private String idDanhMucLoai;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> thongTinChiTiet;
    
    private String tinhTrang;
    
    private String idDonViQuanLy;
    private String idCanBoSuDung;
    private String idNguoiCapPhat;
    private java.time.LocalDate ngayCapPhat;

}
