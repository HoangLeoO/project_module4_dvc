USE egov_db;

-- =======================================================
-- TRIGGER CẬP NHẬT TRẠNG THÁI KẾT HÔN
-- Khi hồ sơ kết hôn được phê duyệt (APPROVED):
-- 1. Cập nhật marital_status = 'MARRIED' cho cả hai bên
-- 2. Liên kết spouse_id giữa hai bên
-- 3. Tạo quan hệ trong bảng mock_citizen_relationships
-- =======================================================

DROP TRIGGER IF EXISTS trg_marriage_registration_sync;
DELIMITER $$

CREATE TRIGGER trg_marriage_registration_sync
    AFTER UPDATE ON ops_dossiers
    FOR EACH ROW
BEGIN
    DECLARE v_service_code VARCHAR(50);
    DECLARE v_husband_cccd VARCHAR(12);
    DECLARE v_wife_cccd VARCHAR(12);
    DECLARE v_husband_id BIGINT;
    DECLARE v_wife_id BIGINT;
    DECLARE v_marriage_date DATE;
    DECLARE v_marriage_date_str VARCHAR(20);

    -- Chỉ thực hiện khi trạng thái chuyển sang APPROVED
    IF NEW.dossier_status = 'APPROVED' AND OLD.dossier_status <> 'APPROVED' THEN

        -- Lấy service_code của hồ sơ
        SELECT s.service_code
        INTO v_service_code
        FROM cat_services s
        WHERE s.id = NEW.service_id;

        -- Chỉ xử lý nếu là dịch vụ Đăng ký kết hôn (HT01_KETHON)
        IF v_service_code = 'HT01_KETHON' THEN

            -- Trích xuất thông tin CCCD từ JSON form_data
            SET v_husband_cccd = JSON_UNQUOTE(JSON_EXTRACT(NEW.form_data, '$.husbandIdNumber'));
            SET v_wife_cccd = JSON_UNQUOTE(JSON_EXTRACT(NEW.form_data, '$.wifeIdNumber'));
            SET v_marriage_date_str = JSON_UNQUOTE(JSON_EXTRACT(NEW.form_data, '$.intendedMarriageDate'));

            -- Chuyển đổi định dạng ngày tháng (nếu có)
            IF v_marriage_date_str IS NOT NULL AND v_marriage_date_str <> 'null' THEN
                SET v_marriage_date = STR_TO_DATE(v_marriage_date_str, '%Y-%m-%d');
            ELSE
                SET v_marriage_date = CURDATE();
            END IF;

            -- Tìm ID của chồng và vợ từ bảng mock_citizens
            IF v_husband_cccd IS NOT NULL AND v_husband_cccd <> '' AND v_husband_cccd <> 'null' THEN
                SELECT id INTO v_husband_id
                FROM mock_citizens
                WHERE cccd = TRIM(v_husband_cccd)
                LIMIT 1;
            END IF;

            IF v_wife_cccd IS NOT NULL AND v_wife_cccd <> '' AND v_wife_cccd <> 'null' THEN
                SELECT id INTO v_wife_id
                FROM mock_citizens
                WHERE cccd = TRIM(v_wife_cccd)
                LIMIT 1;
            END IF;

            -- Chỉ cập nhật nếu tìm thấy cả hai công dân
            IF v_husband_id IS NOT NULL AND v_wife_id IS NOT NULL THEN

                -- ===============================
                -- 1. CẬP NHẬT BẢNG mock_citizens
                -- ===============================
                
                -- Cập nhật trạng thái hôn nhân và spouse_id cho Người chồng
                UPDATE mock_citizens
                SET marital_status = 'MARRIED',
                    spouse_id = v_wife_id,
                    updated_at = NOW()
                WHERE id = v_husband_id;

                -- Cập nhật trạng thái hôn nhân và spouse_id cho Người vợ
                UPDATE mock_citizens
                SET marital_status = 'MARRIED',
                    spouse_id = v_husband_id,
                    updated_at = NOW()
                WHERE id = v_wife_id;

                -- ===============================================
                -- 2. TẠO QUAN HỆ TRONG mock_citizen_relationships
                -- ===============================================
                
                -- Tạo quan hệ: Vợ là "VO" đối với Chồng
                -- (citizen_id = chồng, relative_id = vợ, relationship_type = 'VO')
                INSERT INTO mock_citizen_relationships (citizen_id, relative_id, relationship_type, created_at, updated_at)
                VALUES (v_husband_id, v_wife_id, 'VO', NOW(), NOW())
                ON DUPLICATE KEY UPDATE 
                    relationship_type = 'VO',
                    updated_at = NOW();

                -- Tạo quan hệ: Chồng là "CHONG" đối với Vợ
                -- (citizen_id = vợ, relative_id = chồng, relationship_type = 'CHONG')
                INSERT INTO mock_citizen_relationships (citizen_id, relative_id, relationship_type, created_at, updated_at)
                VALUES (v_wife_id, v_husband_id, 'CHONG', NOW(), NOW())
                ON DUPLICATE KEY UPDATE 
                    relationship_type = 'CHONG',
                    updated_at = NOW();

            END IF;

        END IF;
    END IF;
END$$

DELIMITER ;
