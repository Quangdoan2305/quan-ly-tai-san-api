package com.gtelict.phuong_tien_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "PT_BIEN_BAN_BAN_GIAO")
@Data
public class BienBanBanGiao {
    @Id
    private String id;
    
    private String soBienBan;
    private LocalDate ngayBanGiao;
    
    private String loaiBenNhan; // "DON_VI" hoac "CAN_BO"
    private String idBenNhan;
    private String loaiBenGiao; // "DON_VI", "CAN_BO", "KHO"
    private String idBenGiao;
    
    private String nguoiKyId;
    private String fileDinhKem;
}
