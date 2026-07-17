package com.gtelict.phuong_tien_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PheDuyetRequest {
    @NotBlank(message = "Người ký duyệt không được để trống")
    @Schema(example = "CB-04", description = "ID của cán bộ thực hiện ký duyệt")
    private String nguoiKyId;
}
