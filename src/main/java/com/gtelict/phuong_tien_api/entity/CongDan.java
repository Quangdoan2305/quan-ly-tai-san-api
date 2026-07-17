package com.gtelict.phuong_tien_api.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "PT_CONG_DAN")
@Data
public class CongDan {
    @Id
    private String id;
    private String soCccd;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String queQuan;
    private String maDanToc;
    private String maTonGiao;
    private String maDghcThuongTru;
    private String diaChiThuongTru;
    private String maDghcHienTai;
    private String diaChiHienTai;
}