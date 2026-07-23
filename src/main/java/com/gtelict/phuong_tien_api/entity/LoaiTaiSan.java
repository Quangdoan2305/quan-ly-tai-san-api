package com.gtelict.phuong_tien_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "pt_loai_tai_san")
@Data
public class LoaiTaiSan {

    @Id
    private String id;

    private String ma;

    private String ten;

    private String nhomLoai;

    private String trangThai;

    private LocalDateTime ngayCapNhat;
}
