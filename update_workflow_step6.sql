USE egov_db;

-- =======================================================
-- BỔ SUNG BƯỚC 6: CÔNG DÂN XÁC NHẬN KẾT QUẢ
-- Áp dụng cho dịch vụ Khai sinh (HK01_TRE)
-- =======================================================

-- 1. Thêm bước 6 vào quy trình HK01_TRE
-- Lưu ý: role_required_id là NULL hoặc role của công dân (nếu có hệ thống phân quyền cụ thể)
-- Ở đây ta dùng NULL vì bước này do chính công dân thực hiện.
INSERT INTO cat_workflow_steps (service_id, step_name, step_order, role_required_id)
SELECT id, 'Công dân xác nhận thông tin', 6, NULL 
FROM cat_services 
WHERE service_code = 'HK01_TRE';

-- [Tùy chọn] Cập nhật cho HS-KS-TRE nếu có
INSERT INTO cat_workflow_steps (service_id, step_name, step_order, role_required_id)
SELECT id, 'Công dân xác nhận thông tin', 6, NULL 
FROM cat_services 
WHERE service_code = 'HS-KS-TRE';

-- =======================================================
-- KIỂM TRA LẠI CÁC BƯỚC
-- SELECT * FROM cat_workflow_steps WHERE service_id IN (SELECT id FROM cat_services WHERE service_code IN ('HK01_TRE', 'HS-KS-TRE')) ORDER BY step_order;
-- =======================================================
