package com.gtelict.phuong_tien_api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

// @Component (Vô hiệu hóa để không chạy tự động và sinh rác schema)
public class DatabaseMigrationRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Running Database Migration Script...");

        try {
            // Thêm cột trang_thai
            jdbcTemplate.execute("ALTER TABLE pt_bien_ban_ban_giao ADD COLUMN IF NOT EXISTS trang_thai VARCHAR(50) DEFAULT 'NHAP';");
            System.out.println("Cột trang_thai đã được thêm thành công.");
            
            // Cập nhật các bản ghi cũ
            jdbcTemplate.execute("UPDATE pt_bien_ban_ban_giao SET trang_thai = 'DA_KY' WHERE trang_thai IS NULL;");
            
            // Thêm cột nguoi_lap_id
            jdbcTemplate.execute("ALTER TABLE pt_bien_ban_ban_giao ADD COLUMN IF NOT EXISTS nguoi_lap_id VARCHAR(255);");
            System.out.println("Cột nguoi_lap_id đã được thêm thành công.");

            // Thêm Khóa ngoại cho nguoi_ky_id
            jdbcTemplate.execute("DO $$ \n" +
                    "BEGIN \n" +
                    "    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_nguoi_ky_id') THEN \n" +
                    "        ALTER TABLE pt_bien_ban_ban_giao ADD CONSTRAINT fk_nguoi_ky_id FOREIGN KEY (nguoi_ky_id) REFERENCES pt_can_bo_chien_si(id); \n" +
                    "    END IF; \n" +
                    "END $$;");
            System.out.println("Khóa ngoại fk_nguoi_ky_id đã được đảm bảo.");

            // Thêm Khóa ngoại cho nguoi_lap_id
            jdbcTemplate.execute("DO $$ \n" +
                    "BEGIN \n" +
                    "    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_nguoi_lap_id') THEN \n" +
                    "        ALTER TABLE pt_bien_ban_ban_giao ADD CONSTRAINT fk_nguoi_lap_id FOREIGN KEY (nguoi_lap_id) REFERENCES pt_can_bo_chien_si(id); \n" +
                    "    END IF; \n" +
                    "END $$;");
            System.out.println("Khóa ngoại fk_nguoi_lap_id đã được đảm bảo.");

        } catch (Exception e) {
            System.err.println("Migration failed (maybe already migrated or syntax issue): " + e.getMessage());
        }
    }
}
