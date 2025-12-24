use egov_db;
-- =======================================================
-- TẠO TRIGGER GỬI THÔNG BÁO TỰ ĐỘNG (Role-Based)
-- =======================================================
DROP TRIGGER IF EXISTS trg_notify_applicant;
DELIMITER $$
CREATE TRIGGER trg_notify_applicant
    AFTER UPDATE ON ops_dossiers
    FOR EACH ROW
BEGIN
    DECLARE v_service_name VARCHAR(255);
    DECLARE v_service_code VARCHAR(50);
    DECLARE v_message TEXT;
    DECLARE v_title VARCHAR(200);

    -- Lấy thông tin dịch vụ (dùng chung)
    SELECT s.service_name, s.service_code
    INTO v_service_name, v_service_code
    FROM cat_services s
    WHERE s.id = NEW.service_id;

    -- 1. THÔNG BÁO CHO CÔNG DÂN (APPLICANT)
    -- Chỉ gửi khi trạng thái thay đổi
    IF OLD.dossier_status <> NEW.dossier_status THEN

        -- Reset biến
        SET v_title = NULL;
        SET v_message = NULL;

        -- Logic xác định nội dung (như cũ)
        CASE NEW.dossier_status
            /* ================= PHÊ DUYỆT ================= */
            WHEN 'APPROVED' THEN
                SET v_title = 'Hồ sơ đã được phê duyệt';
                SET v_message = CASE v_service_code
                                    WHEN 'HK01_TRE' THEN 'Hồ sơ đăng ký khai sinh, thường trú và cấp thẻ BHYT cho trẻ em đã được phê duyệt.'
                                    WHEN 'HT01_KETHON' THEN 'Hồ sơ đăng ký kết hôn đã được phê duyệt. Vui lòng chuẩn bị để nhận giấy chứng nhận.'
                                    WHEN 'HK02_KAITU' THEN 'Hồ sơ đăng ký khai tử và giải quyết chế độ mai táng đã được phê duyệt.'
                                    WHEN 'HT02_XACNHANHN' THEN 'Yêu cầu cấp Giấy xác nhận tình trạng hôn nhân đã được phê duyệt.'
                                    WHEN 'DD01_BIENDONG' THEN 'Hồ sơ đăng ký biến động đất đai đã được phê duyệt.'
                                    WHEN 'DD02_CHUYENMDSD' THEN 'Hồ sơ chuyển mục đích sử dụng đất đã được phê duyệt.'
                                    WHEN 'DD03_TACHHOP' THEN 'Hồ sơ tách thừa/hợp thửa đất đã được phê duyệt.'
                                    WHEN 'KD01_HKD' THEN 'Hồ sơ đăng ký thành lập hộ kinh doanh đã được phê duyệt.'
                                    ELSE CONCAT('Hồ sơ ', NEW.dossier_code, ' thuộc dịch vụ "', v_service_name, '" đã được phê duyệt thành công.')
                    END;

            /* ================= TRẢ KẾT QUẢ ================= */
            WHEN 'RESULT_RETURNED' THEN
                SET v_title = 'Đã có kết quả hồ sơ';
                SET v_message = CASE v_service_code
                                    WHEN 'HK01_TRE' THEN 'Đã trả Giấy khai sinh, thông tin thường trú và thẻ BHYT cho trẻ em. Vui lòng nhận kết quả.'
                                    WHEN 'HT01_KETHON' THEN 'Đã trả Giấy chứng nhận kết hôn. Chúc mừng hạnh phúc!'
                                    WHEN 'HK02_KAITU' THEN 'Đã trả Giấy khai tử và kết quả giải quyết chế độ. Xin chia buồn cùng gia đình.'
                                    WHEN 'HT02_XACNHANHN' THEN 'Đã trả Giấy xác nhận tình trạng hôn nhân.'
                                    WHEN 'DD01_BIENDONG' THEN 'Kết quả đăng ký biến động đất đai đã sẵn sàng. Vui lòng nhận bản chính.'
                                    WHEN 'DD02_CHUYENMDSD' THEN 'Quyết định cho phép chuyển mục đích sử dụng đất đã được trả.'
                                    WHEN 'DD03_TACHHOP' THEN 'Kết quả tách/hợp thửa đất đã được trả.'
                                    WHEN 'KD01_HKD' THEN 'Giấy chứng nhận đăng ký hộ kinh doanh đã được trả.'
                                    ELSE CONCAT('Kết quả của hồ sơ ', NEW.dossier_code, ' đã được trả. Vui lòng đến nhận hoặc kiểm tra trực tuyến.')
                    END;

            /* ================= CÁC TRẠNG THÁI KHÁC ================= */
            WHEN 'REJECTED' THEN
                SET v_title = 'Hồ sơ bị từ chối';
                SET v_message = CONCAT('Hồ sơ ', NEW.dossier_code, ' đã bị từ chối. Lý do: ', COALESCE(NEW.rejection_reason, 'Không có lý do cụ thể.'));

            WHEN 'SUPPLEMENT_REQUIRED' THEN
                SET v_title = 'Yêu cầu bổ sung hồ sơ';
                SET v_message = CONCAT('Hồ sơ ', NEW.dossier_code, ' cần được bổ sung thông tin/giấy tờ. Vui lòng kiểm tra lại.');

            WHEN 'PENDING' THEN
                SET v_title = 'Hồ sơ đang được xử lý';
                SET v_message = CONCAT('Hồ sơ ', NEW.dossier_code, ' đã được tiếp nhận và đang trong quá trình xử lý.');

            WHEN 'VERIFIED' THEN
                SET v_title = 'Hồ sơ đã qua thẩm định';
                SET v_message = CONCAT('Hồ sơ ', NEW.dossier_code, ' đã hoàn thành bước thẩm định và đang chờ phê duyệt.');

            ELSE
                SET v_title = 'Cập nhật trạng thái hồ sơ';
                SET v_message = CONCAT('Hồ sơ ', NEW.dossier_code, ' đã chuyển sang trạng thái: ', NEW.dossier_status);
            END CASE;

        -- Insert thông báo cho CÔNG DÂN
        IF v_title IS NOT NULL THEN
            INSERT INTO mod_notifications (user_id, title, message, type, is_read, created_at)
            VALUES (NEW.applicant_id, v_title, v_message, 'STATUS_UPDATE', FALSE, NOW());
        END IF;

    END IF;

    -- 2. THÔNG BÁO CHO CÁN BỘ (OFFICIAL/HANDLER)
    -- Gửi khi current_handler_id thay đổi (được phân công)
    IF (OLD.current_handler_id IS NULL AND NEW.current_handler_id IS NOT NULL)
        OR (OLD.current_handler_id <> NEW.current_handler_id) THEN

        -- Insert thông báo cho CÁN BỘ
        INSERT INTO mod_notifications (user_id, title, message, type, is_read, created_at)
        VALUES (
                   NEW.current_handler_id,
                   'Tiếp nhận xử lý hồ sơ',
                   CONCAT('Bạn vừa được phân công xử lý hồ sơ ', NEW.dossier_code, ' (', v_service_name, '). Trạng thái hiện tại: ', NEW.dossier_status),
                   'TASK_ASSIGNMENT',
                   FALSE,
                   NOW()
               );
    END IF;

END$$
DELIMITER ;
