-- =====================================================================
-- THÊM DỮ LIỆU ĐÁNH GIÁ TỪ NGƯỜI DÂN
-- =====================================================================

USE egov_db;

-- Thêm nhiều hồ sơ đã hoàn thành để có dữ liệu đánh giá
-- =====================================================================
-- PHƯỜNG HẢI CHÂU - Thêm hồ sơ đã hoàn thành
-- =====================================================================

-- Hồ sơ khai sinh (cán bộ Tư pháp - Phạm Thị Dung)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, 
 dossier_status, submission_date, finish_date, form_data)
SELECT 
    'HS_HC_KS_002', s.id, d.id, app.id, cb.id, 
    'APPROVED', 
    DATE_SUB(NOW(), INTERVAL 10 DAY),
    DATE_SUB(NOW(), INTERVAL 3 DAY),
    JSON_OBJECT('child_name','Trần Thị Mai','dob','2024-12-25')
FROM cat_services s
JOIN sys_users app ON app.username = 'cd_02'
JOIN sys_users cb ON cb.username = 'hc_tp'
JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'HK01_TRE';

INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, 
 dossier_status, submission_date, finish_date, form_data)
SELECT 
    'HS_HC_KS_003', s.id, d.id, app.id, cb.id, 
    'APPROVED', 
    DATE_SUB(NOW(), INTERVAL 15 DAY),
    DATE_SUB(NOW(), INTERVAL 8 DAY),
    JSON_OBJECT('child_name','Lê Văn Đức','dob','2024-12-15')
FROM cat_services s
JOIN sys_users app ON app.username = 'cd_03'
JOIN sys_users cb ON cb.username = 'hc_tp'
JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'HK01_TRE';

-- Hồ sơ xác nhận hôn nhân (cán bộ Tư pháp - Phạm Thị Dung)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, 
 dossier_status, submission_date, finish_date, form_data)
SELECT 
    'HS_HC_XN_002', s.id, d.id, app.id, cb.id, 
    'APPROVED', 
    DATE_SUB(NOW(), INTERVAL 5 DAY),
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    JSON_OBJECT('requester','Lê Văn Cường','purpose','Làm visa')
FROM cat_services s
JOIN sys_users app ON app.username = 'cd_03'
JOIN sys_users cb ON cb.username = 'hc_tp'
JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'HT02_XACNHANHN';

INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, 
 dossier_status, submission_date, finish_date, form_data)
SELECT 
    'HS_HC_XN_003', s.id, d.id, app.id, cb.id, 
    'APPROVED', 
    DATE_SUB(NOW(), INTERVAL 7 DAY),
    DATE_SUB(NOW(), INTERVAL 4 DAY),
    JSON_OBJECT('requester','Phạm Thị Dung','purpose','Mua nhà')
FROM cat_services s
JOIN sys_users app ON app.username = 'cd_04'
JOIN sys_users cb ON cb.username = 'hc_tp'
JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'HT02_XACNHANHN';

-- Hồ sơ đất đai (cán bộ Địa chính - Hoàng Văn Em)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, 
 dossier_status, submission_date, finish_date, form_data)
SELECT 
    'HS_HC_DD_002', s.id, d.id, app.id, cb.id, 
    'APPROVED', 
    DATE_SUB(NOW(), INTERVAL 20 DAY),
    DATE_SUB(NOW(), INTERVAL 5 DAY),
    JSON_OBJECT('certificate_number','GCN-001','change_reason','Chuyển nhượng')
FROM cat_services s
JOIN sys_users app ON app.username = 'cd_01'
JOIN sys_users cb ON cb.username = 'hc_dc'
JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'DD01_BIENDONG';

INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, 
 dossier_status, submission_date, finish_date, form_data)
SELECT 
    'HS_HC_DD_003', s.id, d.id, app.id, cb.id, 
    'APPROVED', 
    DATE_SUB(NOW(), INTERVAL 25 DAY),
    DATE_SUB(NOW(), INTERVAL 10 DAY),
    JSON_OBJECT('certificate_number','GCN-002','change_reason','Thừa kế')
FROM cat_services s
JOIN sys_users app ON app.username = 'cd_02'
JOIN sys_users cb ON cb.username = 'hc_dc'
JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'DD01_BIENDONG';

-- Hồ sơ kinh doanh (cán bộ Kinh tế - Ngô Thị Hạnh)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, 
 dossier_status, submission_date, finish_date, form_data)
SELECT 
    'HS_HC_KD_002', s.id, d.id, app.id, cb.id, 
    'APPROVED', 
    DATE_SUB(NOW(), INTERVAL 12 DAY),
    DATE_SUB(NOW(), INTERVAL 6 DAY),
    JSON_OBJECT('business_name','Cửa hàng tạp hóa Bình An','business_line','Bán lẻ')
FROM cat_services s
JOIN sys_users app ON app.username = 'cd_03'
JOIN sys_users cb ON cb.username = 'hc_kt'
JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'KD01_HKD';

INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, 
 dossier_status, submission_date, finish_date, form_data)
SELECT 
    'HS_HC_KD_003', s.id, d.id, app.id, cb.id, 
    'APPROVED', 
    DATE_SUB(NOW(), INTERVAL 18 DAY),
    DATE_SUB(NOW(), INTERVAL 12 DAY),
    JSON_OBJECT('business_name','Quán cà phê Sáng','business_line','Dịch vụ ăn uống')
FROM cat_services s
JOIN sys_users app ON app.username = 'cd_04'
JOIN sys_users cb ON cb.username = 'hc_kt'
JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'KD01_HKD';

-- =====================================================================
-- PHƯỜNG THANH KHÊ - Thêm hồ sơ đã hoàn thành
-- =====================================================================

-- Hồ sơ xử lý bởi Cán bộ Tư pháp Thanh Khê (Trương Thị Quỳnh)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, 
 dossier_status, submission_date, finish_date, form_data)
SELECT 
    'HS_TK_KS_001', s.id, d.id, app.id, cb.id, 
    'APPROVED', 
    DATE_SUB(NOW(), INTERVAL 14 DAY),
    DATE_SUB(NOW(), INTERVAL 7 DAY),
    JSON_OBJECT('child_name','Võ Minh Tuấn','dob','2024-12-10')
FROM cat_services s
JOIN sys_users app ON app.username = 'cd_07'
JOIN sys_users cb ON cb.username = 'tk_tp'
JOIN sys_departments d ON d.dept_name = 'Phường Thanh Khê'
WHERE s.service_code = 'HK01_TRE';

INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, 
 dossier_status, submission_date, finish_date, form_data)
SELECT 
    'HS_TK_KS_002', s.id, d.id, app.id, cb.id, 
    'APPROVED', 
    DATE_SUB(NOW(), INTERVAL 9 DAY),
    DATE_SUB(NOW(), INTERVAL 3 DAY),
    JSON_OBJECT('child_name','Lê Thị Hoa','dob','2024-12-20')
FROM cat_services s
JOIN sys_users app ON app.username = 'cd_08'
JOIN sys_users cb ON cb.username = 'tk_tp'
JOIN sys_departments d ON d.dept_name = 'Phường Thanh Khê'
WHERE s.service_code = 'HK01_TRE';

-- Hồ sơ xử lý bởi Cán bộ Địa chính Thanh Khê (Võ Thị Lan)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, 
 dossier_status, submission_date, finish_date, form_data)
SELECT 
    'HS_TK_DD_001', s.id, d.id, app.id, cb.id, 
    'APPROVED', 
    DATE_SUB(NOW(), INTERVAL 22 DAY),
    DATE_SUB(NOW(), INTERVAL 8 DAY),
    JSON_OBJECT('certificate_number','GCN-TK-001','change_reason','Tặng cho')
FROM cat_services s
JOIN sys_users app ON app.username = 'cd_09'
JOIN sys_users cb ON cb.username = 'tk_dc'
JOIN sys_departments d ON d.dept_name = 'Phường Thanh Khê'
WHERE s.service_code = 'DD01_BIENDONG';

-- Hồ sơ xử lý bởi Cán bộ Kinh tế Thanh Khê (Lương Văn Quân)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, 
 dossier_status, submission_date, finish_date, form_data)
SELECT 
    'HS_TK_KD_001', s.id, d.id, app.id, cb.id, 
    'APPROVED', 
    DATE_SUB(NOW(), INTERVAL 11 DAY),
    DATE_SUB(NOW(), INTERVAL 5 DAY),
    JSON_OBJECT('business_name','Tiệm tóc Thanh Khê','business_line','Dịch vụ làm đẹp')
FROM cat_services s
JOIN sys_users app ON app.username = 'cd_10'
JOIN sys_users cb ON cb.username = 'tk_kt'
JOIN sys_departments d ON d.dept_name = 'Phường Thanh Khê'
WHERE s.service_code = 'KD01_HKD';

-- =====================================================================
-- THÊM ĐÁNH GIÁ TỪ NGƯỜI DÂN (mod_feedbacks)
-- =====================================================================

-- Đánh giá cho CÁN BỘ TƯ PHÁP PHƯỜNG HẢI CHÂU (Phạm Thị Dung - hc_tp)
-- Rating cao (4-5 sao)
INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id,
    d.id,
    'Rất hài lòng với dịch vụ',
    'Cán bộ nhiệt tình, giải quyết nhanh chóng. Hồ sơ được xử lý đúng hạn.',
    5,
    TRUE,
    DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_XNHN_001'
WHERE u.username = 'cd_01';

INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id, d.id, 'Dịch vụ tốt', 
    'Cán bộ hướng dẫn tận tình, xử lý đúng quy trình.', 
    5, TRUE, DATE_SUB(NOW(), INTERVAL 3 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_HC_KS_002'
WHERE u.username = 'cd_02';

INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id, d.id, 'Hài lòng', 
    'Thủ tục đơn giản, không phải chờ lâu.', 
    4, TRUE, DATE_SUB(NOW(), INTERVAL 8 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_HC_KS_003'
WHERE u.username = 'cd_03';

INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id, d.id, 'Tốt', 
    'Cán bộ chuyên nghiệp, giải quyết nhanh.', 
    5, TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_HC_XN_002'
WHERE u.username = 'cd_03';

INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id, d.id, 'Rất hài lòng', 
    'Xử lý nhanh gọn, thái độ tốt.', 
    5, TRUE, DATE_SUB(NOW(), INTERVAL 4 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_HC_XN_003'
WHERE u.username = 'cd_04';

-- Đánh giá cho CÁN BỘ ĐỊA CHÍNH PHƯỜNG HẢI CHÂU (Hoàng Văn Em - hc_dc)
-- Rating trung bình (3-4 sao)
INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id, d.id, 'Bình thường', 
    'Xử lý đúng hạn nhưng thủ tục hơi phức tạp.', 
    3, TRUE, DATE_SUB(NOW(), INTERVAL 5 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_HC_DD_002'
WHERE u.username = 'cd_01';

INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id, d.id, 'Khá tốt', 
    'Cán bộ có kinh nghiệm, giải thích rõ ràng.', 
    4, TRUE, DATE_SUB(NOW(), INTERVAL 10 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_HC_DD_003'
WHERE u.username = 'cd_02';

-- Đánh giá cho CÁN BỘ KINH TẾ PHƯỜNG HẢI CHÂU (Ngô Thị Hạnh - hc_kt)
-- Rating cao (4-5 sao)
INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id, d.id, 'Xuất sắc', 
    'Cán bộ tư vấn rất kỹ, hỗ trợ tốt trong quá trình làm thủ tục.', 
    5, TRUE, DATE_SUB(NOW(), INTERVAL 6 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_KD_001'
WHERE u.username = 'cd_06';

INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id, d.id, 'Rất tốt', 
    'Giải quyết nhanh, không phải chờ đợi lâu.', 
    5, TRUE, DATE_SUB(NOW(), INTERVAL 6 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_HC_KD_002'
WHERE u.username = 'cd_03';

INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id, d.id, 'Hài lòng', 
    'Thủ tục rõ ràng, cán bộ nhiệt tình.', 
    4, TRUE, DATE_SUB(NOW(), INTERVAL 12 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_HC_KD_003'
WHERE u.username = 'cd_04';

-- Đánh giá cho CÁN BỘ TƯ PHÁP THANH KHÊ (Trương Thị Quỳnh - tk_tp)
-- Rating trung bình đến cao (3-5 sao)
INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id, d.id, 'Khá ổn', 
    'Giải quyết đúng thời hạn cam kết.', 
    4, TRUE, DATE_SUB(NOW(), INTERVAL 7 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_TK_KS_001'
WHERE u.username = 'cd_07';

INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id, d.id, 'Bình thường', 
    'Có giải quyết nhưng hơi chậm so với dự kiến.', 
    3, TRUE, DATE_SUB(NOW(), INTERVAL 3 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_TK_KS_002'
WHERE u.username = 'cd_08';

-- Đánh giá cho CÁN BỘ ĐỊA CHÍNH THANH KHÊ (Võ Thị Lan - tk_dc)
-- Rating thấp đến trung bình (2-3 sao)
INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id, d.id, 'Chưa hài lòng', 
    'Thủ tục phức tạp, phải bổ sung nhiều lần. Cán bộ hướng dẫn chưa rõ ràng.', 
    2, TRUE, DATE_SUB(NOW(), INTERVAL 8 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_TK_DD_001'
WHERE u.username = 'cd_09';

-- Đánh giá cho CÁN BỘ KINH TẾ THANH KHÊ (Lương Văn Quân - tk_kt)
-- Rating cao (4-5 sao)
INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved, created_at)
SELECT 
    u.id, d.id, 'Tốt', 
    'Cán bộ hỗ trợ tốt, giải quyết đúng hạn.', 
    4, TRUE, DATE_SUB(NOW(), INTERVAL 5 DAY)
FROM sys_users u
JOIN ops_dossiers d ON d.dossier_code = 'HS_TK_KD_001'
WHERE u.username = 'cd_10';

-- =====================================================================
-- KIỂM TRA DỮ LIỆU VỪA THÊM
-- =====================================================================

SELECT 
    d.dossier_code,
    s.service_name,
    u.full_name AS applicant,
    cb.full_name AS handler,
    dept.dept_name,
    d.dossier_status,
    fb.rating,
    fb.title AS feedback_title
FROM ops_dossiers d
JOIN cat_services s ON d.service_id = s.id
JOIN sys_users u ON d.applicant_id = u.id
JOIN sys_users cb ON d.current_handler_id = cb.id
JOIN sys_departments dept ON d.receiving_dept_id = dept.id
LEFT JOIN mod_feedbacks fb ON fb.dossier_id = d.id
WHERE d.dossier_status IN ('APPROVED', 'REJECTED')
ORDER BY dept.dept_name, cb.full_name, d.finish_date DESC;