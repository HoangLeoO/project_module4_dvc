use egov_db;
-- =======================================================
-- TẠO TRIGGER GHI LOG KHI CẬP NHẬT TRẠNG THÁI HỒ SƠ
-- =======================================================
DROP TRIGGER IF EXISTS trg_ops_dossiers_status_log;
DELIMITER $$
CREATE TRIGGER trg_ops_dossiers_status_log
    AFTER UPDATE ON ops_dossiers
    FOR EACH ROW
BEGIN
    DECLARE v_service_code VARCHAR(50);
    DECLARE v_comment TEXT;
    -- Chỉ log khi trạng thái thay đổi
    IF OLD.dossier_status <> NEW.dossier_status THEN
        -- Lấy service_code của hồ sơ
        SELECT s.service_code
        INTO v_service_code
        FROM cat_services s
        WHERE s.id = NEW.service_id;
        -- Sinh comment theo dịch vụ + trạng thái
        SET v_comment = CASE
            /* ================= PHÊ DUYỆT ================= */
                            WHEN NEW.dossier_status = 'APPROVED' THEN
                                CASE v_service_code
                                    WHEN 'HK01_TRE' THEN 'Phê duyệt hồ sơ đăng ký khai sinh, thường trú và cấp thẻ BHYT cho trẻ em'
                                    WHEN 'HT01_KETHON' THEN 'Phê duyệt hồ sơ đăng ký kết hôn'
                                    WHEN 'HK02_KAITU' THEN 'Phê duyệt hồ sơ đăng ký khai tử và giải quyết chế độ mai táng, tử tuất'
                                    WHEN 'HT02_XACNHANHN' THEN 'Phê duyệt cấp Giấy xác nhận tình trạng hôn nhân'
                                    WHEN 'DD01_BIENDONG' THEN 'Phê duyệt hồ sơ đăng ký biến động đất đai'
                                    WHEN 'DD02_CHUYENMDSD' THEN 'Phê duyệt hồ sơ chuyển mục đích sử dụng đất'
                                    WHEN 'DD03_TACHHOP' THEN 'Phê duyệt hồ sơ tách thửa/hợp thửa đất'
                                    WHEN 'KD01_HKD' THEN 'Phê duyệt hồ sơ đăng ký thành lập hộ kinh doanh'
                                    ELSE 'Phê duyệt hồ sơ'
                                    END
            /* ================= TRẢ KẾT QUẢ ================= */
                            WHEN NEW.dossier_status = 'RESULT_RETURNED' THEN
                                CASE v_service_code
                                    WHEN 'HK01_TRE' THEN 'Đã trả Giấy khai sinh, thông tin thường trú và thẻ BHYT cho trẻ em'
                                    WHEN 'HT01_KETHON' THEN 'Đã trả Giấy chứng nhận kết hôn'
                                    WHEN 'HK02_KAITU' THEN 'Đã trả Giấy khai tử và kết quả giải quyết chế độ mai táng'
                                    WHEN 'HT02_XACNHANHN' THEN 'Đã trả Giấy xác nhận tình trạng hôn nhân'
                                    WHEN 'DD01_BIENDONG' THEN 'Đã trả kết quả đăng ký biến động đất đai'
                                    WHEN 'DD02_CHUYENMDSD' THEN 'Đã trả quyết định cho phép chuyển mục đích sử dụng đất'
                                    WHEN 'DD03_TACHHOP' THEN 'Đã trả kết quả tách thửa/hợp thửa đất'
                                    WHEN 'KD01_HKD' THEN 'Đã trả Giấy chứng nhận đăng ký hộ kinh doanh'
                                    ELSE 'Đã trả kết quả hồ sơ'
                                    END
            /* ================= THẨM ĐỊNH ================= */
                            WHEN NEW.dossier_status = 'VERIFIED' THEN
                                'Đã hoàn thành thẩm định hồ sơ'
            /* ================= CHUYỂN BƯỚC ================= */
                            WHEN NEW.dossier_status = 'PENDING' THEN
                                'Chuyển hồ sơ sang bước xử lý tiếp theo'
            /* ================= TỪ CHỐI ================= */
                            WHEN NEW.dossier_status = 'REJECTED' THEN
                                COALESCE(NEW.rejection_reason, 'Từ chối giải quyết hồ sơ')
            /* ================= YÊU CẦU BỔ SUNG ================= */
                            WHEN NEW.dossier_status = 'SUPPLEMENT_REQUIRED' THEN
                                'Yêu cầu bổ sung hồ sơ'
                            ELSE
                                'Cập nhật trạng thái hồ sơ'
            END;
        -- Ghi log
        -- Lưu ý: actor_id lấy từ current_handler_id của bản ghi c (người vừa nhận xử lý hoặc người vừa thực hiện)
        -- Tùy logic nghiệp vụ:
        -- Nếu update set current_handler_id = Người tiếp theo, thì log này nên ghi actor là người thực hiện (OLD.current_handler_id)
        -- Tuy nhiên, trong ngữ cảnh trigger đơn giản này, ta tạm dùng NEW.current_handler_id hoặc cần logic phức tạp hơn để xác định "Who did this".
        -- Ở đây ta giả định người thực hiện hành động chính là người được gán trong câu UPDATE (nếu hệ thống truyền vào)
        -- HOẶC tốt nhất là ứng dụng nên insert log. Trigger chỉ là giải pháp backup.
        INSERT INTO ops_dossier_logs
        (
            dossier_id,
            actor_id,
            action,
            prev_status,
            next_status,
            comments
        )
        VALUES
            (
                NEW.id,
                -- Logic fix: Khi Approve, người thực hiện là người đang giữ hồ sơ (OLD handler) chứ không phải người nhận tiếp theo
                -- Nhưng nếu câu update thay đổi cả handler, thì OLD.current_handler_id là người vừa xử lý xong.
                -- Logic fix: Khi Approve, người thực hiện là người đang giữ hồ sơ (OLD handler) chứ không phải người nhận tiếp theo
                -- Nhưng nếu câu update thay đổi cả handler, thì OLD.current_handler_id là người vừa xử lý xong.
                -- Fix: Ensure actor_id is not null by coalescing
                COALESCE(OLD.current_handler_id, NEW.current_handler_id, NEW.applicant_id),
                CASE
                    WHEN NEW.dossier_status = 'PENDING'         THEN 'CHUYEN_BUOC'
                    WHEN NEW.dossier_status = 'VERIFIED'        THEN 'THAM_DINH'
                    WHEN NEW.dossier_status = 'APPROVED'        THEN 'PHE_DUYET'
                    WHEN NEW.dossier_status = 'RESULT_RETURNED' THEN 'TRA_KET_QUA'
                    WHEN NEW.dossier_status = 'REJECTED'        THEN 'TU_CHOI'
                    WHEN NEW.dossier_status = 'SUPPLEMENT_REQUIRED' THEN 'YEU_CAU_BO_SUNG'
                    ELSE 'CAP_NHAT_TRANG_THAI'
                    END,
                OLD.dossier_status,
                NEW.dossier_status,
                v_comment
            );
    END IF;
END$$
DELIMITER ;