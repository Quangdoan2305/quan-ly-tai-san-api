-- =======================================================
-- KỊCH BẢN KHỞI TẠO CƠ SỞ DỮ LIỆU (POSTGRESQL)
-- Xóa bảng cũ nếu tồn tại (để làm mới hoàn toàn)
-- =======================================================

DROP TABLE IF EXISTS pt_chi_tiet_bien_ban CASCADE;
DROP TABLE IF EXISTS pt_bien_ban_ban_giao CASCADE;
DROP TABLE IF EXISTS pt_tai_san CASCADE;
DROP TABLE IF EXISTS pt_can_bo_chien_si CASCADE;
DROP TABLE IF EXISTS pt_cong_dan CASCADE;
DROP TABLE IF EXISTS pt_dia_gioi_hanh_chinh CASCADE;
DROP TABLE IF EXISTS pt_don_vi CASCADE;
DROP TABLE IF EXISTS pt_dan_toc CASCADE;
DROP TABLE IF EXISTS pt_ton_giao CASCADE;
DROP TABLE IF EXISTS pt_tai_san_phuong_tien CASCADE;
DROP TABLE IF EXISTS pt_tai_san_thiet_bi_cntt CASCADE;
DROP TABLE IF EXISTS pt_tai_san_vu_khi CASCADE;
DROP TABLE IF EXISTS pt_loai_tai_san CASCADE;

-- =======================================================
-- 1. TẠO BẢNG
-- =======================================================

CREATE TABLE pt_dia_gioi_hanh_chinh (
    id VARCHAR(50) PRIMARY KEY,
    ma VARCHAR(50) UNIQUE NOT NULL,
    ten VARCHAR(255) NOT NULL,
    cap INTEGER,
    parent_id VARCHAR(50) REFERENCES pt_dia_gioi_hanh_chinh(id) ON DELETE RESTRICT,
    trang_thai VARCHAR(50) DEFAULT 'ACTIVE',
    ngay_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pt_don_vi (
    id VARCHAR(50) PRIMARY KEY,
    ma VARCHAR(50) UNIQUE NOT NULL,
    ten VARCHAR(255) NOT NULL,
    parent_id VARCHAR(50) REFERENCES pt_don_vi(id) ON DELETE RESTRICT,
    dia_chi TEXT,
    so_dien_thoai VARCHAR(20),
    trang_thai VARCHAR(50) DEFAULT 'ACTIVE',
    ngay_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pt_dan_toc (
    id VARCHAR(50) PRIMARY KEY,
    ma VARCHAR(50) UNIQUE NOT NULL,
    ten VARCHAR(255) NOT NULL,
    trang_thai VARCHAR(50) DEFAULT 'ACTIVE',
    ngay_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pt_ton_giao (
    id VARCHAR(50) PRIMARY KEY,
    ma VARCHAR(50) UNIQUE NOT NULL,
    ten VARCHAR(255) NOT NULL,
    trang_thai VARCHAR(50) DEFAULT 'ACTIVE',
    ngay_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pt_loai_tai_san (
    id VARCHAR(50) PRIMARY KEY,
    ma VARCHAR(50) UNIQUE NOT NULL,
    ten VARCHAR(255) NOT NULL,
    nhom_loai VARCHAR(50) NOT NULL, -- PHUONG_TIEN, THIET_BI_CNTT, VU_KHI, KHAC
    trang_thai VARCHAR(50) DEFAULT 'ACTIVE',
    ngay_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pt_cong_dan (
    id VARCHAR(50) PRIMARY KEY,
    so_cccd VARCHAR(20) UNIQUE NOT NULL,
    ho_ten VARCHAR(255) NOT NULL,
    ngay_sinh DATE,
    gioi_tinh VARCHAR(10),
    que_quan TEXT,
    id_dan_toc VARCHAR(50) REFERENCES pt_dan_toc(id) ON DELETE RESTRICT,
    id_ton_giao VARCHAR(50) REFERENCES pt_ton_giao(id) ON DELETE RESTRICT,
    id_dghc_thuong_tru VARCHAR(50) REFERENCES pt_dia_gioi_hanh_chinh(id) ON DELETE RESTRICT,
    dia_chi_thuong_tru TEXT,
    id_dghc_hien_tai VARCHAR(50) REFERENCES pt_dia_gioi_hanh_chinh(id) ON DELETE RESTRICT,
    dia_chi_hien_tai TEXT
);

CREATE TABLE pt_can_bo_chien_si (
    id VARCHAR(50) PRIMARY KEY,
    ma_can_bo VARCHAR(50) UNIQUE NOT NULL,
    cap_bac VARCHAR(50),
    chuc_vu VARCHAR(100),
    id_don_vi VARCHAR(50) REFERENCES pt_don_vi(id) ON DELETE RESTRICT,
    id_cong_dan VARCHAR(50) REFERENCES pt_cong_dan(id) ON DELETE RESTRICT
);

CREATE TABLE pt_tai_san (
    id VARCHAR(50) PRIMARY KEY,
    ma_tai_san VARCHAR(50) UNIQUE NOT NULL,
    ten_tai_san VARCHAR(255) NOT NULL,
    id_loai_tai_san VARCHAR(50) REFERENCES pt_loai_tai_san(id) ON DELETE RESTRICT,
    thong_tin_chi_tiet JSONB,
    tinh_trang VARCHAR(50),
    id_don_vi_quan_ly VARCHAR(50) REFERENCES pt_don_vi(id) ON DELETE RESTRICT,
    id_can_bo_su_dung VARCHAR(50) REFERENCES pt_can_bo_chien_si(id) ON DELETE RESTRICT,
    id_nguoi_cap_phat VARCHAR(50) REFERENCES pt_can_bo_chien_si(id) ON DELETE RESTRICT,
    ngay_cap_phat DATE,
    CHECK (
        (id_don_vi_quan_ly IS NOT NULL AND id_can_bo_su_dung IS NULL) OR 
        (id_don_vi_quan_ly IS NULL AND id_can_bo_su_dung IS NOT NULL) OR
        (id_don_vi_quan_ly IS NULL AND id_can_bo_su_dung IS NULL)
    )
);

CREATE TABLE pt_tai_san_phuong_tien (
    id VARCHAR(50) PRIMARY KEY REFERENCES pt_tai_san(id) ON DELETE CASCADE,
    bien_so VARCHAR(50) UNIQUE,
    so_khung VARCHAR(100),
    so_may VARCHAR(100),
    nhan_hieu VARCHAR(100)
);

CREATE TABLE pt_tai_san_thiet_bi_cntt (
    id VARCHAR(50) PRIMARY KEY REFERENCES pt_tai_san(id) ON DELETE CASCADE,
    so_serial VARCHAR(100) UNIQUE,
    dia_chi_mac VARCHAR(50),
    cau_hinh TEXT
);

CREATE TABLE pt_tai_san_vu_khi (
    id VARCHAR(50) PRIMARY KEY REFERENCES pt_tai_san(id) ON DELETE CASCADE,
    so_hieu VARCHAR(100) UNIQUE,
    nam_san_xuat VARCHAR(20)
);

CREATE TABLE pt_bien_ban_ban_giao (
    id VARCHAR(50) PRIMARY KEY,
    so_bien_ban VARCHAR(50) UNIQUE NOT NULL,
    ngay_ban_giao DATE,
    loai_ben_nhan VARCHAR(50),
    id_ben_nhan VARCHAR(50),
    loai_ben_giao VARCHAR(50),
    id_ben_giao VARCHAR(50),
    nguoi_lap_id VARCHAR(50) REFERENCES pt_can_bo_chien_si(id) ON DELETE RESTRICT,
    nguoi_ky_id VARCHAR(50) REFERENCES pt_can_bo_chien_si(id) ON DELETE RESTRICT,
    trang_thai VARCHAR(20) DEFAULT 'NHAP',
    file_dinh_kem TEXT
);

CREATE TABLE pt_chi_tiet_bien_ban (
    id VARCHAR(50) PRIMARY KEY,
    bien_ban_id VARCHAR(50) REFERENCES pt_bien_ban_ban_giao(id) ON DELETE CASCADE,
    tai_san_id VARCHAR(50) REFERENCES pt_tai_san(id) ON DELETE RESTRICT,
    tinh_trang_luc_giao VARCHAR(50),
    ghi_chu TEXT
);

-- =======================================================
-- 2. INSERT DỮ LIỆU MẪU (MOCK DATA)
-- =======================================================

-- 2.1. pt_dia_gioi_hanh_chinh
INSERT INTO pt_dia_gioi_hanh_chinh (id, ma, ten, cap, parent_id, trang_thai, ngay_cap_nhat) VALUES
('DG-01', 'HN_001', 'Quận Ba Đình, Hà Nội', 2, NULL, 'ACTIVE', CURRENT_TIMESTAMP),
('DG-02', 'ND_002', 'TP Nam Định', 2, NULL, 'ACTIVE', CURRENT_TIMESTAMP),
('DG-03', 'TB_003', 'Huyện Vũ Thư, Thái Bình', 2, NULL, 'ACTIVE', CURRENT_TIMESTAMP),
('DG-04', 'HP_004', 'Quận Lê Chân, Hải Phòng', 2, NULL, 'ACTIVE', CURRENT_TIMESTAMP),
('DG-05', 'HN_005', 'Quận Đống Đa, Hà Nội', 2, NULL, 'ACTIVE', CURRENT_TIMESTAMP);

-- 2.2. pt_don_vi
INSERT INTO pt_don_vi (id, ma, ten, parent_id, dia_chi, so_dien_thoai, trang_thai, ngay_cap_nhat) VALUES
('DV-01', 'DV_01', 'Công an phường Cống Vị', NULL, 'Ba Đình, Hà Nội', '02412345678', 'ACTIVE', CURRENT_TIMESTAMP),
('DV-02', 'DV_02', 'Phòng CSGT Công an TP Hà Nội', NULL, 'Hoàn Kiếm, Hà Nội', '02487654321', 'ACTIVE', CURRENT_TIMESTAMP);

-- 2.3. pt_dan_toc
INSERT INTO pt_dan_toc (id, ma, ten, trang_thai, ngay_cap_nhat) VALUES
('DT-01', 'KINH', 'Dân tộc Kinh', 'ACTIVE', CURRENT_TIMESTAMP);

-- 2.4. pt_ton_giao
INSERT INTO pt_ton_giao (id, ma, ten, trang_thai, ngay_cap_nhat) VALUES
('TG-01', 'KHONG', 'Không tôn giáo', 'ACTIVE', CURRENT_TIMESTAMP);

-- 2.5. pt_loai_tai_san
INSERT INTO pt_loai_tai_san (id, ma, ten, nhom_loai, trang_thai, ngay_cap_nhat) VALUES
('LPT-01', 'OTO', 'Ô tô chuyên dụng', 'PHUONG_TIEN', 'ACTIVE', CURRENT_TIMESTAMP),
('LPT-02', 'XEMAY', 'Xe mô tô tuần tra', 'PHUONG_TIEN', 'ACTIVE', CURRENT_TIMESTAMP),
('LPT-03', 'VU_KHI', 'Súng ngắn', 'VU_KHI', 'ACTIVE', CURRENT_TIMESTAMP),
('LPT-04', 'MAY_TINH', 'Máy tính xách tay', 'THIET_BI_CNTT', 'ACTIVE', CURRENT_TIMESTAMP),
('LPT-05', 'MUC_IN', 'Hộp mực in', 'KHAC', 'ACTIVE', CURRENT_TIMESTAMP);

-- 2.6. pt_cong_dan
INSERT INTO pt_cong_dan (id, so_cccd, ho_ten, ngay_sinh, gioi_tinh, que_quan, id_dan_toc, id_ton_giao, id_dghc_thuong_tru, dia_chi_thuong_tru, id_dghc_hien_tai, dia_chi_hien_tai) VALUES
('CD-01', '001200300401', 'Nguyễn Văn An', '1990-05-15', 'NAM', 'Hà Nội', 'DT-01', 'TG-01', 'DG-01', 'Quận Ba Đình, Hà Nội', 'DG-01', 'Quận Ba Đình, Hà Nội'),
('CD-02', '001200300402', 'Trần Thị Bình', '1992-08-20', 'NU', 'Nam Định', 'DT-01', 'TG-01', 'DG-02', 'TP Nam Định', 'DG-01', 'Quận Cầu Giấy, Hà Nội'),
('CD-03', '001200300403', 'Lê Hữu Cảnh', '1985-12-10', 'NAM', 'Thái Bình', 'DT-01', 'TG-01', 'DG-03', 'Huyện Vũ Thư, Thái Bình', 'DG-03', 'Huyện Vũ Thư, Thái Bình'),
('CD-04', '001200300404', 'Phạm Quỳnh Dung', '1995-02-28', 'NU', 'Hải Phòng', 'DT-01', 'TG-01', 'DG-04', 'Quận Lê Chân, Hải Phòng', 'DG-04', 'Quận Lê Chân, Hải Phòng'),
('CD-05', '001200300405', 'Hoàng Văn Em', '1988-11-05', 'NAM', 'Hà Nội', 'DT-01', 'TG-01', 'DG-05', 'Quận Đống Đa, Hà Nội', 'DG-05', 'Quận Đống Đa, Hà Nội');

-- 2.7. pt_can_bo_chien_si
INSERT INTO pt_can_bo_chien_si (id, ma_can_bo, cap_bac, chuc_vu, id_don_vi, id_cong_dan) VALUES
('CB-01', 'CANBO-001', 'Đại úy', 'Trưởng công an phường', 'DV-01', 'CD-01'),
('CB-02', 'CANBO-002', 'Thượng úy', 'Thủ kho', 'DV-01', 'CD-02'),
('CB-03', 'CANBO-003', 'Trung úy', 'Cán bộ', 'DV-01', 'CD-03'),
('CB-04', 'CANBO-004', 'Thiếu tá', 'Phó trưởng phòng', 'DV-02', 'CD-04'),
('CB-05', 'CANBO-005', 'Đại úy', 'Cán bộ', 'DV-02', 'CD-05');

-- 2.8. pt_tai_san
INSERT INTO pt_tai_san (id, ma_tai_san, ten_tai_san, id_loai_tai_san, thong_tin_chi_tiet, tinh_trang, id_don_vi_quan_ly, id_can_bo_su_dung, id_nguoi_cap_phat, ngay_cap_phat) VALUES
('TS-01', 'OTO-29A1234', 'Xe Ford Ranger', 'LPT-01', NULL, 'TOT', 'DV-01', NULL, NULL, NULL),
('TS-02', 'XM-29B5678', 'Xe Honda Winner', 'LPT-02', '{"dungTich": "150cc"}', 'TOT', NULL, 'CB-01', 'CB-02', '2024-01-10'),
('TS-03', 'VK-K54-001', 'Súng K54', 'LPT-03', NULL, 'TOT', NULL, 'CB-04', 'CB-05', '2024-03-20'),
('TS-04', 'MT-DELL-01', 'Laptop Dell Latitude', 'LPT-04', '{"ram": "16GB", "cpu": "Core i7"}', 'HONG', NULL, 'CB-03', 'CB-02', '2024-02-15'),
('TS-05', 'MI-HP-01', 'Mực in HP 85A', 'LPT-05', '{"soLuong": "10"}', 'TOT', 'DV-01', NULL, NULL, NULL);

INSERT INTO pt_tai_san_phuong_tien (id, bien_so, so_khung, so_may, nhan_hieu) VALUES
('TS-01', '29A-123.45', 'FR123456', 'M9876', 'Ford'),
('TS-02', '29B-567.89', NULL, NULL, 'Honda');

INSERT INTO pt_tai_san_vu_khi (id, so_hieu, nam_san_xuat) VALUES
('TS-03', 'K54-001', '2015');

INSERT INTO pt_tai_san_thiet_bi_cntt (id, so_serial, dia_chi_mac, cau_hinh) VALUES
('TS-04', 'DL-123456', '00:1B:44:11:3A:B7', 'Core i7, 16GB RAM');

-- 2.9. pt_bien_ban_ban_giao
INSERT INTO pt_bien_ban_ban_giao (id, so_bien_ban, ngay_ban_giao, loai_ben_nhan, id_ben_nhan, loai_ben_giao, id_ben_giao, nguoi_lap_id, nguoi_ky_id, trang_thai) VALUES
('BB-01', 'BB-2024-001', '2024-01-10', 'CAN_BO', 'CB-01', 'DON_VI', 'DV-01', 'CB-02', 'CB-02', 'DA_KY'),
('BB-02', 'BB-2024-002', '2024-02-15', 'CAN_BO', 'CB-03', 'DON_VI', 'DV-01', 'CB-02', 'CB-02', 'DA_KY'),
('BB-03', 'BB-2024-003', '2024-03-20', 'CAN_BO', 'CB-04', 'DON_VI', 'DV-02', 'CB-05', 'CB-05', 'DA_KY'),
('BB-04', 'BB-2024-004', '2024-04-25', 'DON_VI', 'DV-01', 'KHO', 'KHO_TONG', 'CB-02', NULL, 'NHAP'),
('BB-05', 'BB-2024-005', '2024-05-30', 'CAN_BO', 'CB-05', 'DON_VI', 'DV-02', 'CB-04', NULL, 'NHAP');

-- 2.10. pt_chi_tiet_bien_ban
INSERT INTO pt_chi_tiet_bien_ban (id, bien_ban_id, tai_san_id, tinh_trang_luc_giao, ghi_chu) VALUES
('CT-01', 'BB-01', 'TS-02', 'TOT', 'Giao xe đi tuần tra'),
('CT-02', 'BB-02', 'TS-04', 'TOT', 'Cấp laptop làm việc'),
('CT-03', 'BB-03', 'TS-03', 'TOT', 'Giao vũ khí'),
('CT-04', 'BB-04', 'TS-01', 'TOT', 'Nhập ô tô về đội'),
('CT-05', 'BB-04', 'TS-05', 'TOT', 'Nhập 10 hộp mực'),
('CT-06', 'BB-05', 'TS-03', 'TOT', 'Điều chuyển vũ khí nội bộ');
