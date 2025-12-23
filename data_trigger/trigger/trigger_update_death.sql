use egov_db;
-- =======================================================
-- TẠO TRIGGER CẬP NHẬT TRẠNG THÁI KHAI TỬ
-- =======================================================
DROP TRIGGER IF EXISTS trg_update_death_status;
DELIMITER $$
CREATE TRIGGER trg_update_death_status
    AFTER UPDATE ON ops_dossiers
    FOR EACH ROW
BEGIN
    DECLARE v_service_code VARCHAR(50);
    DECLARE v_deceased_name VARCHAR(100);
    DECLARE v_deceased_dob DATE;
    DECLARE v_deceased_dob_str VARCHAR(20);
    DECLARE v_deceased_cccd VARCHAR(12);

    -- Chỉ thực hiện khi trạng thái chuyển sang APPROVED
    IF NEW.dossier_status = 'APPROVED' AND OLD.dossier_status <> 'APPROVED' THEN

        -- Lấy service_code của hồ sơ
        SELECT s.service_code
        INTO v_service_code
        FROM cat_services s
        WHERE s.id = NEW.service_id;

        -- Chỉ xử lý nếu là dịch vụ Khai tử (HK02_KAITU)
        IF v_service_code = 'HK02_KAITU' THEN

            -- Trích xuất thông tin từ JSON form_data
            SET v_deceased_name = JSON_UNQUOTE(JSON_EXTRACT(NEW.form_data, '$.deceasedFullName'));
            SET v_deceased_dob_str = JSON_UNQUOTE(JSON_EXTRACT(NEW.form_data, '$.dateOfBirth'));
            SET v_deceased_cccd = JSON_UNQUOTE(JSON_EXTRACT(NEW.form_data, '$.deceasedIdNumber'));

            -- Chuyển đổi định dạng ngày tháng
            SET v_deceased_dob = STR_TO_DATE(v_deceased_dob_str, '%Y-%m-%d');

            -- Ưu tiên cập nhật theo CCCD nếu có
            IF v_deceased_cccd IS NOT NULL AND v_deceased_cccd <> '' THEN
                UPDATE mock_citizens
                SET is_deceased = TRUE
                WHERE cccd = v_deceased_cccd;
                -- Nếu không có CCCD thì fallback sang Tên + Ngày sinh
            ELSEIF v_deceased_name IS NOT NULL AND v_deceased_dob IS NOT NULL THEN
                UPDATE mock_citizens
                SET is_deceased = TRUE
                WHERE full_name = v_deceased_name
                  AND dob = v_deceased_dob;
            END IF;

        END IF;
    END IF;
END$$
DELIMITER ;
