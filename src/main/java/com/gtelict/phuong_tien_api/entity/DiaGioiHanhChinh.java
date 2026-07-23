package com.gtelict.phuong_tien_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "pt_dia_gioi_hanh_chinh")
@Data
public class DiaGioiHanhChinh {
    @Id
    private String id;
    
    @Column(unique = true, nullable = false)
    private String ma;
    
    @Column(nullable = false)
    private String ten;
    
    private Integer cap;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private DiaGioiHanhChinh parent;
    
    private String trangThai;
    
    private LocalDateTime ngayCapNhat;
}
