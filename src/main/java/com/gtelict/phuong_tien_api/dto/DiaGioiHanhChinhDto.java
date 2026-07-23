package com.gtelict.phuong_tien_api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DiaGioiHanhChinhDto {
    private String id;
    private String ma;
    private String ten;
    private Integer cap;
    private String parentId;
    private String trangThai;
    private LocalDateTime ngayCapNhat;
}
