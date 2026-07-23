package com.gtelict.phuong_tien_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class BienBanRequest {
    @NotBlank(message = "Số biên bản không được để trống")
    @Schema(example = "BB-2024-001")
    private String soBienBan;

    @NotBlank(message = "Loại bên nhận không được để trống")
    @Schema(example = "DON_VI", description = "Loại bên nhận", allowableValues = { "CAN_BO", "DON_VI" })
    private String loaiBenNhan;

    @NotBlank(message = "ID bên nhận không được để trống")
    @Schema(example = "DV001")
    private String idBenNhan;

    @NotBlank(message = "Loại bên giao không được để trống")
    @Schema(example = "KHO", description = "Loại bên giao", allowableValues = { "CAN_BO", "DON_VI", "KHO" })
    private String loaiBenGiao;

    @NotBlank(message = "ID bên giao không được để trống")
    @Schema(example = "KHO_TONG")
    private String idBenGiao;

    @Schema(example = "CB002")
    private String nguoiKyId;

    @NotBlank(message = "Người lập không được để trống")
    @Schema(example = "CB-02", description = "Người lập (Người tạo bản ghi trên phần mềm)")
    private String nguoiLapId;

    @NotBlank(message = "Trạng thái không được để trống")
    @Schema(example = "NHAP", description = "Trạng thái biên bản: NHAP hoặc DA_KY")
    private String trangThai;

    private String fileDinhKem;

    @jakarta.validation.constraints.NotEmpty(message = "Danh sách tài sản không được rỗng")
    @jakarta.validation.Valid
    @Schema(description = "Danh sách tài sản bàn giao kèm tình trạng")
    private List<ChiTietBienBanRequestDto> chiTietTaiSan;
}
