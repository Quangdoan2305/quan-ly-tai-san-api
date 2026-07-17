-- =======================================================
-- KỊCH BẢN KHỞI TẠO CƠ SỞ DỮ LIỆU (POSTGRESQL)
-- Xóa bảng cũ nếu tồn tại (để làm mới hoàn toàn)
-- =======================================================

DROP TABLE IF EXISTS pt_chi_tiet_bien_ban CASCADE;
DROP TABLE IF EXISTS pt_bien_ban_ban_giao CASCADE;
DROP TABLE IF EXISTS pt_tai_san CASCADE;
DROP TABLE IF EXISTS pt_can_bo_chien_si CASCADE;
DROP TABLE IF EXISTS pt_cong_dan CASCADE;
DROP TABLE IF EXISTS pt_danh_muc CASCADE;

-- =======================================================
-- 1. TẠO BẢNG
-- =======================================================

CREATE TABLE pt_cong_dan (
    id VARCHAR(50) PRIMARY KEY,
    so_cccd VARCHAR(20) UNIQUE NOT NULL,
    ho_ten VARCHAR(255) NOT NULL,
    ngay_sinh DATE,
    gioi_tinh VARCHAR(10),
    que_quan TEXT,
    ma_dan_toc VARCHAR(50),
    ma_ton_giao VARCHAR(50),
    ma_dghc_thuong_tru VARCHAR(50),
    dia_chi_thuong_tru TEXT,
    ma_dghc_hien_tai VARCHAR(50),
    dia_chi_hien_tai TEXT
);

CREATE TABLE pt_can_bo_chien_si (
    id VARCHAR(50) PRIMARY KEY,
    ma_can_bo VARCHAR(50) UNIQUE NOT NULL,
    cap_bac VARCHAR(50),
    chuc_vu VARCHAR(100),
    id_don_vi VARCHAR(50),
    id_cong_dan VARCHAR(50) REFERENCES pt_cong_dan(id)
);

CREATE TABLE pt_danh_muc (
    id VARCHAR(50) PRIMARY KEY,
    ma VARCHAR(50) NOT NULL,
    ten VARCHAR(255) NOT NULL,
    loai VARCHAR(50) NOT NULL,
    parent_id VARCHAR(50),
    cap INTEGER,
    trang_thai VARCHAR(50),
    UNIQUE(ma, loai)
);

CREATE TABLE pt_tai_san (
    id VARCHAR(50) PRIMARY KEY,
    ma_tai_san VARCHAR(50) UNIQUE NOT NULL,
    ten_tai_san VARCHAR(255) NOT NULL,
    id_danh_muc_loai VARCHAR(50) REFERENCES pt_danh_muc(id),
    thong_tin_chi_tiet JSONB,
    tinh_trang VARCHAR(50),
    id_don_vi_quan_ly VARCHAR(50),
    id_can_bo_su_dung VARCHAR(50) REFERENCES pt_can_bo_chien_si(id),
    id_nguoi_cap_phat VARCHAR(50) REFERENCES pt_can_bo_chien_si(id),
    ngay_cap_phat DATE
);

CREATE TABLE pt_bien_ban_ban_giao (
    id VARCHAR(50) PRIMARY KEY,
    so_bien_ban VARCHAR(50) UNIQUE NOT NULL,
    ngay_ban_giao DATE,
    loai_ben_nhan VARCHAR(50),
    id_ben_nhan VARCHAR(50),
    loai_ben_giao VARCHAR(50),
    id_ben_giao VARCHAR(50),
    nguoi_lap_id VARCHAR(50) REFERENCES pt_can_bo_chien_si(id),
    nguoi_ky_id VARCHAR(50) REFERENCES pt_can_bo_chien_si(id),
    trang_thai VARCHAR(20) DEFAULT 'NHAP',
    file_dinh_kem TEXT
);

CREATE TABLE pt_chi_tiet_bien_ban (
    id VARCHAR(50) PRIMARY KEY,
    bien_ban_id VARCHAR(50) REFERENCES pt_bien_ban_ban_giao(id),
    tai_san_id VARCHAR(50) REFERENCES pt_tai_san(id),
    tinh_trang_luc_giao VARCHAR(50),
    ghi_chu TEXT
);

-- =======================================================
-- 2. INSERT DỮ LIỆU MẪU (MOCK DATA)
-- =======================================================

-- 2.1. pt_cong_dan
INSERT INTO pt_cong_dan (id, so_cccd, ho_ten, ngay_sinh, gioi_tinh, que_quan, ma_dan_toc, ma_ton_giao, ma_dghc_thuong_tru, dia_chi_thuong_tru, ma_dghc_hien_tai, dia_chi_hien_tai) VALUES
('CD-01', '001200300401', 'Nguyễn Văn An', '1990-05-15', 'NAM', 'Hà Nội', 'KINH', 'KHONG', 'HN_001', 'Quận Ba Đình, Hà Nội', 'HN_001', 'Quận Ba Đình, Hà Nội'),
('CD-02', '001200300402', 'Trần Thị Bình', '1992-08-20', 'NU', 'Nam Định', 'KINH', 'KHONG', 'ND_002', 'TP Nam Định', 'HN_002', 'Quận Cầu Giấy, Hà Nội'),
('CD-03', '001200300403', 'Lê Hữu Cảnh', '1985-12-10', 'NAM', 'Thái Bình', 'KINH', 'KHONG', 'TB_003', 'Huyện Vũ Thư, Thái Bình', 'TB_003', 'Huyện Vũ Thư, Thái Bình'),
('CD-04', '001200300404', 'Phạm Quỳnh Dung', '1995-02-28', 'NU', 'Hải Phòng', 'KINH', 'KHONG', 'HP_004', 'Quận Lê Chân, Hải Phòng', 'HP_004', 'Quận Lê Chân, Hải Phòng'),
('CD-05', '001200300405', 'Hoàng Văn Em', '1988-11-05', 'NAM', 'Hà Nội', 'KINH', 'KHONG', 'HN_005', 'Quận Đống Đa, Hà Nội', 'HN_005', 'Quận Đống Đa, Hà Nội');

-- 2.2. pt_can_bo_chien_si
INSERT INTO pt_can_bo_chien_si (id, ma_can_bo, cap_bac, chuc_vu, id_don_vi, id_cong_dan) VALUES
('CB-01', 'CANBO-001', 'Đại úy', 'Trưởng công an phường', 'DV-01', 'CD-01'),
('CB-02', 'CANBO-002', 'Thượng úy', 'Thủ kho', 'DV-01', 'CD-02'),
('CB-03', 'CANBO-003', 'Trung úy', 'Cán bộ', 'DV-01', 'CD-03'),
('CB-04', 'CANBO-004', 'Thiếu tá', 'Phó trưởng phòng', 'DV-02', 'CD-04'),
('CB-05', 'CANBO-005', 'Đại úy', 'Cán bộ', 'DV-02', 'CD-05');

-- 2.3. pt_danh_muc
INSERT INTO pt_danh_muc (id, ma, ten, loai, parent_id, cap, trang_thai) VALUES
('DM-01', 'OTO', 'Ô tô chuyên dụng', 'LOAI_TAI_SAN', NULL, 1, 'HOAT_DONG'),
('DM-02', 'XEMAY', 'Xe mô tô tuần tra', 'LOAI_TAI_SAN', NULL, 1, 'HOAT_DONG'),
('DM-03', 'VU_KHI', 'Súng ngắn', 'LOAI_TAI_SAN', NULL, 1, 'HOAT_DONG'),
('DM-04', 'MAY_TINH', 'Máy tính xách tay', 'LOAI_TAI_SAN', NULL, 1, 'HOAT_DONG'),
('DM-05', 'MUC_IN', 'Hộp mực in', 'LOAI_TAI_SAN', NULL, 1, 'HOAT_DONG'),
('DM-06', 'KINH', 'Dân tộc Kinh', 'DAN_TOC', NULL, 1, 'HOAT_DONG'),
('DM-07', 'KHONG', 'Không tôn giáo', 'TON_GIAO', NULL, 1, 'HOAT_DONG'),
('DM-08', 'HN_001', 'Quận Ba Đình, Hà Nội', 'DIA_GIOI_HANH_CHINH', NULL, 1, 'HOAT_DONG'),
('DM-09', 'ND_002', 'TP Nam Định', 'DIA_GIOI_HANH_CHINH', NULL, 1, 'HOAT_DONG'),
('DM-10', 'TB_003', 'Huyện Vũ Thư, Thái Bình', 'DIA_GIOI_HANH_CHINH', NULL, 1, 'HOAT_DONG'),
('DM-11', 'HP_004', 'Quận Lê Chân, Hải Phòng', 'DIA_GIOI_HANH_CHINH', NULL, 1, 'HOAT_DONG'),
('DM-12', 'HN_005', 'Quận Đống Đa, Hà Nội', 'DIA_GIOI_HANH_CHINH', NULL, 1, 'HOAT_DONG');

-- 2.4. pt_tai_san
INSERT INTO pt_tai_san (id, ma_tai_san, ten_tai_san, id_danh_muc_loai, thong_tin_chi_tiet, tinh_trang, id_don_vi_quan_ly, id_can_bo_su_dung, id_nguoi_cap_phat, ngay_cap_phat) VALUES
('TS-01', 'OTO-29A1234', 'Xe Ford Ranger 29A-123.45', 'DM-01', '{"bienSo": "29A-123.45", "soKhung": "FR123456", "soMay": "M9876"}', 'TOT', 'DV-01', NULL, NULL, NULL),
('TS-02', 'XM-29B5678', 'Xe Honda Winner 29B-567.89', 'DM-02', '{"bienSo": "29B-567.89", "dungTich": "150cc"}', 'TOT', 'DV-01', 'CB-01', 'CB-02', '2024-01-10'),
('TS-03', 'VK-K54-001', 'Súng K54 số hiệu 001', 'DM-03', '{"soHieu": "K54-001", "namSanXuat": "2015"}', 'TOT', 'DV-02', 'CB-04', 'CB-05', '2024-03-20'),
('TS-04', 'MT-DELL-01', 'Laptop Dell Latitude', 'DM-04', '{"macAddress": "00:1B:44:11:3A:B7", "ram": "16GB", "cpu": "Core i7"}', 'HONG', 'DV-01', 'CB-03', 'CB-02', '2024-02-15'),
('TS-05', 'MI-HP-01', 'Mực in HP 85A', 'DM-05', '{"soLuong": "10"}', 'TOT', 'DV-01', NULL, NULL, NULL);

-- 2.5. pt_bien_ban_ban_giao
INSERT INTO pt_bien_ban_ban_giao (id, so_bien_ban, ngay_ban_giao, loai_ben_nhan, id_ben_nhan, loai_ben_giao, id_ben_giao, nguoi_lap_id, nguoi_ky_id, trang_thai) VALUES
('BB-01', 'BB-2024-001', '2024-01-10', 'CAN_BO', 'CB-01', 'DON_VI', 'DV-01', 'CB-02', 'CB-02', 'DA_KY'),
('BB-02', 'BB-2024-002', '2024-02-15', 'CAN_BO', 'CB-03', 'DON_VI', 'DV-01', 'CB-02', 'CB-02', 'DA_KY'),
('BB-03', 'BB-2024-003', '2024-03-20', 'CAN_BO', 'CB-04', 'DON_VI', 'DV-02', 'CB-05', 'CB-05', 'DA_KY'),
('BB-04', 'BB-2024-004', '2024-04-25', 'DON_VI', 'DV-01', 'KHO', 'KHO_TONG', 'CB-02', NULL, 'NHAP'),
('BB-05', 'BB-2024-005', '2024-05-30', 'CAN_BO', 'CB-05', 'DON_VI', 'DV-02', 'CB-04', NULL, 'NHAP');

-- 2.6. pt_chi_tiet_bien_ban
INSERT INTO pt_chi_tiet_bien_ban (id, bien_ban_id, tai_san_id, tinh_trang_luc_giao, ghi_chu) VALUES
('CT-01', 'BB-01', 'TS-02', 'TOT', 'Giao xe đi tuần tra'),
('CT-02', 'BB-02', 'TS-04', 'TOT', 'Cấp laptop làm việc'),
('CT-03', 'BB-03', 'TS-03', 'TOT', 'Giao vũ khí'),
('CT-04', 'BB-04', 'TS-01', 'TOT', 'Nhập ô tô về đội'),
('CT-05', 'BB-04', 'TS-05', 'TOT', 'Nhập 10 hộp mực'),
('CT-06', 'BB-05', 'TS-03', 'TOT', 'Điều chuyển vũ khí nội bộ');
