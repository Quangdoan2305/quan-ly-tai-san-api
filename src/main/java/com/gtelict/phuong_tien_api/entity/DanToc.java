package com.gtelict.phuong_tien_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "pt_dan_toc")
@Data
public class DanToc {
    @Id
    private String id;
    
    @Column(unique = true, nullable = false)
    private String ma;
    
    @Column(nullable = false)
    private String ten;
    
    private String trangThai;
    
    private LocalDateTime ngayCapNhat;
}
