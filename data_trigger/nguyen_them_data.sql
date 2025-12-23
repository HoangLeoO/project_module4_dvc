-- INSERT hồ sơ cho PHƯỜNG HẢI CHÂU
INSERT INTO ops_dossiers (
    dossier_code,
    receiving_dept_id,
    service_id,
    applicant_id,
    current_handler_id,
    dossier_status,
    submission_date,
    due_date,
    finish_date
)
VALUES
-- Hồ sơ MỚI
(
 'HC_HS_001',
 2,
 (SELECT id FROM cat_services WHERE service_code = 'HT01_KETHON'),
 (SELECT id FROM sys_users WHERE username = 'cd_01'),
 NULL,
 'NEW',
 NOW(),
 DATE_ADD(NOW(), INTERVAL 72 HOUR),
 NULL
),

-- Hồ sơ ĐANG XỬ LÝ (chưa quá hạn)
(
 'HC_HS_002',
 2,
 (SELECT id FROM cat_services WHERE service_code = 'HT02_XACNHANHN'),
 (SELECT id FROM sys_users WHERE username = 'cd_02'),
 (SELECT id FROM sys_users WHERE username = 'hc_mc'),
 'PENDING',
 NOW() - INTERVAL 1 DAY,
 DATE_ADD(NOW(), INTERVAL 1 DAY),
 NULL
),

-- Hồ sơ ĐÃ HOÀN THÀNH
(
 'HC_HS_003',
 2,
 (SELECT id FROM cat_services WHERE service_code = 'DD01_BIENDONG'),
 (SELECT id FROM sys_users WHERE username = 'cd_03'),
 (SELECT id FROM sys_users WHERE username = 'hc_dc'),
 'APPROVED',
 NOW() - INTERVAL 7 DAY,
 NOW() - INTERVAL 2 DAY,
 NOW() - INTERVAL 1 DAY
),

-- Hồ sơ QUÁ HẠN
(
 'HC_HS_004',
 2,
 (SELECT id FROM cat_services WHERE service_code = 'DD02_CHUYENMDSD'),
 (SELECT id FROM sys_users WHERE username = 'cd_04'),
 (SELECT id FROM sys_users WHERE username = 'hc_dc'),
 'PENDING',
 NOW() - INTERVAL 10 DAY,
 NOW() - INTERVAL 1 DAY,
 NULL
);

-- INSERT hồ sơ cho PHƯỜNG THANH KHÊ
INSERT INTO ops_dossiers (
    dossier_code,
    receiving_dept_id,
    service_id,
    applicant_id,
    current_handler_id,
    dossier_status,
    submission_date,
    due_date,
    finish_date
)
VALUES
-- Hồ sơ MỚI
(
 'TK_HS_001',
 4,
 (SELECT id FROM cat_services WHERE service_code = 'HK01_TRE'),
 (SELECT id FROM sys_users WHERE username = 'cd_07'),
 NULL,
 'NEW',
 NOW(),
 DATE_ADD(NOW(), INTERVAL 120 HOUR),
 NULL
),

-- Hồ sơ ĐANG XỬ LÝ
(
 'TK_HS_002',
 4,
 (SELECT id FROM cat_services WHERE service_code = 'KD01_HKD'),
 (SELECT id FROM sys_users WHERE username = 'cd_08'),
 (SELECT id FROM sys_users WHERE username = 'tk_mc'),
 'PENDING',
 NOW() - INTERVAL 2 DAY,
 DATE_ADD(NOW(), INTERVAL 1 DAY),
 NULL
),

-- Hồ sơ ĐÃ HOÀN THÀNH
(
 'TK_HS_003',
 4,
 (SELECT id FROM cat_services WHERE service_code = 'DD03_TACHHOP'),
 (SELECT id FROM sys_users WHERE username = 'cd_09'),
 (SELECT id FROM sys_users WHERE username = 'tk_dc'),
 'APPROVED',
 NOW() - INTERVAL 8 DAY,
 NOW() - INTERVAL 3 DAY,
 NOW() - INTERVAL 2 DAY
);

-- Tổng hồ sơ
SELECT COUNT(*) FROM ops_dossiers;
-- Đang xử lý
SELECT COUNT(*) FROM ops_dossiers WHERE dossier_status = 'PENDING';
-- Quá hạn
SELECT COUNT(*)
FROM ops_dossiers
WHERE dossier_status NOT IN ('APPROVED','REJECTED')
  AND due_date < NOW();
--  Biểu đồ theo lĩnh vực
SELECT cs.domain, od.dossier_status, COUNT(*)
FROM ops_dossiers od
JOIN cat_services cs ON od.service_id = cs.id
GROUP BY cs.domain, od.dossier_status;
