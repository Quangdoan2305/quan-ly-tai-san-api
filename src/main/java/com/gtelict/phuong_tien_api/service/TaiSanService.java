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
    @Autowired
    private DonViRepository donViRepo;
    @Autowired
    private CanBoChienSiRepository canBoRepo;

    public PageResponse<TaiSanDto> search(String tuKhoa, String maDonViQuanLy, String maCanBoSuDung, String tinhTrang, Pageable pageable) {
        String safeTuKhoa = (tuKhoa == null || tuKhoa.trim().isEmpty()) ? "" : tuKhoa.trim();
        String safeMaDonVi = (maDonViQuanLy == null) ? "" : maDonViQuanLy.trim();
        String safeMaCanBo = (maCanBoSuDung == null) ? "" : maCanBoSuDung.trim();
        String safeTinhTrang = (tinhTrang == null) ? "" : tinhTrang.trim();
        
        Page<TaiSan> page = taiSanRepo.search(safeTuKhoa, safeMaDonVi, safeMaCanBo, safeTinhTrang, pageable);
        
        PageResponse<TaiSanDto> response = new PageResponse<>();
        response.setPageSize(page.getSize());
        response.setPageNumber(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setContent(page.getContent().stream().map(this::mapToDto).collect(Collectors.toList()));
        return response;
    }

    public TaiSanDto getChiTiet(String id) {
        TaiSan ts = taiSanRepo.findById(id).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Không tìm thấy tài sản có ID: " + id));
        return mapToDto(ts);
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
        java.util.Set<String> uniqueIds = new java.util.HashSet<>(taiSanIds);
        if (uniqueIds.size() != taiSanIds.size()) {
            throw new IllegalArgumentException("Danh sách tài sản trong biên bản bị trùng lặp!");
        }

        taiSanRepo.lockTaiSanIds(taiSanIds);
        List<TaiSan> taiSans = taiSanRepo.findAllByIdWithSubclass(taiSanIds);
        
        if (taiSans.size() != taiSanIds.size()) {
            throw new jakarta.persistence.EntityNotFoundException("Một hoặc nhiều tài sản trong danh sách không tồn tại!");
        }
        
        DonVi donViNhan = "DON_VI".equals(request.getLoaiBenNhan()) ? donViRepo.findById(request.getIdBenNhan()).orElse(null) : null;
        CanBoChienSi canBoNhan = "CAN_BO".equals(request.getLoaiBenNhan()) ? canBoRepo.findById(request.getIdBenNhan()).orElse(null) : null;
        CanBoChienSi nguoiKy = null;
        if ("DA_KY".equals(request.getTrangThai()) && request.getNguoiKyId() != null) {
            nguoiKy = canBoRepo.findById(request.getNguoiKyId()).orElse(null);
        }

        for (ChiTietBienBanRequestDto ctReq : request.getChiTietTaiSan()) {
            TaiSan ts = taiSans.stream().filter(t -> t.getId().equals(ctReq.getTaiSanId())).findFirst().orElse(null);
            if (ts == null) {
                throw new IllegalArgumentException("Không tìm thấy tài sản có mã ID: " + ctReq.getTaiSanId());
            }

            // Kiểm tra tình trạng tài sản không cho phép bàn giao
            if ("DA_THANH_LY".equals(ts.getTinhTrang()) || "HONG".equals(ts.getTinhTrang())) {
                throw new IllegalArgumentException("Tài sản " + ts.getMaTaiSan() + " đang ở trạng thái " + ts.getTinhTrang() + ", không được phép bàn giao!");
            }

            // Kiểm tra quyền sở hữu trước khi bàn giao
            if ("CAN_BO".equals(request.getLoaiBenGiao())) {
                if (ts.getCanBoSuDung() == null || !ts.getCanBoSuDung().getId().equals(request.getIdBenGiao())) {
                    throw new IllegalArgumentException("Tài sản " + ts.getMaTaiSan() + " không do cán bộ này sử dụng, không thể bàn giao!");
                }
            } else if ("DON_VI".equals(request.getLoaiBenGiao())) {
                if (ts.getDonViQuanLy() == null || !ts.getDonViQuanLy().getId().equals(request.getIdBenGiao())) {
                    throw new IllegalArgumentException("Tài sản " + ts.getMaTaiSan() + " không thuộc quản lý của đơn vị này, không thể bàn giao!");
                }
            } else if ("KHO".equals(request.getLoaiBenGiao())) {
                if (ts.getDonViQuanLy() != null || ts.getCanBoSuDung() != null) {
                    throw new IllegalArgumentException("Tài sản " + ts.getMaTaiSan() + " đã có đơn vị/cán bộ quản lý, không thể xuất từ Kho tổng!");
                }
            }

            // Nếu đã ký thì cập nhật ownership thật, còn nháp thì không cập nhật
            if ("DA_KY".equals(request.getTrangThai())) {
                if ("DON_VI".equals(request.getLoaiBenNhan())) {
                    ts.setDonViQuanLy(donViNhan);
                    ts.setCanBoSuDung(null);
                } else if ("CAN_BO".equals(request.getLoaiBenNhan())) {
                    ts.setCanBoSuDung(canBoNhan);
                    ts.setDonViQuanLy(null);
                } else if ("KHO".equals(request.getLoaiBenNhan())) {
                    ts.setDonViQuanLy(null);
                    ts.setCanBoSuDung(null);
                }
                ts.setNguoiCapPhat(nguoiKy);
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

        return getChiTietBienBan(bb.getId());
    }

    public BienBanBanGiaoDto getChiTietBienBan(String id) {
        BienBanBanGiao bb = bienBanRepo.findById(id).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Không tìm thấy biên bản có ID: " + id));
        
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
                
                if (ts instanceof TaiSanPhuongTien) {
                    TaiSanPhuongTien pt = (TaiSanPhuongTien) ts;
                    rg.setBienSo(pt.getBienSo());
                    rg.setSoKhung(pt.getSoKhung());
                    rg.setSoMay(pt.getSoMay());
                    rg.setNhanHieu(pt.getNhanHieu());
                } else if (ts instanceof TaiSanThietBiCntt) {
                    TaiSanThietBiCntt cntt = (TaiSanThietBiCntt) ts;
                    rg.setSoSerial(cntt.getSoSerial());
                    rg.setDiaChiMac(cntt.getDiaChiMac());
                    rg.setCauHinh(cntt.getCauHinh());
                } else if (ts instanceof TaiSanVuKhi) {
                    TaiSanVuKhi vk = (TaiSanVuKhi) ts;
                    rg.setSoHieu(vk.getSoHieu());
                    rg.setNamSanXuat(vk.getNamSanXuat());
                }
                if (ts.getNguoiCapPhat() != null) {
                    rg.setMaNguoiCapPhat(ts.getNguoiCapPhat().getId());
                    if (ts.getNguoiCapPhat().getCongDan() != null) {
                        rg.setNguoiCapPhat(ts.getNguoiCapPhat().getCongDan().getHoTen());
                    }
                }
                rg.setNgayCapPhat(ts.getNgayCapPhat());
                ctDto.setTaiSan(rg);
            }
            return ctDto;
        }).collect(Collectors.toList()));
        
        return dto;
    }

    @Transactional
    public BienBanBanGiaoDto pheDuyetBienBan(String id, PheDuyetRequest request) {
        BienBanBanGiao bb = bienBanRepo.findById(id).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Không tìm thấy biên bản có ID: " + id));
        if (!"NHAP".equals(bb.getTrangThai())) {
            throw new IllegalArgumentException("Chỉ có thể ký duyệt biên bản ở trạng thái NHAP!");
        }
        
        bb.setTrangThai("DA_KY");
        bb.setNguoiKyId(request.getNguoiKyId());
        bienBanRepo.save(bb);
        
        List<ChiTietBienBan> chiTiets = chiTietRepo.findByBienBanId(bb.getId());
        List<String> taiSanIds = chiTiets.stream().map(ChiTietBienBan::getTaiSanId).collect(Collectors.toList());
        taiSanRepo.lockTaiSanIds(taiSanIds);
        List<TaiSan> taiSans = taiSanRepo.findAllByIdWithSubclass(taiSanIds);
        
        if (taiSans.size() != taiSanIds.size()) {
            throw new jakarta.persistence.EntityNotFoundException("Một hoặc nhiều tài sản trong biên bản không tồn tại hoặc đã bị xóa!");
        }
        
        DonVi donViNhan = "DON_VI".equals(bb.getLoaiBenNhan()) ? donViRepo.findById(bb.getIdBenNhan()).orElse(null) : null;
        CanBoChienSi canBoNhan = "CAN_BO".equals(bb.getLoaiBenNhan()) ? canBoRepo.findById(bb.getIdBenNhan()).orElse(null) : null;
        CanBoChienSi nguoiKy = canBoRepo.findById(request.getNguoiKyId()).orElse(null);

        for (TaiSan ts : taiSans) {
            if ("CAN_BO".equals(bb.getLoaiBenGiao())) {
                if (ts.getCanBoSuDung() == null || !ts.getCanBoSuDung().getId().equals(bb.getIdBenGiao())) {
                    throw new IllegalArgumentException("Tài sản " + ts.getMaTaiSan() + " không do cán bộ này sử dụng, không thể phê duyệt!");
                }
            } else if ("DON_VI".equals(bb.getLoaiBenGiao())) {
                if (ts.getDonViQuanLy() == null || !ts.getDonViQuanLy().getId().equals(bb.getIdBenGiao())) {
                    throw new IllegalArgumentException("Tài sản " + ts.getMaTaiSan() + " không thuộc quản lý của đơn vị này, không thể phê duyệt!");
                }
            } else if ("KHO".equals(bb.getLoaiBenGiao())) {
                if (ts.getDonViQuanLy() != null || ts.getCanBoSuDung() != null) {
                    throw new IllegalArgumentException("Tài sản " + ts.getMaTaiSan() + " đã có đơn vị/cán bộ quản lý, không thể xuất từ Kho tổng!");
                }
            }

            if ("DON_VI".equals(bb.getLoaiBenNhan())) {
                ts.setDonViQuanLy(donViNhan);
                ts.setCanBoSuDung(null);
            } else if ("CAN_BO".equals(bb.getLoaiBenNhan())) {
                ts.setCanBoSuDung(canBoNhan);
                ts.setDonViQuanLy(null);
            } else if ("KHO".equals(bb.getLoaiBenNhan())) {
                ts.setDonViQuanLy(null);
                ts.setCanBoSuDung(null);
            }
            ts.setNguoiCapPhat(nguoiKy);
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
        
        if (ts.getLoaiTaiSan() != null) {
            dto.setMaDanhMucLoai(ts.getLoaiTaiSan().getId());
            dto.setTenLoaiTaiSan(ts.getLoaiTaiSan().getTen());
        }
        
        if (ts instanceof TaiSanPhuongTien) {
            TaiSanPhuongTien pt = (TaiSanPhuongTien) ts;
            dto.setBienSo(pt.getBienSo());
            dto.setSoKhung(pt.getSoKhung());
            dto.setSoMay(pt.getSoMay());
            dto.setNhanHieu(pt.getNhanHieu());
        } else if (ts instanceof TaiSanThietBiCntt) {
            TaiSanThietBiCntt cntt = (TaiSanThietBiCntt) ts;
            dto.setSoSerial(cntt.getSoSerial());
            dto.setDiaChiMac(cntt.getDiaChiMac());
            dto.setCauHinh(cntt.getCauHinh());
        } else if (ts instanceof TaiSanVuKhi) {
            TaiSanVuKhi vk = (TaiSanVuKhi) ts;
            dto.setSoHieu(vk.getSoHieu());
            dto.setNamSanXuat(vk.getNamSanXuat());
        }
        
        dto.setThongTinChiTiet(ts.getThongTinChiTiet());
        dto.setTinhTrang(ts.getTinhTrang());

        if (ts.getDonViQuanLy() != null) {
            dto.setMaDonViQuanLy(ts.getDonViQuanLy().getId());
            dto.setTenDonViQuanLy(ts.getDonViQuanLy().getTen());
        }

        if (ts.getCanBoSuDung() != null) {
            dto.setMaCanBoSuDung(ts.getCanBoSuDung().getId());
            if (ts.getCanBoSuDung().getCongDan() != null) {
                dto.setTenCanBoSuDung(ts.getCanBoSuDung().getCongDan().getHoTen());
            }
        }

        if (ts.getNguoiCapPhat() != null) {
            dto.setMaNguoiCapPhat(ts.getNguoiCapPhat().getId());
            if (ts.getNguoiCapPhat().getCongDan() != null) {
                dto.setNguoiCapPhat(ts.getNguoiCapPhat().getCongDan().getHoTen());
            }
        }
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
