package com.gtelict.phuong_tien_api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DonViDto {
    private String id;
    private String ma;
    private String ten;
    private String parentId;
    private String diaChi;
    private String soDienThoai;
    private String trangThai;
    private LocalDateTime ngayCapNhat;
}
