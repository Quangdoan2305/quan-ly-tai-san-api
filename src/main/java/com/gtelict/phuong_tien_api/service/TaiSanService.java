package com.gtelict.phuong_tien_api.service;

import com.gtelict.phuong_tien_api.dto.*;
import com.gtelict.phuong_tien_api.entity.*;
import com.gtelict.phuong_tien_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaiSanService {
    @Autowired
    private TaiSanRepository taiSanRepo;
    @Autowired
    private BienBanBanGiaoRepository bienBanRepo;
    @Autowired
    private ChiTietBienBanRepository chiTietRepo;

    public PageResponse<TaiSanDto> search(String tuKhoa, String maDonViQuanLy, String maCanBoSuDung, String tinhTrang, Pageable pageable) {
        String safeTuKhoa = (tuKhoa == null || tuKhoa.trim().isEmpty()) ? "" : tuKhoa.trim();
        String safeMaDonVi = (maDonViQuanLy == null) ? "" : maDonViQuanLy.trim();
        String safeMaCanBo = (maCanBoSuDung == null) ? "" : maCanBoSuDung.trim();
        String safeTinhTrang = (tinhTrang == null) ? "" : tinhTrang.trim();
        
        Page<TaiSan> page = taiSanRepo.searchNative(safeTuKhoa, safeMaDonVi, safeMaCanBo, safeTinhTrang, pageable);
        
        PageResponse<TaiSanDto> response = new PageResponse<>();
        response.setPageSize(page.getSize());
        response.setPageNumber(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setContent(page.getContent().stream().map(this::mapToDto).collect(Collectors.toList()));
        return response;
    }

    public TaiSanDto getChiTiet(String id) {
        TaiSan ts = taiSanRepo.findById(id).orElse(null);
        return ts != null ? mapToDto(ts) : null;
    }

    @Transactional
    public BienBanBanGiaoDto banGiaoTaiSan(BienBanRequest request) {
        // Business Validation
        if ("DA_KY".equals(request.getTrangThai()) && (request.getNguoiKyId() == null || request.getNguoiKyId().trim().isEmpty())) {
            throw new IllegalArgumentException("Biên bản trạng thái ĐÃ KÝ (DA_KY) bắt buộc phải có ID người ký (nguoiKyId)!");
        }

        // 1. Tạo Biên bản
        BienBanBanGiao bb = new BienBanBanGiao();
        bb.setId(UUID.randomUUID().toString());
        bb.setSoBienBan(request.getSoBienBan());
        bb.setNgayBanGiao(LocalDate.now());
        bb.setLoaiBenNhan(request.getLoaiBenNhan());
        bb.setIdBenNhan(request.getIdBenNhan());
        bb.setLoaiBenGiao(request.getLoaiBenGiao());
        bb.setIdBenGiao(request.getIdBenGiao());
        
        bb.setTrangThai(request.getTrangThai());
        bb.setNguoiLapId(request.getNguoiLapId());
        
        // Chỉ lưu người ký khi đã ký
        if ("DA_KY".equals(request.getTrangThai())) {
            bb.setNguoiKyId(request.getNguoiKyId());
        } else {
            bb.setNguoiKyId(null);
        }
        
        bienBanRepo.save(bb);

        // 2. Xử lý từng tài sản & Lưu chi tiết biên bản
        List<String> taiSanIds = request.getChiTietTaiSan().stream().map(ChiTietBienBanRequestDto::getTaiSanId).collect(Collectors.toList());
        List<TaiSan> taiSans = taiSanRepo.findAllWithLockByIdIn(taiSanIds);

        for (ChiTietBienBanRequestDto ctReq : request.getChiTietTaiSan()) {
            TaiSan ts = taiSans.stream().filter(t -> t.getId().equals(ctReq.getTaiSanId())).findFirst().orElse(null);
            if (ts == null) continue;

            // [B6 Fix]: Kiểm tra quyền sở hữu trước khi bàn giao
            if ("CAN_BO".equals(request.getLoaiBenGiao())) {
                if (ts.getIdCanBoSuDung() == null || !ts.getIdCanBoSuDung().equals(request.getIdBenGiao())) {
                    throw new IllegalArgumentException("Tài sản " + ts.getMaTaiSan() + " không do cán bộ này sử dụng, không thể bàn giao!");
                }
            } else if ("DON_VI".equals(request.getLoaiBenGiao())) {
                if (ts.getIdDonViQuanLy() == null || !ts.getIdDonViQuanLy().equals(request.getIdBenGiao())) {
                    throw new IllegalArgumentException("Tài sản " + ts.getMaTaiSan() + " không thuộc quản lý của đơn vị này, không thể bàn giao!");
                }
            } else if ("KHO".equals(request.getLoaiBenGiao())) {
                if (ts.getIdDonViQuanLy() != null || ts.getIdCanBoSuDung() != null) {
                    throw new IllegalArgumentException("Tài sản " + ts.getMaTaiSan() + " đã có đơn vị/cán bộ quản lý, không thể xuất từ Kho tổng!");
                }
            }

            // Nếu đã ký thì cập nhật ownership thật, còn nháp thì không cập nhật
            if ("DA_KY".equals(request.getTrangThai())) {
                if ("DON_VI".equals(request.getLoaiBenNhan())) {
                    ts.setIdDonViQuanLy(request.getIdBenNhan());
                    ts.setIdCanBoSuDung(null);
                } else if ("CAN_BO".equals(request.getLoaiBenNhan())) {
                    ts.setIdCanBoSuDung(request.getIdBenNhan());
                }
                ts.setIdNguoiCapPhat(request.getNguoiKyId());
                ts.setNgayCapPhat(LocalDate.now());
            }

            ChiTietBienBan ct = new ChiTietBienBan();
            ct.setId(UUID.randomUUID().toString());
            ct.setBienBanId(bb.getId());
            ct.setTaiSanId(ts.getId());
            ct.setTinhTrangLucGiao(ctReq.getTinhTrangLucGiao());
            ct.setGhiChu(ctReq.getGhiChu());
            chiTietRepo.save(ct);
        }
        
        if ("DA_KY".equals(request.getTrangThai())) {
            taiSanRepo.saveAll(taiSans);
        }

        // 3. Trả về DTO
        BienBanBanGiaoDto dto = new BienBanBanGiaoDto();
        dto.setId(bb.getId());
        dto.setSoBienBan(bb.getSoBienBan());
        dto.setNgayBanGiao(bb.getNgayBanGiao());
        dto.setLoaiBenNhan(bb.getLoaiBenNhan());
        dto.setIdBenNhan(bb.getIdBenNhan());
        dto.setLoaiBenGiao(bb.getLoaiBenGiao());
        dto.setIdBenGiao(bb.getIdBenGiao());
        dto.setNguoiKyId(bb.getNguoiKyId());
        dto.setNguoiLapId(bb.getNguoiLapId());
        dto.setTrangThai(bb.getTrangThai());
        dto.setFileDinhKem(bb.getFileDinhKem());

        dto.setChiTietTaiSan(request.getChiTietTaiSan().stream().map(ctReq -> {
            TaiSan ts = taiSans.stream().filter(t -> t.getId().equals(ctReq.getTaiSanId())).findFirst().orElse(null);
            ChiTietBienBanDto ctDto = new ChiTietBienBanDto();
            ctDto.setTinhTrangLucGiao(ctReq.getTinhTrangLucGiao());
            ctDto.setGhiChu(ctReq.getGhiChu());
            if (ts != null) {
                TaiSanRutGonDto rg = new TaiSanRutGonDto();
                rg.setMaTaiSan(ts.getMaTaiSan());
                rg.setTenTaiSan(ts.getTenTaiSan());
                rg.setThongTinChiTiet(ts.getThongTinChiTiet());
                rg.setMaNguoiCapPhat(ts.getIdNguoiCapPhat());
                rg.setNgayCapPhat(ts.getNgayCapPhat());
                if (ts.getIdNguoiCapPhat() != null)
                    rg.setNguoiCapPhat("Tên " + ts.getIdNguoiCapPhat());
                ctDto.setTaiSan(rg);
            }
            return ctDto;
        }).collect(Collectors.toList()));

        return dto;
    }

    public BienBanBanGiaoDto getChiTietBienBan(String id) {
        BienBanBanGiao bb = bienBanRepo.findById(id).orElse(null);
        if (bb == null) return null;
        
        List<ChiTietBienBan> chiTiets = chiTietRepo.findByBienBanId(bb.getId());
        List<String> taiSanIds = chiTiets.stream().map(ChiTietBienBan::getTaiSanId).collect(Collectors.toList());
        List<TaiSan> taiSans = taiSanRepo.findAllById(taiSanIds);
        
        BienBanBanGiaoDto dto = new BienBanBanGiaoDto();
        dto.setId(bb.getId());
        dto.setSoBienBan(bb.getSoBienBan());
        dto.setNgayBanGiao(bb.getNgayBanGiao());
        dto.setLoaiBenNhan(bb.getLoaiBenNhan());
        dto.setIdBenNhan(bb.getIdBenNhan());
        dto.setLoaiBenGiao(bb.getLoaiBenGiao());
        dto.setIdBenGiao(bb.getIdBenGiao());
        dto.setNguoiKyId(bb.getNguoiKyId());
        dto.setNguoiLapId(bb.getNguoiLapId());
        dto.setTrangThai(bb.getTrangThai());
        dto.setFileDinhKem(bb.getFileDinhKem());
        
        dto.setChiTietTaiSan(chiTiets.stream().map(ct -> {
            TaiSan ts = taiSans.stream().filter(t -> t.getId().equals(ct.getTaiSanId())).findFirst().orElse(null);
            ChiTietBienBanDto ctDto = new ChiTietBienBanDto();
            ctDto.setTinhTrangLucGiao(ct.getTinhTrangLucGiao());
            ctDto.setGhiChu(ct.getGhiChu());
            if (ts != null) {
                TaiSanRutGonDto rg = new TaiSanRutGonDto();
                rg.setMaTaiSan(ts.getMaTaiSan());
                rg.setTenTaiSan(ts.getTenTaiSan());
                rg.setThongTinChiTiet(ts.getThongTinChiTiet());
                rg.setMaNguoiCapPhat(ts.getIdNguoiCapPhat());
                rg.setNgayCapPhat(ts.getNgayCapPhat());
                if (ts.getIdNguoiCapPhat() != null)
                    rg.setNguoiCapPhat("Tên " + ts.getIdNguoiCapPhat());
                ctDto.setTaiSan(rg);
            }
            return ctDto;
        }).collect(Collectors.toList()));
        
        return dto;
    }

    @Transactional
    public BienBanBanGiaoDto pheDuyetBienBan(String id, PheDuyetRequest request) {
        BienBanBanGiao bb = bienBanRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy biên bản!"));
        if (!"NHAP".equals(bb.getTrangThai())) {
            throw new IllegalArgumentException("Chỉ có thể ký duyệt biên bản ở trạng thái NHAP!");
        }
        
        bb.setTrangThai("DA_KY");
        bb.setNguoiKyId(request.getNguoiKyId());
        bienBanRepo.save(bb);
        
        List<ChiTietBienBan> chiTiets = chiTietRepo.findByBienBanId(bb.getId());
        List<String> taiSanIds = chiTiets.stream().map(ChiTietBienBan::getTaiSanId).collect(Collectors.toList());
        List<TaiSan> taiSans = taiSanRepo.findAllWithLockByIdIn(taiSanIds);
        
        for (TaiSan ts : taiSans) {
            // [B6 Fix]: Kiểm tra quyền sở hữu trước khi phê duyệt (tránh trường hợp tài sản đã đổi chủ trong lúc chờ duyệt)
            if ("CAN_BO".equals(bb.getLoaiBenGiao())) {
                if (ts.getIdCanBoSuDung() == null || !ts.getIdCanBoSuDung().equals(bb.getIdBenGiao())) {
                    throw new IllegalArgumentException("Tài sản " + ts.getMaTaiSan() + " không do cán bộ này sử dụng, không thể phê duyệt!");
                }
            } else if ("DON_VI".equals(bb.getLoaiBenGiao())) {
                if (ts.getIdDonViQuanLy() == null || !ts.getIdDonViQuanLy().equals(bb.getIdBenGiao())) {
                    throw new IllegalArgumentException("Tài sản " + ts.getMaTaiSan() + " không thuộc quản lý của đơn vị này, không thể phê duyệt!");
                }
            } else if ("KHO".equals(bb.getLoaiBenGiao())) {
                if (ts.getIdDonViQuanLy() != null || ts.getIdCanBoSuDung() != null) {
                    throw new IllegalArgumentException("Tài sản " + ts.getMaTaiSan() + " đã có đơn vị/cán bộ quản lý, không thể xuất từ Kho tổng!");
                }
            }

            if ("DON_VI".equals(bb.getLoaiBenNhan())) {
                ts.setIdDonViQuanLy(bb.getIdBenNhan());
                ts.setIdCanBoSuDung(null);
            } else if ("CAN_BO".equals(bb.getLoaiBenNhan())) {
                ts.setIdCanBoSuDung(bb.getIdBenNhan());
            }
            ts.setIdNguoiCapPhat(request.getNguoiKyId());
            ts.setNgayCapPhat(LocalDate.now());
        }
        taiSanRepo.saveAll(taiSans);
        
        return getChiTietBienBan(id);
    }

    private TaiSanDto mapToDto(TaiSan ts) {
        TaiSanDto dto = new TaiSanDto();
        dto.setId(ts.getId());
        dto.setMaTaiSan(ts.getMaTaiSan());
        dto.setTenTaiSan(ts.getTenTaiSan());
        dto.setMaDanhMucLoai(ts.getIdDanhMucLoai());
        if (ts.getIdDanhMucLoai() != null)
            dto.setTenLoaiTaiSan("Tên loại " + ts.getIdDanhMucLoai());
        dto.setThongTinChiTiet(ts.getThongTinChiTiet());
        dto.setTinhTrang(ts.getTinhTrang());

        dto.setMaDonViQuanLy(ts.getIdDonViQuanLy());
        if (ts.getIdDonViQuanLy() != null)
            dto.setTenDonViQuanLy("Tên đơn vị " + ts.getIdDonViQuanLy());

        dto.setMaCanBoSuDung(ts.getIdCanBoSuDung());
        if (ts.getIdCanBoSuDung() != null)
            dto.setTenCanBoSuDung("Tên cán bộ " + ts.getIdCanBoSuDung());

        dto.setMaNguoiCapPhat(ts.getIdNguoiCapPhat());
        if (ts.getIdNguoiCapPhat() != null)
            dto.setNguoiCapPhat("Tên người cấp phát " + ts.getIdNguoiCapPhat());
        dto.setNgayCapPhat(ts.getNgayCapPhat());

        return dto;
    }

    public PageResponse<BienBanBanGiaoDto> getLichSuBanGiao(String taiSanId, Pageable pageable) {
        Page<BienBanBanGiao> page = bienBanRepo.findByTaiSanId(taiSanId, pageable);

        PageResponse<BienBanBanGiaoDto> response = new PageResponse<>();
        response.setPageSize(page.getSize());
        response.setPageNumber(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());

        response.setContent(page.getContent().stream().map(bb -> {
            BienBanBanGiaoDto dto = new BienBanBanGiaoDto();
            dto.setId(bb.getId());
            dto.setSoBienBan(bb.getSoBienBan());
            dto.setNgayBanGiao(bb.getNgayBanGiao());
            dto.setLoaiBenNhan(bb.getLoaiBenNhan());
            dto.setIdBenNhan(bb.getIdBenNhan());
            dto.setLoaiBenGiao(bb.getLoaiBenGiao());
            dto.setIdBenGiao(bb.getIdBenGiao());
            dto.setNguoiKyId(bb.getNguoiKyId());
            dto.setNguoiLapId(bb.getNguoiLapId());
            dto.setTrangThai(bb.getTrangThai());
            dto.setFileDinhKem(bb.getFileDinhKem());
            return dto;
        }).collect(Collectors.toList()));

        return response;
    }

    public PageResponse<BienBanBanGiaoDto> searchBienBan(String soBienBan, LocalDate tuNgay, LocalDate denNgay,
            String loaiBenNhan, String idBenNhan, String loaiBenGiao, String idBenGiao, String nguoiKyId,
            String maDonVi, String trangThai, Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<BienBanBanGiao> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (soBienBan != null && !soBienBan.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("soBienBan")), "%" + soBienBan.trim().toLowerCase() + "%"));
            }
            if (tuNgay != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ngayBanGiao"), tuNgay));
            }
            if (denNgay != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ngayBanGiao"), denNgay));
            }
            if (trangThai != null && !trangThai.isEmpty()) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }
            if (loaiBenNhan != null && !loaiBenNhan.isEmpty()) {
                predicates.add(cb.equal(root.get("loaiBenNhan"), loaiBenNhan));
            }
            if (idBenNhan != null && !idBenNhan.isEmpty()) {
                predicates.add(cb.equal(root.get("idBenNhan"), idBenNhan));
            }
            if (loaiBenGiao != null && !loaiBenGiao.isEmpty()) {
                predicates.add(cb.equal(root.get("loaiBenGiao"), loaiBenGiao));
            }
            if (idBenGiao != null && !idBenGiao.isEmpty()) {
                predicates.add(cb.equal(root.get("idBenGiao"), idBenGiao));
            }
            if (nguoiKyId != null && !nguoiKyId.isEmpty()) {
                predicates.add(cb.equal(root.get("nguoiKyId"), nguoiKyId));
            }
            if (maDonVi != null && !maDonVi.isEmpty()) {
                jakarta.persistence.criteria.Predicate p1 = cb.and(
                        cb.equal(root.get("loaiBenNhan"), "DON_VI"),
                        cb.equal(root.get("idBenNhan"), maDonVi));
                jakarta.persistence.criteria.Predicate p2 = cb.and(
                        cb.equal(root.get("loaiBenGiao"), "DON_VI"),
                        cb.equal(root.get("idBenGiao"), maDonVi));
                predicates.add(cb.or(p1, p2));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<BienBanBanGiao> page = bienBanRepo.findAll(spec, pageable);

        PageResponse<BienBanBanGiaoDto> response = new PageResponse<>();
        response.setPageSize(page.getSize());
        response.setPageNumber(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());

        response.setContent(page.getContent().stream().map(bb -> {
            BienBanBanGiaoDto dto = new BienBanBanGiaoDto();
            dto.setId(bb.getId());
            dto.setSoBienBan(bb.getSoBienBan());
            dto.setNgayBanGiao(bb.getNgayBanGiao());
            dto.setLoaiBenNhan(bb.getLoaiBenNhan());
            dto.setIdBenNhan(bb.getIdBenNhan());
            dto.setLoaiBenGiao(bb.getLoaiBenGiao());
            dto.setIdBenGiao(bb.getIdBenGiao());
            dto.setNguoiKyId(bb.getNguoiKyId());
            dto.setNguoiLapId(bb.getNguoiLapId());
            dto.setTrangThai(bb.getTrangThai());
            dto.setFileDinhKem(bb.getFileDinhKem());
            return dto;
        }).collect(Collectors.toList()));

        return response;
    }
}
