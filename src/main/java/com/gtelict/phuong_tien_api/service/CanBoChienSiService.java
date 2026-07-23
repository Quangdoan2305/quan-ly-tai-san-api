package com.gtelict.phuong_tien_api.service;

import com.gtelict.phuong_tien_api.dto.*;
import com.gtelict.phuong_tien_api.entity.*;
import com.gtelict.phuong_tien_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CanBoChienSiService {
    @Autowired private CanBoChienSiRepository canBoRepo;
    @Autowired private TaiSanRepository taiSanRepo;

    public PageResponse<CanBoTomTat> search(String maDonVi, String maCanBo, String capBac, String hoTen, String soCccd, Pageable pageable) {
        Page<CanBoChienSi> page = canBoRepo.search(maDonVi, maCanBo, capBac, hoTen, soCccd, pageable);
        List<CanBoTomTat> dtoList = page.getContent().stream().map(this::mapToTomTat).collect(Collectors.toList());
        
        PageResponse<CanBoTomTat> response = new PageResponse<>();
        response.setPageSize(page.getSize());
        response.setPageNumber(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setContent(dtoList);
        return response;
    }

    public CanBoChiTiet getChiTiet(String id, jakarta.servlet.http.HttpServletRequest request) {
        CanBoChienSi entity = canBoRepo.findById(id).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Không tìm thấy cán bộ có ID: " + id));
        
        CanBoChiTiet chiTiet = new CanBoChiTiet();
        chiTiet.setId(entity.getId());
        chiTiet.setMaCanBo(entity.getMaCanBo());
        chiTiet.setCapBac(entity.getCapBac());
        chiTiet.setChucVu(entity.getChucVu());
        
        if (entity.getDonVi() != null) {
            chiTiet.setMaDonVi(entity.getDonVi().getId());
            chiTiet.setTenDonVi(entity.getDonVi().getTen());
        }
        
        if (entity.getCongDan() != null) {
            chiTiet.setHoTen(entity.getCongDan().getHoTen());
            chiTiet.setNgaySinh(entity.getCongDan().getNgaySinh());
            chiTiet.setGioiTinh(entity.getCongDan().getGioiTinh());
            chiTiet.setMaCongDan(entity.getCongDan().getId());
        }
        if (entity.getCongDan() != null) {
            CongDanDto congDanDto = new CongDanDto();
            CongDan cd = entity.getCongDan();
            congDanDto.setId(cd.getId());
            congDanDto.setSoCccd(cd.getSoCccd());
            congDanDto.setHoTen(cd.getHoTen());
            congDanDto.setNgaySinh(cd.getNgaySinh());
            congDanDto.setGioiTinh(cd.getGioiTinh());
            congDanDto.setQueQuan(cd.getQueQuan());
            if (cd.getDanToc() != null) congDanDto.setTenDanToc(cd.getDanToc().getTen());
            if (cd.getTonGiao() != null) congDanDto.setTenTonGiao(cd.getTonGiao().getTen());
            if (cd.getDghcThuongTru() != null) {
                congDanDto.setTenDghcThuongTru(cd.getDghcThuongTru().getTen());
                congDanDto.setDiaChiThuongTru(cd.getDiaChiThuongTru());
            }
            if (cd.getDghcHienTai() != null) {
                congDanDto.setTenDghcHienTai(cd.getDghcHienTai().getTen());
                congDanDto.setDiaChiHienTai(cd.getDiaChiHienTai());
            }
            chiTiet.setThongTinCongDan(congDanDto);
        }

        // [C10 Fix]: Ghi log PII Audit
        if (entity.getCongDan() != null && entity.getCongDan().getSoCccd() != null && !entity.getCongDan().getSoCccd().isEmpty()) {
            String clientIp = request != null ? request.getRemoteAddr() : "UNKNOWN";
            String authHeader = request != null ? request.getHeader("Authorization") : null;
            log.warn("[PII Audit Log] Dữ liệu CCCD đã được trả về. Client IP: {}, Auth: {}", clientIp, (authHeader != null ? "Có Token" : "Không Token"));
        }

        List<TaiSan> taiSans = taiSanRepo.findByCanBoSuDungId(entity.getId());
        chiTiet.setDanhSachTaiSan(taiSans.stream().map(ts -> {
            TaiSanRutGonDto rg = new TaiSanRutGonDto();
            rg.setMaTaiSan(ts.getMaTaiSan());
            rg.setTenTaiSan(ts.getTenTaiSan());
            rg.setThongTinChiTiet(ts.getThongTinChiTiet());
            if (ts.getNguoiCapPhat() != null) {
                rg.setMaNguoiCapPhat(ts.getNguoiCapPhat().getId());
                if (ts.getNguoiCapPhat().getCongDan() != null) {
                    rg.setNguoiCapPhat(ts.getNguoiCapPhat().getCongDan().getHoTen());
                }
            }
            rg.setNgayCapPhat(ts.getNgayCapPhat());
            return rg;
        }).collect(Collectors.toList()));
        
        return chiTiet;
    }

    private CanBoTomTat mapToTomTat(CanBoChienSi entity) {
        CanBoTomTat tomTat = new CanBoTomTat();
        tomTat.setId(entity.getId());
        tomTat.setMaCanBo(entity.getMaCanBo());
        tomTat.setCapBac(entity.getCapBac());
        tomTat.setChucVu(entity.getChucVu());
        if (entity.getDonVi() != null) {
            tomTat.setMaDonVi(entity.getDonVi().getId());
            tomTat.setTenDonVi(entity.getDonVi().getTen());
        }
        if (entity.getCongDan() != null) {
            tomTat.setHoTen(entity.getCongDan().getHoTen());
            tomTat.setNgaySinh(entity.getCongDan().getNgaySinh());
            tomTat.setGioiTinh(entity.getCongDan().getGioiTinh());
            tomTat.setMaCongDan(entity.getCongDan().getId());
        }
        return tomTat;
    }
}
