package com.gtelict.phuong_tien_api.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pt_can_bo_chien_si")
@Data
public class CanBoChienSi {
    @Id
    private String id;
    private String maCanBo;
    private String capBac;
    private String chucVu;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_don_vi")
    private DonVi donVi;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cong_dan", referencedColumnName = "id")
    private CongDan congDan;
}