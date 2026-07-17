package com.gtelict.phuong_tien_api.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PT_DANH_MUC")
@Data
public class DanhMuc {
    @Id
    private String id;
    private String ma;
    private String ten;
    private String loai;
    private String parentId;
    private Integer cap;
    private String trangThai;
}