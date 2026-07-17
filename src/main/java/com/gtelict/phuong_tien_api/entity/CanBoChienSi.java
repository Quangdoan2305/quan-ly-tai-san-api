package com.gtelict.phuong_tien_api.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PT_CAN_BO_CHIEN_SI")
@Data
public class CanBoChienSi {
    @Id
    private String id;
    private String maCanBo;
    private String capBac;
    private String chucVu;
    private String idDonVi;
    
    @OneToOne
    @JoinColumn(name = "id_cong_dan", referencedColumnName = "id")
    private CongDan congDan;
}