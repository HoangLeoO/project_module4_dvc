use egov_db;

-- =================================================================
-- TRIGGER: trg_update_land_owner
-- MỤC ĐÍCH: Tự động cập nhật chủ sở hữu đất khi hồ sơ "Biến động đất đai"
--           (DD01_BIENDONG) được phê duyệt (APPROVED).
-- =================================================================

DROP TRIGGER IF EXISTS trg_update_land_owner;
DELIMITER $$
CREATE TRIGGER trg_update_land_owner
    AFTER UPDATE ON ops_dossiers
    FOR EACH ROW
BEGIN
    DECLARE v_service_code VARCHAR(50);
    DECLARE v_land_cert_no VARCHAR(50);
    DECLARE v_new_owner_cccd VARCHAR(12);
    DECLARE v_new_owner_id BIGINT;

    -- 1. Chỉ thực hiện khi trạng thái chuyển sang APPROVED
    IF OLD.dossier_status <> NEW.dossier_status AND NEW.dossier_status = 'APPROVED' THEN

        -- 2. Lấy Service Code
        SELECT s.service_code INTO v_service_code
        FROM cat_services s
        WHERE s.id = NEW.service_id;

        -- 3. Chỉ xử lý cho dịch vụ Biến động đất đai (DD01_BIENDONG)
        IF v_service_code = 'DD01_BIENDONG' THEN

            -- 4. Trích xuất dữ liệu từ JSON form_data
            --    newOwnerCccd: Số định danh của chủ mới
            --    landCertificateNumber: Số GCN của thửa đất cần thay đổi
            --    Lưu ý: Dùng JSON_UNQUOTE(JSON_EXTRACT(...)) hoặc toán tử ->>
            SET v_new_owner_cccd = NEW.form_data ->> '$.newOwnerCccd';
            SET v_land_cert_no = NEW.form_data ->> '$.landCertificateNumber';

            -- 5. Tìm ID của chủ sở hữu mới trong bảng mock_citizens
            SELECT id INTO v_new_owner_id
            FROM mock_citizens
            WHERE cccd = v_new_owner_cccd;

            -- 6. Thực hiện cập nhật nếu tìm thấy chủ mới và số GCN hợp lệ
            IF v_new_owner_id IS NOT NULL AND v_land_cert_no IS NOT NULL THEN
                UPDATE mock_lands
                SET owner_id = v_new_owner_id
                WHERE land_certificate_number = v_land_cert_no;
            END IF;

        END IF;
    END IF;
END$$
DELIMITER ;
