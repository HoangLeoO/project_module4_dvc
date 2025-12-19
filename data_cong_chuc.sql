use egov_db;

-- Thêm cấp 1: Thành phố
INSERT INTO sys_departments (dept_code, dept_name, level)
VALUES ('CITY-001', 'Thành phố Đà Nẵng', 1);

-- Thêm cấp 2: Phường/Xã
-- Lấy ID của Thành phố (đảm bảo không fix cứng là 1)
SET @city_id = (SELECT id FROM sys_departments WHERE dept_code = 'CITY-001');

INSERT INTO sys_departments (dept_code, dept_name, parent_id, level)
VALUES ('WARD-001', 'Phường Hải Châu', @city_id, 2),
       ('WARD-002', 'Phường Hòa Cường', @city_id, 2),
       ('WARD-003', 'Phường Thanh Khê', @city_id, 2),
       ('WARD-004', 'Phường An Khê', @city_id, 2),
       ('WARD-005', 'Xã Bà Nà', @city_id, 2);

-- Thêm các role cơ bản
INSERT INTO sys_roles (role_name, description)
VALUES
-- Lãnh đạo UBND xã/phường
('CHU_TICH_UBND',
 'Chủ tịch UBND xã/phường - Quyền phê duyệt cao nhất'),
('PHO_CHU_TICH_UBND',
 'Phó Chủ tịch UBND xã/phường - Phê duyệt theo phân công/ủy quyền'),
-- Bộ phận Một cửa
('CANBO_MOTCUA',
 'Cán bộ Bộ phận Một cửa - Tiếp nhận và trả kết quả hồ sơ'),
-- Tư pháp – Hộ tịch
('CANBO_TU_PHAP',
 'Cán bộ Tư pháp - Hộ tịch - Khai sinh, khai tử, kết hôn, ly hôn'),
-- Địa chính – Tài nguyên môi trường
('CANBO_DIA_CHINH',
 'Cán bộ Địa chính - Tài nguyên Môi trường - Đất đai, xây dựng, môi trường'),
-- Kinh tế
('CANBO_KINH_TE',
 'Cán bộ Kinh tế - Cấp phép kinh doanh, hộ kinh doanh, chợ'),
('ADMIN',
 'Admin - Quyền cao nhất');

INSERT INTO mock_citizens
(cccd, full_name, dob, gender, hometown, ethnic_group, religion,
 permanent_address, temporary_address, fingerprint_data, avatar_url,
 marital_status, is_deceased)
VALUES ('012345679001', 'Nguyễn Văn An', '1975-05-12', 'MALE', 'Đà Nẵng', 'Kinh', 'Không',
        'Phường Hải Châu, TP Đà Nẵng', 'Phường Hải Châu, TP Đà Nẵng', 'FP_001', '/avatars/1.png', 'MARRIED', FALSE),

       ('012345679002', 'Trần Thị Bình', '1978-08-21', 'FEMALE', 'Quảng Nam', 'Kinh', 'Không',
        'Phường Hải Châu, TP Đà Nẵng', 'Phường Hải Châu, TP Đà Nẵng', 'FP_002', '/avatars/2.png', 'MARRIED', FALSE),

       ('012345679003', 'Lê Văn Cường', '1992-02-10', 'MALE', 'Đà Nẵng', 'Kinh', 'Không',
        'Phường Thanh Khê, TP Đà Nẵng', NULL, 'FP_003', '/avatars/3.png', 'SINGLE', FALSE),

       ('012345679004', 'Phạm Thị Dung', '1995-11-03', 'FEMALE', 'Huế', 'Kinh', 'Phật giáo',
        'Phường Hòa Cường, TP Đà Nẵng', NULL, 'FP_004', '/avatars/4.png', 'SINGLE', FALSE),

       ('012345679005', 'Hoàng Văn Em', '1975-07-18', 'MALE', 'Quảng Trị', 'Kinh', 'Không',
        'Xã Bà Nà, TP Đà Nẵng', NULL, 'FP_005', '/avatars/5.png', 'WIDOWED', FALSE),

       ('012345679006', 'Ngô Thị Hạnh', '1988-03-25', 'FEMALE', 'Đà Nẵng', 'Kinh', 'Không',
        'Phường An Khê, TP Đà Nẵng', NULL, 'FP_006', '/avatars/6.png', 'MARRIED', FALSE),

       ('012345679007', 'Đặng Văn Khôi', '1986-09-14', 'MALE', 'Quảng Nam', 'Kinh', 'Không',
        'Phường An Khê, TP Đà Nẵng', NULL, 'FP_007', '/avatars/7.png', 'MARRIED', FALSE),

       ('012345679008', 'Võ Thị Lan', '1990-12-01', 'FEMALE', 'Đà Nẵng', 'Kinh', 'Không',
        'Phường Hải Châu, TP Đà Nẵng', NULL, 'FP_008', '/avatars/8.png', 'SINGLE', FALSE),

       ('012345679009', 'Bùi Văn Minh', '1998-06-06', 'MALE', 'Quảng Bình', 'Kinh', 'Không',
        'Phường Thanh Khê, TP Đà Nẵng', NULL, 'FP_009', '/avatars/9.png', 'SINGLE', FALSE),

       ('012345679010', 'Đỗ Thị Ngọc', '1960-10-10', 'FEMALE', 'Huế', 'Kinh', 'Công giáo',
        'Phường Hòa Cường, TP Đà Nẵng', NULL, 'FP_010', '/avatars/10.png', 'MARRIED', FALSE),

       ('012345679011', 'Nguyễn Văn Phúc', '1985-01-01', 'MALE', 'Đà Nẵng', 'Kinh', 'Không',
        'Phường Hải Châu, TP Đà Nẵng', NULL, 'FP_011', '/avatars/11.png', 'WIDOWED', FALSE),

       ('012345679012', 'Trương Thị Quỳnh', '1993-04-17', 'FEMALE', 'Quảng Nam', 'Kinh', 'Không',
        'Phường Thanh Khê, TP Đà Nẵng', NULL, 'FP_012', '/avatars/12.png', 'SINGLE', FALSE),

       ('012345679013', 'Phan Văn Long', '1982-02-02', 'MALE', 'Đà Nẵng', 'Kinh', 'Không',
        'Phường Hòa Cường, TP Đà Nẵng', NULL, 'FP_013', '/avatars/13.png', 'MARRIED', FALSE),

       ('012345679014', 'Lý Thị Mai', '1984-07-07', 'FEMALE', 'Quảng Trị', 'Kinh', 'Không',
        'Phường Hòa Cường, TP Đà Nẵng', NULL, 'FP_014', '/avatars/14.png', 'MARRIED', FALSE),

       ('012345679015', 'Huỳnh Văn Nam', '1990-09-09', 'MALE', 'Đà Nẵng', 'Kinh', 'Không',
        'Xã Bà Nà, TP Đà Nẵng', NULL, 'FP_015', '/avatars/15.png', 'SINGLE', FALSE),

       ('012345679016', 'Nguyễn Thị Oanh', '2002-05-20', 'FEMALE', 'Đà Nẵng', 'Kinh', 'Không',
        'Phường Hải Châu, TP Đà Nẵng', NULL, 'FP_016', '/avatars/16.png', 'SINGLE', FALSE),

       ('012345679017', 'Trần Văn Phát', '1978-11-11', 'MALE', 'Quảng Nam', 'Kinh', 'Không',
        'Phường An Khê, TP Đà Nẵng', NULL, 'FP_017', '/avatars/17.png', 'DIVORCED', FALSE),

       ('012345679018', 'Phạm Thị Hồng', '1980-03-30', 'FEMALE', 'Huế', 'Kinh', 'Không',
        'Phường An Khê, TP Đà Nẵng', NULL, 'FP_018', '/avatars/18.png', 'MARRIED', FALSE),

       ('012345679019', 'Lương Văn Quân', '1968-08-08', 'MALE', 'Quảng Bình', 'Kinh', 'Không',
        'Phường Thanh Khê, TP Đà Nẵng', NULL, 'FP_019', '/avatars/19.png', 'MARRIED', FALSE),

       ('012345679020', 'Nguyễn Thị Thanh', '1999-12-25', 'FEMALE', 'Đà Nẵng', 'Kinh', 'Không',
        'Xã Bà Nà, TP Đà Nẵng', NULL, 'FP_020', '/avatars/20.png', 'SINGLE', FALSE);
INSERT INTO mock_households
(household_code, head_citizen_id, address)
VALUES
    ('HK-DN-0001', 1,
     'Phường Hải Châu, TP Đà Nẵng'),

    ('HK-DN-0002', 3,
     'Phường Thanh Khê, TP Đà Nẵng'),

    ('HK-DN-0003', 5,
     'Xã Bà Nà, TP Đà Nẵng'),

    ('HK-DN-0004', 6,
     'Phường An Khê, TP Đà Nẵng'),

    ('HK-DN-0005', 10,
     'Phường Hòa Cường, TP Đà Nẵng'),

    ('HK-DN-0006', 11,
     'Phường Hải Châu, TP Đà Nẵng'),

    ('HK-DN-0007', 13,
     'Phường Hòa Cường, TP Đà Nẵng'),

    ('HK-DN-0008', 17,
     'Phường An Khê, TP Đà Nẵng');

INSERT INTO mock_household_members
(household_id, citizen_id, relation_to_head, move_in_date, status)
VALUES
-- Hộ 1: Gia đình đầy đủ (HK-DN-0001)
(1, 1, 'CHU_HO', '2005-01-01', 1),
(1, 2, 'VO',     '2005-01-01', 1),
(1, 8, 'CON',    '2001-12-01', 1),

-- Hộ 2: Người độc thân (HK-DN-0002)
(2, 3, 'CHU_HO', '2015-06-01', 1),

-- Hộ 3: Hộ xã – người góa
(3, 5, 'CHU_HO', '2010-03-15', 1),

-- Hộ 4: Gia đình trẻ (HK-DN-0004)
(4, 6, 'CHU_HO', '2012-08-20', 1),
(4, 7, 'CHONG',  '2012-08-20', 1),

-- Hộ 5: Gia đình trung niên (HK-DN-0005)
(5, 10, 'CHU_HO', '1995-01-01', 1),
(5, 13, 'CON',    '2002-02-02', 0),

-- Hộ 6: Người cao tuổi sống một mình
(6, 11, 'CHU_HO', '1980-01-01', 1),

-- Hộ 7: Gia đình hạt nhân
(7, 13, 'CHU_HO', '2008-05-05', 1),
(7, 14, 'VO',     '2008-05-05', 1),

-- Hộ 8: Người đã ly hôn
(8, 17, 'CHU_HO', '2018-11-11', 1);

INSERT INTO mock_lands
(land_certificate_number, issue_date, issue_authority,
 map_sheet_number, parcel_number, address_detail,
 area_m2, usage_form, land_purpose, usage_period,
 house_area_m2, construction_area_m2, asset_notes,
 owner_id, land_status)
VALUES
-- Đất ở đô thị – hộ gia đình (HK-DN-0001)
('GCN-DN-0001', '2010-06-15', 'UBND TP Đà Nẵng',
 'TBD-01', 'TH-001',
 'Phường Hải Châu, TP Đà Nẵng',
 120.00, 'Sử dụng riêng', 'Đất ở đô thị', 'Lâu dài',
 90.00, 180.00, 'Nhà ở 2 tầng bê tông cốt thép',
 1, 'Hợp pháp'),

-- Đất ở đô thị – người độc thân
('GCN-DN-0002', '2018-09-10', 'UBND TP Đà Nẵng',
 'TBD-02', 'TH-015',
 'Phường Thanh Khê, TP Đà Nẵng',
 75.50, 'Sử dụng riêng', 'Đất ở đô thị', 'Lâu dài',
 60.00, 60.00, 'Nhà cấp 4',
 3, 'Hợp pháp'),

-- Đất ở nông thôn – xã Bà Nà
('GCN-DN-0003', '2005-03-20', 'UBND Huyện Hòa Vang',
 'TBD-05', 'TH-102',
 'Xã Bà Nà, TP Đà Nẵng',
 350.00, 'Sử dụng riêng', 'Đất ở nông thôn', 'Lâu dài',
 120.00, 120.00, 'Nhà vườn, có cây ăn trái',
 5, 'Hợp pháp'),

-- Đất ở đô thị – gia đình trẻ
('GCN-DN-0004', '2016-11-30', 'UBND TP Đà Nẵng',
 'TBD-03', 'TH-044',
 'Phường An Khê, TP Đà Nẵng',
 95.00, 'Sử dụng riêng', 'Đất ở đô thị', 'Lâu dài',
 80.00, 160.00, 'Nhà 2 tầng',
 6, 'Đang thế chấp'),

-- Đất ở đô thị – trung niên
('GCN-DN-0005', '2000-01-12', 'UBND TP Đà Nẵng',
 'TBD-04', 'TH-078',
 'Phường Hòa Cường, TP Đà Nẵng',
 110.00, 'Sử dụng riêng', 'Đất ở đô thị', 'Lâu dài',
 100.00, 200.00, 'Nhà ở kết hợp kinh doanh',
 10, 'Hợp pháp'),

-- Đất trồng cây lâu năm
('GCN-DN-0006', '2012-07-07', 'UBND Huyện Hòa Vang',
 'TBD-06', 'TH-210',
 'Xã Bà Nà, TP Đà Nẵng',
 1500.00, 'Sử dụng riêng', 'Đất trồng cây lâu năm', 'Đến năm 2050',
 NULL, NULL, 'Đất trồng keo, chuối',
 15, 'Hợp pháp'),

-- Đất ở đô thị – người cao tuổi
('GCN-DN-0007', '1995-05-05', 'UBND TP Đà Nẵng',
 'TBD-01', 'TH-009',
 'Phường Hải Châu, TP Đà Nẵng',
 65.00, 'Sử dụng riêng', 'Đất ở đô thị', 'Lâu dài',
 50.00, 50.00, 'Nhà cấp 4 cũ',
 11, 'Hợp pháp');

INSERT INTO mock_businesses
(tax_code, business_name, capital, owner_id, address, business_lines)
VALUES
-- Hộ kinh doanh / doanh nghiệp nhỏ
('0401234567',
 'Hộ kinh doanh Nguyễn Văn An',
 300000000.00,
 1,
 'Phường Hải Châu, TP Đà Nẵng',
 'Bán lẻ tạp hóa'),

('0401234568',
 'Công ty TNHH Thương mại Bình Minh',
 2000000000.00,
 2,
 'Phường Hải Châu, TP Đà Nẵng',
 'Bán buôn hàng tiêu dùng'),

-- Doanh nghiệp dịch vụ
('0401234569',
 'Công ty TNHH Dịch vụ Du lịch Bà Nà',
 5000000000.00,
 5,
 'Xã Bà Nà, TP Đà Nẵng',
 'Dịch vụ du lịch, lưu trú'),

('0401234570',
 'Công ty TNHH Xây dựng Cường Phát',
 3500000000.00,
 3,
 'Phường Thanh Khê, TP Đà Nẵng',
 'Xây dựng công trình dân dụng'),

-- Doanh nghiệp vừa
('0401234571',
 'Công ty TNHH Kinh doanh Tổng hợp Hòa Cường',
 8000000000.00,
 10,
 'Phường Hòa Cường, TP Đà Nẵng',
 'Kinh doanh vật liệu xây dựng'),
('0401234572',
 'Công ty TNHH Thực phẩm An Khê',
 1500000000.00,
 6,
 'Phường An Khê, TP Đà Nẵng',
 'Chế biến và phân phối thực phẩm'),
-- Doanh nghiệp dịch vụ – cá nhân trẻ
('0401234573',
 'Công ty TNHH Công nghệ Quang Minh',
 1200000000.00,
 8,
 'Phường Hải Châu, TP Đà Nẵng',
 'Phần mềm, dịch vụ CNTT');

INSERT INTO sys_users
(username, password_hash, full_name, user_type, citizen_id, dept_id)
VALUES
    ('admin', '$2a$10$admin', 'Quản trị hệ thống', 'ADMIN', NULL, NULL);


# PHƯỜNG HẢI CHÂU (dept_id = 2)
INSERT INTO sys_users
(username, password_hash, full_name, user_type, citizen_id, dept_id)
VALUES
    ('hc_ct',  '$2a$10$hc_ct',  'Nguyễn Văn An',     'OFFICIAL', 1, 2),
    ('hc_pct', '$2a$10$hc_pct', 'Trần Thị Bình',     'OFFICIAL', 2, 2),
    ('hc_mc',  '$2a$10$hc_mc',  'Lê Văn Cường',      'OFFICIAL', 3, 2),
    ('hc_tp',  '$2a$10$hc_tp',  'Phạm Thị Dung',     'OFFICIAL', 4, 2),
    ('hc_dc',  '$2a$10$hc_dc',  'Hoàng Văn Em',      'OFFICIAL', 5, 2),
    ('hc_kt',  '$2a$10$hc_kt',  'Ngô Thị Hạnh',      'OFFICIAL', 6, 2);

INSERT INTO sys_users
(username, password_hash, full_name, user_type, citizen_id)
VALUES
    ('cd_01', '$2a$10$cd1', 'Nguyễn Văn An',  'CITIZEN', 1),
    ('cd_02', '$2a$10$cd2', 'Trần Thị Bình',  'CITIZEN', 2),
    ('cd_03', '$2a$10$cd3', 'Lê Văn Cường',   'CITIZEN', 3),
    ('cd_04', '$2a$10$cd4', 'Phạm Thị Dung',  'CITIZEN', 4),
    ('cd_05', '$2a$10$cd5', 'Hoàng Văn Em',   'CITIZEN', 5),
    ('cd_06', '$2a$10$cd6', 'Ngô Thị Hạnh',   'CITIZEN', 6);

# PHƯỜNG THANH KHÊ (dept_id = 4)

INSERT INTO sys_users
(username, password_hash, full_name, user_type, citizen_id, dept_id)
VALUES
    ('tk_ct',  '$2a$10$tk_ct',  'Bùi Văn Minh',   'OFFICIAL', 9, 4),
    ('tk_pct', '$2a$10$tk_pct', 'Đỗ Thị Ngọc',    'OFFICIAL', 10, 4),
    ('tk_mc',  '$2a$10$tk_mc',  'Phan Văn Long',  'OFFICIAL', 13, 4),
    ('tk_tp',  '$2a$10$tk_tp',  'Trương Thị Quỳnh','OFFICIAL',12,4),
    ('tk_dc',  '$2a$10$tk_dc',  'Võ Thị Lan',     'OFFICIAL',8,4),
    ('tk_kt',  '$2a$10$tk_kt',  'Lương Văn Quân', 'OFFICIAL',19,4);

INSERT INTO sys_users
(username, password_hash, full_name, user_type, citizen_id)
VALUES
    ('cd_07', '$2a$10$cd7', 'Bùi Văn Minh',    'CITIZEN', 9),
    ('cd_08', '$2a$10$cd8', 'Đỗ Thị Ngọc',     'CITIZEN', 10),
    ('cd_09', '$2a$10$cd9', 'Phan Văn Long',   'CITIZEN', 13),
    ('cd_10', '$2a$10$cd10','Trương Thị Quỳnh','CITIZEN',12),
    ('cd_11', '$2a$10$cd11','Võ Thị Lan',      'CITIZEN',8),
    ('cd_12', '$2a$10$cd12','Lương Văn Quân',  'CITIZEN',19);

# Gán role cho PHƯỜNG HẢI CHÂU
INSERT INTO sys_user_roles (user_id, role_id)
SELECT u.id, r.id FROM sys_users u JOIN sys_roles r
                                        ON r.role_name = 'CHU_TICH_UBND'
WHERE u.username = 'hc_ct';

INSERT INTO sys_user_roles (user_id, role_id)
SELECT u.id, r.id FROM sys_users u JOIN sys_roles r
                                        ON r.role_name = 'PHO_CHU_TICH_UBND'
WHERE u.username = 'hc_pct';

INSERT INTO sys_user_roles (user_id, role_id)
SELECT u.id, r.id FROM sys_users u JOIN sys_roles r
                                        ON r.role_name = 'CANBO_MOTCUA'
WHERE u.username = 'hc_mc';

INSERT INTO sys_user_roles (user_id, role_id)
SELECT u.id, r.id FROM sys_users u JOIN sys_roles r
                                        ON r.role_name = 'CANBO_TU_PHAP'
WHERE u.username = 'hc_tp';

INSERT INTO sys_user_roles (user_id, role_id)
SELECT u.id, r.id FROM sys_users u JOIN sys_roles r
                                        ON r.role_name = 'CANBO_DIA_CHINH'
WHERE u.username = 'hc_dc';

INSERT INTO sys_user_roles (user_id, role_id)
SELECT u.id, r.id FROM sys_users u JOIN sys_roles r
                                        ON r.role_name = 'CANBO_KINH_TE'
WHERE u.username = 'hc_kt';

# Gán role cho PHƯỜNG THANH KHÊ

INSERT INTO sys_user_roles (user_id, role_id)
SELECT u.id, r.id FROM sys_users u JOIN sys_roles r
                                        ON r.role_name = 'CHU_TICH_UBND'
WHERE u.username = 'tk_ct';

INSERT INTO sys_user_roles (user_id, role_id)
SELECT u.id, r.id FROM sys_users u JOIN sys_roles r
                                        ON r.role_name = 'PHO_CHU_TICH_UBND'
WHERE u.username = 'tk_pct';

INSERT INTO sys_user_roles (user_id, role_id)
SELECT u.id, r.id FROM sys_users u JOIN sys_roles r
                                        ON r.role_name = 'CANBO_MOTCUA'
WHERE u.username = 'tk_mc';

INSERT INTO sys_user_roles (user_id, role_id)
SELECT u.id, r.id FROM sys_users u JOIN sys_roles r
                                        ON r.role_name = 'CANBO_TU_PHAP'
WHERE u.username = 'tk_tp';

INSERT INTO sys_user_roles (user_id, role_id)
SELECT u.id, r.id FROM sys_users u JOIN sys_roles r
                                        ON r.role_name = 'CANBO_DIA_CHINH'
WHERE u.username = 'tk_dc';

INSERT INTO sys_user_roles (user_id, role_id)
SELECT u.id, r.id FROM sys_users u JOIN sys_roles r
                                        ON r.role_name = 'CANBO_KINH_TE'
WHERE u.username = 'tk_kt';

SELECT d.dept_name, u.username, r.role_name
FROM sys_user_roles ur
         JOIN sys_users u ON ur.user_id = u.id
         JOIN sys_roles r ON ur.role_id = r.id
         JOIN sys_departments d ON u.dept_id = d.id
ORDER BY d.dept_name, r.role_name;


-- Thêm các dịch vụ công vào bảng cat_services
-- 1. Dịch vụ Hộ tịch, Cư trú, và Y tế cho trẻ em
INSERT INTO cat_services (service_code, service_name, id_role, domain, sla_hours, fee_amount, form_schema)
VALUES ('HK01_TRE',
        'Đăng ký khai sinh, đăng ký thường trú, cấp thẻ bảo hiểm y tế cho trẻ em dưới 6 tuổi',
        (SELECT id FROM sys_roles WHERE role_name = 'CANBO_TU_PHAP'),
        'HỘ TỊCH & CƯ TRÚ',
        120, -- 5 ngày làm việc
        0.00,
        '{"sections": [{"id":"child_info","title":"Thông tin trẻ","fields":[{"name":"childFullName","label":"Họ và tên trẻ","type":"text","required":true},{"name":"dateOfBirth","label":"Ngày sinh","type":"date","required":true},{"name":"gender","label":"Giới tính","type":"select","options":[{"value":"MALE","label":"Nam"},{"value":"FEMALE","label":"Nữ"}],"required":true},{"name":"placeOfBirth","label":"Nơi sinh","type":"text","required":true}]},{"id":"parent_info","title":"Thông tin cha mẹ","fields":[{"name":"fatherFullName","label":"Họ tên cha","type":"text"},{"name":"fatherIdNumber","label":"CCCD Cha","type":"text"},{"name":"motherFullName","label":"Họ tên mẹ","type":"text","required":true},{"name":"motherIdNumber","label":"CCCD Mẹ","type":"text","required":true}]},{"id":"other_info","title":"Thông tin khác","fields":[{"name":"registeredAddress","label":"Địa chỉ thường trú","type":"text","required":true},{"name":"requestBhyt","label":"Đăng ký BHYT","type":"checkbox"}]}]}');

-- 2. Đăng ký kết hôn
INSERT INTO cat_services (service_code, service_name, id_role, domain, sla_hours, fee_amount, form_schema)
VALUES ('HT01_KETHON',
        'Thủ tục đăng ký kết hôn',
        (SELECT id FROM sys_roles WHERE role_name = 'CANBO_TU_PHAP'),
        'HỘ TỊCH',
        72, -- 3 ngày làm việc
        100000.00,
        '{"sections": [{"id":"husband_info","title":"Thông tin người chồng","fields":[{"name":"husbandFullName","label":"Họ tên chồng","type":"text","required":true},{"name":"husbandDob","label":"Ngày sinh","type":"date","required":true},{"name":"husbandIdNumber","label":"CCCD/CMND","type":"text","required":true}]},{"id":"wife_info","title":"Thông tin người vợ","fields":[{"name":"wifeFullName","label":"Họ tên vợ","type":"text","required":true},{"name":"wifeDob","label":"Ngày sinh","type":"date","required":true},{"name":"wifeIdNumber","label":"CCCD/CMND","type":"text","required":true}]},{"id":"marriage_info","title":"Thông tin đăng ký","fields":[{"name":"intendedMarriageDate","label":"Ngày dự định kết hôn","type":"date","required":true},{"name":"registeredPlace","label":"Nơi đăng ký","type":"text","required":true}]}]}');

-- 3. Đăng ký khai tử, xóa thường trú, chế độ mai táng/tử tuất
INSERT INTO cat_services (service_code, service_name, id_role, domain, sla_hours, fee_amount, form_schema)
VALUES ('HK02_KAITU',
        'Đăng ký khai tử, xóa đăng ký thường trú, giải quyết mai táng phí, tử tuất',
        (SELECT id FROM sys_roles WHERE role_name = 'CANBO_TU_PHAP'),
        'HỘ TỊCH & CƯ TRÚ & LĐTBXH',
        168, -- 7 ngày làm việc
        0.00,
        '{"sections": [{"id":"deceased_info","title":"Thông tin người mất","fields":[{"name":"deceasedFullName","label":"Họ tên người mất","type":"text","required":true},{"name":"dateOfBirth","label":"Ngày sinh","type":"date","required":true},{"name":"dateOfDeath","label":"Ngày mất","type":"date","required":true},{"name":"placeOfDeath","label":"Nơi mất","type":"text","required":true},{"name":"lastResidence","label":"Nơi cư trú cuối cùng","type":"text","required":true}]},{"id":"declarant_info","title":"Thông tin người khai","fields":[{"name":"relativeFullName","label":"Họ tên người thân","type":"text","required":true},{"name":"relativeRelationship","label":"Mối quan hệ","type":"text","required":true}]}]}');

-- 4. Cấp Giấy xác nhận tình trạng hôn nhân
INSERT INTO cat_services (service_code, service_name, id_role, domain, sla_hours, fee_amount,
                          form_schema)
VALUES ('HT02_XACNHANHN',
        'Thủ tục cấp Giấy xác nhận tình trạng hôn nhân',
        (SELECT id FROM sys_roles WHERE role_name = 'CANBO_TU_PHAP'),
        'HỘ TỊCH',
        48, -- 2 ngày làm việc
        30000.00,
        '{"sections": [{"id":"personal_info","title":"Thông tin người yêu cầu","fields":[{"name":"requesterFullName","label":"Họ tên người yêu cầu","type":"text","required":true},{"name":"dateOfBirth","label":"Ngày sinh","type":"date","required":true},{"name":"idNumber","label":"CCCD/CMND","type":"text","required":true}]},{"id":"status_info","title":"Tình trạng hôn nhân","fields":[{"name":"currentMaritalStatus","label":"Tình trạng hiện tại","type":"text","required":true},{"name":"confirmationPeriod","label":"Giai đoạn xác nhận","type":"text","required":true},{"name":"purposeOfUse","label":"Mục đích sử dụng","type":"text","required":true}]}]}');

-- 5. Đăng ký biến động đất đai do thay đổi quyền sử dụng (hộ/vợ chồng)
INSERT INTO cat_services (service_code, service_name, id_role, domain, sla_hours, fee_amount,
                          form_schema)
VALUES ('DD01_BIENDONG',
        'Đăng ký biến động đối với trường hợp thay đổi quyền sử dụng đất, quyền sở hữu tài sản gắn liền với đất theo thỏa thuận của các thành viên hộ gia đình hoặc của vợ và chồng',
        (SELECT id FROM sys_roles WHERE role_name = 'CANBO_DIA_CHINH'),
        'ĐẤT ĐAI',
        360, -- Khoảng 15 ngày làm việc
        50000.00,
        '{"sections": [{"id":"land_info","title":"Thông tin thửa đất","fields":[{"name":"landCertificateNumber","label":"Số GCN (Sổ đỏ)","type":"text","required":true},{"name":"landPlotNumber","label":"Số thửa","type":"text","required":true},{"name":"landMapSheet","label":"Tờ bản đồ số","type":"text","required":true},{"name":"currentOwner","label":"Chủ sở hữu hiện tại","type":"text","required":true}]},{"id":"change_info","title":"Thông tin biến động","fields":[{"name":"changeType","label":"Loại biến động","type":"text","required":true},{"name":"changeReason","label":"Lý do biến động","type":"text","required":true},{"name":"newOwner","label":"Chủ sở hữu mới","type":"text"}]}]}');

-- 6. Chuyển mục đích sử dụng đất
INSERT INTO cat_services (service_code, service_name, id_role, domain, sla_hours, fee_amount,
                          form_schema)
VALUES ('DD02_CHUYENMDSD',
        'Thủ tục chuyển mục đích sử dụng đất phải được phép của cơ quan nhà nước có thẩm quyền đối với hộ gia đình, cá nhân',
        (SELECT id FROM sys_roles WHERE role_name = 'CANBO_DIA_CHINH'),
        'ĐẤT ĐAI',
        480, -- Khoảng 20 ngày làm việc
        500000.00,
        '{"sections": [{"id":"land_info","title":"Thông tin thửa đất","fields":[{"name":"landCertificateNumber","label":"Số GCN (Sổ đỏ)","type":"text","required":true},{"name":"landPlotNumber","label":"Số thửa","type":"text","required":true},{"name":"mapSheetNumber","label":"Tờ bản đồ số","type":"text","required":true},{"name":"landAreaM2","label":"Diện tích (m2)","type":"number","required":true}]},{"id":"purpose_info","title":"Thông tin chuyển đổi","fields":[{"name":"currentLandPurpose","label":"Mục đích sử dụng hiện tại","type":"text","required":true},{"name":"requestedLandPurpose","label":"Mục đích sử dụng mong muốn","type":"text","required":true},{"name":"reasonForChange","label":"Lý do chuyển mục đích","type":"textarea","required":true}]},{"id":"commitment_info","title":"Cam kết","fields":[{"name":"commitment","label":"Cam kết của người xin chuyển","type":"textarea","required":true}]}]}');

-- 7. Tách thửa hoặc hợp thửa đất
INSERT INTO cat_services (service_code, service_name, id_role, domain, sla_hours, fee_amount,
                          form_schema)
VALUES ('DD03_TACHHOP',
        'Thủ tục tách thửa hoặc hợp thửa đất',
        (SELECT id FROM sys_roles WHERE role_name = 'CANBO_DIA_CHINH'),
        'ĐẤT ĐAI',
        360, -- Khoảng 15 ngày làm việc
        50000.00,
        '{"sections": [{"id":"land_info","title":"Thông tin thửa đất gốc","fields":[{"name":"landCertificateNumber","label":"Số GCN (Sổ đỏ)","type":"text","required":true},{"name":"landPlotNumber","label":"Số thửa","type":"text","required":true},{"name":"mapSheetNumber","label":"Tờ bản đồ số","type":"text","required":true},{"name":"originalAreaM2","label":"Diện tích gốc (m2)","type":"number","required":true}]},{"id":"split_info","title":"Thông tin tách thửa","fields":[{"name":"requestedSplitAreas","label":"Diện tích các thửa mới (phân cách bằng dấu phẩy)","type":"text","required":true},{"name":"numberOfNewPlots","label":"Số lượng thửa mới","type":"number","required":true},{"name":"splitReason","label":"Lý do tách thửa","type":"textarea","required":true},{"name":"surveyCompleted","label":"Đã đo đạc địa chính?","type":"checkbox","required":true}]}]}');

-- 8. Đăng ký thành lập hộ kinh doanh
INSERT INTO cat_services (service_code, service_name, id_role, domain, sla_hours, fee_amount, form_schema)
VALUES ('KD01_HKD',
        'Đăng ký thành lập hộ kinh doanh',
        (SELECT id FROM sys_roles WHERE role_name = 'CANBO_KINH_TE'),
        'KINH DOANH',
        72, -- 3 ngày làm việc
        50000.00,
        '{"sections": [{"id":"business_info","title":"Thông tin hộ kinh doanh","fields":[{"name":"businessName","label":"Tên hộ kinh doanh","type":"text","required":true},{"name":"businessAddress","label":"Địa điểm kinh doanh","type":"text","required":true},{"name":"businessLine","label":"Ngành nghề kinh doanh","type":"text","required":true},{"name":"registeredCapital","label":"Vốn kinh doanh (VNĐ)","type":"number","required":true},{"name":"numberOfEmployees","label":"Số lượng lao động","type":"number","required":true}]},{"id":"owner_info","title":"Thông tin chủ hộ","fields":[{"name":"businessOwner","label":"Họ tên chủ hộ","type":"text","required":true},{"name":"ownerIdNumber","label":"Số CCCD/CMND","type":"text","required":true}]}]}');


-- THÊM QUY TRÌNH XỬ LÝ (WORKFLOW STEPS)

-- 1. HK01_TRE: Khai sinh + Thường trú + BHYT
-- Quy trình: Một cửa tiếp nhận -> Tư pháp thẩm định -> Tư pháp xác minh cư trú -> Lãnh đạo phê duyệt -> Một cửa trả kết quả
INSERT INTO cat_workflow_steps (service_id, step_name, step_order, role_required_id)
SELECT s.id, 'Tiếp nhận hồ sơ', 1, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HK01_TRE' AND r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT s.id, 'Kiểm tra hồ sơ hộ tịch', 2, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HK01_TRE' AND r.role_name = 'CANBO_TU_PHAP'
UNION ALL
SELECT s.id, 'Xác minh thông tin cư trú', 3, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HK01_TRE' AND r.role_name = 'CANBO_TU_PHAP'
UNION ALL
SELECT s.id, 'Phê duyệt kết quả', 4, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HK01_TRE' AND r.role_name = 'PHO_CHU_TICH_UBND'
UNION ALL
SELECT s.id, 'Trả kết quả', 5, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HK01_TRE' AND r.role_name = 'CANBO_MOTCUA';

-- 2. HT01_KETHON: Đăng ký kết hôn
-- Quy trình: Một cửa tiếp nhận -> Tư pháp thẩm tra -> Chủ tịch phê duyệt -> Một cửa trao GCN
INSERT INTO cat_workflow_steps (service_id, step_name, step_order, role_required_id)
SELECT s.id, 'Tiếp nhận hồ sơ', 1, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HT01_KETHON' AND r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT s.id, 'Thẩm tra điều kiện kết hôn', 2, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HT01_KETHON' AND r.role_name = 'CANBO_TU_PHAP'
UNION ALL
SELECT s.id, 'Phê duyệt kết hôn', 3, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HT01_KETHON' AND r.role_name = 'CHU_TICH_UBND'
UNION ALL
SELECT s.id, 'Trao Giấy chứng nhận kết hôn', 4, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HT01_KETHON' AND r.role_name = 'CANBO_MOTCUA';

-- 3. HK02_KAITU: Khai tử
-- Quy trình: Một cửa tiếp nhận -> Tư pháp xác minh -> Lãnh đạo phê duyệt -> Tư pháp cập nhật -> Một cửa trả KQ
INSERT INTO cat_workflow_steps (service_id, step_name, step_order, role_required_id)
SELECT s.id, 'Tiếp nhận hồ sơ', 1, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HK02_KAITU' AND r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT s.id, 'Xác minh thông tin khai tử', 2, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HK02_KAITU' AND r.role_name = 'CANBO_TU_PHAP'
UNION ALL
SELECT s.id, 'Phê duyệt khai tử', 3, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HK02_KAITU' AND r.role_name = 'PHO_CHU_TICH_UBND'
UNION ALL
SELECT s.id, 'Cập nhật dữ liệu dân cư', 4, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HK02_KAITU' AND r.role_name = 'CANBO_TU_PHAP'
UNION ALL
SELECT s.id, 'Trả kết quả', 5, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HK02_KAITU' AND r.role_name = 'CANBO_MOTCUA';

-- 4. HT02_XACNHANHN: Xác nhận tình trạng hôn nhân
-- Quy trình: Một cửa tiếp nhận -> Tư pháp đối soát -> Lãnh đạo xác nhận -> Một cửa trả KQ
INSERT INTO cat_workflow_steps (service_id, step_name, step_order, role_required_id)
SELECT s.id, 'Tiếp nhận hồ sơ', 1, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HT02_XACNHANHN' AND r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT s.id, 'Đối soát dữ liệu hôn nhân', 2, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HT02_XACNHANHN' AND r.role_name = 'CANBO_TU_PHAP'
UNION ALL
SELECT s.id, 'Phê duyệt xác nhận', 3, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HT02_XACNHANHN' AND r.role_name = 'PHO_CHU_TICH_UBND'
UNION ALL
SELECT s.id, 'Trả giấy xác nhận', 4, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'HT02_XACNHANHN' AND r.role_name = 'CANBO_MOTCUA';

-- 5. DD01_BIENDONG: Biến động đất đai
-- Quy trình: Một cửa tiếp nhận -> Địa chính kiểm tra -> Địa chính lấy ý kiến -> Chủ tịch phê duyệt -> Địa chính cập nhật -> Một cửa trả KQ
INSERT INTO cat_workflow_steps (service_id, step_name, step_order, role_required_id)
SELECT s.id, 'Tiếp nhận hồ sơ', 1, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD01_BIENDONG' AND r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT s.id, 'Kiểm tra pháp lý đất đai', 2, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD01_BIENDONG' AND r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT s.id, 'Lấy ý kiến liên quan', 3, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD01_BIENDONG' AND r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT s.id, 'Phê duyệt biến động', 4, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD01_BIENDONG' AND r.role_name = 'CHU_TICH_UBND'
UNION ALL
SELECT s.id, 'Cập nhật hồ sơ địa chính', 5, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD01_BIENDONG' AND r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT s.id, 'Trả kết quả', 6, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD01_BIENDONG' AND r.role_name = 'CANBO_MOTCUA';

-- 6. DD02_CHUYENMDSD: Chuyển mục đích sử dụng đất
-- Quy trình: Một cửa tiếp nhận -> Địa chính thẩm định -> Địa chính quy hoạch -> Chủ tịch phê duyệt -> Địa chính cập nhật -> Một cửa trả KQ
INSERT INTO cat_workflow_steps (service_id, step_name, step_order, role_required_id)
SELECT s.id, 'Tiếp nhận hồ sơ', 1, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD02_CHUYENMDSD' AND r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT s.id, 'Thẩm định nhu cầu sử dụng đất', 2, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD02_CHUYENMDSD' AND r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT s.id, 'Lấy ý kiến quy hoạch', 3, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD02_CHUYENMDSD' AND r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT s.id, 'Phê duyệt chuyển mục đích', 4, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD02_CHUYENMDSD' AND r.role_name = 'CHU_TICH_UBND'
UNION ALL
SELECT s.id, 'Cập nhật hồ sơ địa chính', 5, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD02_CHUYENMDSD' AND r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT s.id, 'Trả kết quả', 6, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD02_CHUYENMDSD' AND r.role_name = 'CANBO_MOTCUA';

-- 7. DD03_TACHHOP: Tách / Hợp thửa đất
-- Quy trình: Một cửa tiếp nhận -> Địa chính kiểm tra -> Địa chính đo đạc -> Chủ tịch phê duyệt -> Một cửa trả KQ
INSERT INTO cat_workflow_steps (service_id, step_name, step_order, role_required_id)
SELECT s.id, 'Tiếp nhận hồ sơ', 1, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD03_TACHHOP' AND r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT s.id, 'Kiểm tra điều kiện tách/hợp thửa', 2, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD03_TACHHOP' AND r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT s.id, 'Đo đạc và cập nhật bản đồ', 3, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD03_TACHHOP' AND r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT s.id, 'Phê duyệt kết quả', 4, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD03_TACHHOP' AND r.role_name = 'CHU_TICH_UBND'
UNION ALL
SELECT s.id, 'Trả kết quả', 5, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'DD03_TACHHOP' AND r.role_name = 'CANBO_MOTCUA';

-- 8. KD01_HKD: Đăng ký hộ kinh doanh
-- Quy trình: Một cửa tiếp nhận -> Kinh tế thẩm tra -> Lãnh đạo phê duyệt -> Một cửa trả KQ
INSERT INTO cat_workflow_steps (service_id, step_name, step_order, role_required_id)
SELECT s.id, 'Tiếp nhận hồ sơ', 1, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'KD01_HKD' AND r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT s.id, 'Thẩm tra thông tin kinh doanh', 2, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'KD01_HKD' AND r.role_name = 'CANBO_KINH_TE'
UNION ALL
SELECT s.id, 'Phê duyệt đăng ký', 3, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'KD01_HKD' AND r.role_name = 'PHO_CHU_TICH_UBND'
UNION ALL
SELECT s.id, 'Cấp Giấy chứng nhận HKD', 4, r.id FROM cat_services s, sys_roles r WHERE s.service_code = 'KD01_HKD' AND r.role_name = 'CANBO_MOTCUA';

-- =======================================================
-- MODULE 4: DỮ LIỆU VẬN HÀNH (OPERATIONAL DATA)
-- =======================================================

-- 1. HỒ SƠ 1: Mới tiếp nhận (NEW) - Khai sinh
-- Người nộp: cd_01 (Nguyễn Văn An)
-- Cán bộ xử lý: hc_mc (Lê Văn Cường - Một cửa Phường Hải Châu)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, dossier_status, submission_date, due_date, form_data)
VALUES
    ('HS-HK01-0001',
     (SELECT id FROM cat_services WHERE service_code = 'HK01_TRE'),
     (SELECT id FROM sys_departments WHERE dept_code = 'WARD-001'), -- Phường Hải Châu
     (SELECT id FROM sys_users WHERE username = 'cd_01'),
     (SELECT id FROM sys_users WHERE username = 'hc_mc'),
     'NEW',
     NOW(),
     DATE_ADD(NOW(), INTERVAL 5 DAY),
     '{"childFullName": "Nguyễn Văn Bi", "dateOfBirth": "2023-10-10", "gender": "MALE", "placeOfBirth": "Bệnh viện Phụ sản Nhi", "fatherFullName": "Nguyễn Văn An", "motherFullName": "Trần Thị Bình"}'
    );

-- Log cho HS 1
INSERT INTO ops_dossier_logs (dossier_id, actor_id, action, prev_status, next_status, comments)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HK01-0001'),
     (SELECT id FROM sys_users WHERE username = 'cd_01'),
     'NOP_HO_SO', NULL, 'NEW', 'Công dân nộp hồ sơ trực tuyến');

-- 2. HỒ SƠ 2: Đang xử lý (PENDING) - Đất đai (Biến động)
-- Người nộp: cd_02 (Trần Thị Bình)
-- Cán bộ xử lý: hc_dc (Hoàng Văn Em - Địa chính Phường Hải Châu)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, dossier_status, submission_date, due_date, form_data)
VALUES
    ('HS-DD01-0002',
     (SELECT id FROM cat_services WHERE service_code = 'DD01_BIENDONG'),
     (SELECT id FROM sys_departments WHERE dept_code = 'WARD-001'),
     (SELECT id FROM sys_users WHERE username = 'cd_02'),
     (SELECT id FROM sys_users WHERE username = 'hc_dc'),
     'PENDING',
     DATE_SUB(NOW(), INTERVAL 1 DAY),
     DATE_ADD(NOW(), INTERVAL 14 DAY),
     '{"landCertificateNumber": "GCN-DN-0001", "changeType": "Tặng cho quyền sử dụng đất", "newOwner": "Nguyễn Thị Oanh"}'
    );

INSERT INTO ops_dossier_logs (dossier_id, actor_id, action, prev_status, next_status, comments)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD01-0002'),
     (SELECT id FROM sys_users WHERE username = 'cd_02'),
     'NOP_HO_SO', NULL, 'NEW', 'Nộp hồ sơ trực tuyến'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD01-0002'),
     (SELECT id FROM sys_users WHERE username = 'hc_mc'),
     'CHUYEN_BUOC', 'NEW', 'PENDING', 'Hồ sơ đầy đủ, chuyển Địa chính thẩm định');

-- 3. HỒ SƠ 3: Đã trình ký (VERIFIED) - Xác nhận hôn nhân
-- Người nộp: cd_04 (Phạm Thị Dung)
-- Cán bộ xử lý: hc_pct (Trần Thị Bình -- HS 16: Hộ kinh doanh (Hải Châu)
INSERT INTO ops_dossiers (dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, dossier_status, submission_date, due_date, form_data)
VALUES ('HS-KD01-0016', (SELECT id FROM cat_services WHERE service_code = 'KD01_HKD'), (SELECT id FROM sys_departments WHERE dept_code = 'WARD-001'), (SELECT id FROM sys_users WHERE username = 'cd_04'), (SELECT id FROM sys_users WHERE username = 'hc_mc'), 'NEW', NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), '{"businessName": "Tiệm làm tóc Dung"}');
INSERT INTO ops_dossier_logs (dossier_id, actor_id, action, prev_status, next_status, comments) VALUES ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-KD01-0016'), (SELECT id FROM sys_users WHERE username = 'cd_04'), 'NOP_HO_SO', NULL, 'NEW', NULL);


-- =======================================================
-- MODULE 2, 3, 5: DỮ LIỆU BỔ SUNG (MISSING COVERAGE)
-- =======================================================

-- 1. Cấu hình hệ thống (sys_configs)
INSERT INTO sys_configs (config_key, config_value, description)
VALUES
    ('UPLOAD_PATH', '/var/www/uploads', 'Đường dẫn lưu trữ file upload'),
    ('MAX_FILE_SIZE_MB', '20', 'Dung lượng file tối đa (MB)'),
    ('SYSTEM_MAINTENANCE_MODE', 'FALSE', 'Chế độ bảo trì hệ thống'),
    ('DEFAULT_PAGE_SIZE', '10', 'Số bản ghi hiển thị mặc định trên trang');

-- 2. Ủy quyền (sys_user_delegations)
-- Chủ tịch (hc_ct) ủy quyền cho Phó Chủ tịch (hc_pct) trong 1 tuần
INSERT INTO sys_user_delegations (from_user_id, to_user_id, start_time, end_time, notes)
VALUES
    ((SELECT id FROM sys_users WHERE username = 'hc_ct'),
     (SELECT id FROM sys_users WHERE username = 'hc_pct'),
     NOW(),
     DATE_ADD(NOW(), INTERVAL 7 DAY),
     'Đi công tác Hà Nội, ủy quyền xử lý hồ sơ');

-- 3. Biểu mẫu in (cat_templates)
INSERT INTO cat_templates (service_id, template_name, file_path, variable_mapping)
VALUES
    -- Khai sinh
    ((SELECT id FROM cat_services WHERE service_code = 'HK01_TRE'),
     'Giấy khai sinh bản chính', '/templates/khaisinh_ban_chinh.docx',
     '{"childName": "childFullName", "dob": "dateOfBirth", "father": "fatherFullName", "mother": "motherFullName"}'),
    -- Kết hôn
    ((SELECT id FROM cat_services WHERE service_code = 'HT01_KETHON'),
     'Giấy chứng nhận kết hôn', '/templates/kethon_mau_01.docx',
     '{"husband": "husbandFullName", "wife": "wifeFullName", "date": "intendedMarriageDate"}'),
    -- Kinh doanh
    ((SELECT id FROM cat_services WHERE service_code = 'KD01_HKD'),
     'Giấy phép kinh doanh hộ cá thể', '/templates/gpkd_mau_a4.docx',
     '{"name": "businessName", "owner": "businessOwner", "address": "businessAddress"}');

-- 4. Kho cá nhân (mod_personal_vaults)
-- Công dân 1 (Nguyễn Văn An) lưu giấy tờ
INSERT INTO mod_personal_vaults (user_id, doc_name, doc_type, file_url)
VALUES
    ((SELECT id FROM sys_users WHERE username = 'cd_01'), 'CCCD Mặt trước', 'IMAGE', '/vault/cd01_cccd_mt.jpg'),
    ((SELECT id FROM sys_users WHERE username = 'cd_01'), 'CCCD Mặt sau', 'IMAGE', '/vault/cd01_cccd_ms.jpg'),
    ((SELECT id FROM sys_users WHERE username = 'cd_01'), 'Sổ hộ khẩu cũ', 'PDF', '/vault/cd01_shk_scan.pdf'),
    ((SELECT id FROM sys_users WHERE username = 'cd_01'), 'Bằng đại học', 'PDF', '/vault/cd01_bang_dai_hoc.pdf');

-- 5. Phản ánh (mod_feedbacks)
INSERT INTO mod_feedbacks (user_id, dossier_id, title, content, rating, is_resolved)
VALUES
    ((SELECT id FROM sys_users WHERE username = 'cd_02'),
     (SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD01-0002'),
     'Thái độ phục vụ tốt', 'Cán bộ hướng dẫn rất nhiệt tình, hồ sơ xử lý nhanh.', 5, TRUE),

    ((SELECT id FROM sys_users WHERE username = 'cd_04'),
     NULL, -- Phản ánh chung
     'Hệ thống chậm', 'Truy cập vào giờ cao điểm hơi lag, đề nghị nâng cấp.', 3, FALSE);

-- 6. Thông báo (mod_notifications)
INSERT INTO mod_notifications (user_id, title, message, type)
VALUES
    ((SELECT id FROM sys_users WHERE username = 'cd_01'),
     'Hồ sơ đã được phê duyệt',
     'Hồ sơ đăng ký kinh doanh (HS-KD01-0004) của ông/bà đã có kết quả. Vui lòng đến nhận.',
     'STATUS_UPDATE'),

    ((SELECT id FROM sys_users WHERE username = 'hc_pct'),
     'Nhận ủy quyền mới',
     'Bạn vừa nhận được ủy quyền từ đ/c Chủ tịch (hc_ct).',
     'DELEGATION'),

    ((SELECT id FROM sys_users WHERE username = 'cd_03'),
     'Yêu cầu bổ sung hồ sơ',
     'Hồ sơ khai tử (HS-HK02-0005) cần bổ sung giấy báo tử bản gốc.',
     'STATUS_UPDATE');
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, dossier_status, submission_date, due_date, form_data)
VALUES
    ('HS-HT02-0003',
     (SELECT id FROM cat_services WHERE service_code = 'HT02_XACNHANHN'),
     (SELECT id FROM sys_departments WHERE dept_code = 'WARD-001'),
     (SELECT id FROM sys_users WHERE username = 'cd_11'),
     (SELECT id FROM sys_users WHERE username = 'hc_pct'),
     'VERIFIED',
     DATE_SUB(NOW(), INTERVAL 2 DAY),
     DATE_ADD(NOW(), INTERVAL 1 DAY),
     '{"requesterFullName": "Võ Thị Lan", "purposeOfUse": "Vay vốn ngân hàng"}'
    );

INSERT INTO ops_dossier_logs (dossier_id, actor_id, action, prev_status, next_status, comments)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HT02-0003'),
     (SELECT id FROM sys_users WHERE username = 'cd_11'),
     'NOP_HO_SO', NULL, 'NEW', NULL),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HT02-0003'),
     (SELECT id FROM sys_users WHERE username = 'hc_mc'),
     'CHUYEN_BUOC', 'NEW', 'PENDING', 'Chuyển Tư pháp rà soát'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HT02-0003'),
     (SELECT id FROM sys_users WHERE username = 'hc_tp'),
     'TRINH_KY', 'PENDING', 'VERIFIED', 'Đã kiểm tra dữ liệu hộ tịch, đủ điều kiện, trình Lãnh đạo ký');

-- 4. HỒ SƠ 4: Đã phê duyệt (APPROVED) - ĐK Kinh doanh
-- Người nộp: cd_01 (Nguyễn Văn An)
-- Cán bộ xử lý: hc_mc (Một cửa - Chờ trả)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, dossier_status, submission_date, due_date, finish_date, form_data)
VALUES
    ('HS-KD01-0004',
     (SELECT id FROM cat_services WHERE service_code = 'KD01_HKD'),
     (SELECT id FROM sys_departments WHERE dept_code = 'WARD-001'),
     (SELECT id FROM sys_users WHERE username = 'cd_01'),
     (SELECT id FROM sys_users WHERE username = 'hc_mc'),
     'APPROVED',
     DATE_SUB(NOW(), INTERVAL 3 DAY),
     DATE_ADD(NOW(), INTERVAL 2 DAY),
     NOW(),
     '{"businessName": "Tạp hóa Cô Bình", "registeredCapital": 50000000}'
    );

INSERT INTO ops_dossier_logs (dossier_id, actor_id, action, prev_status, next_status, comments)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-KD01-0004'),
     (SELECT id FROM sys_users WHERE username = 'cd_01'),
     'NOP_HO_SO', NULL, 'NEW', 'Nộp hồ sơ trực tuyến'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-KD01-0004'),
     (SELECT id FROM sys_users WHERE username = 'hc_mc'),
     'CHUYEN_BUOC', 'NEW', 'PENDING', 'Hồ sơ hợp lệ, chuyển Kinh tế thẩm định'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-KD01-0004'),
     (SELECT id FROM sys_users WHERE username = 'hc_kt'),
     'TRINH_KY', 'PENDING', 'VERIFIED', 'Đủ điều kiện cấp phép'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-KD01-0004'),
     (SELECT id FROM sys_users WHERE username = 'hc_pct'),
     'PHE_DUYET', 'VERIFIED', 'APPROVED', 'Đã ký giấy phép kinh doanh');

-- Tạo kết quả cho HS 4
INSERT INTO ops_dossier_results (dossier_id, decision_number, signer_name, e_file_url)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-KD01-0004'),
     'GPKD-001/2023', 'Trần Thị Bình', '/results/gpkd_001.pdf');

-- 5. HỒ SƠ 5: Bị từ chối (REJECTED) - Khai tử
-- Người nộp: cd_03 (Lê Văn Cường)
-- Trạng thái cuối: REJECTED
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, dossier_status, submission_date, rejection_reason, form_data)
VALUES
    ('HS-HK02-0005',
     (SELECT id FROM cat_services WHERE service_code = 'HK02_KAITU'),
     (SELECT id FROM sys_departments WHERE dept_code = 'WARD-001'),
     (SELECT id FROM sys_users WHERE username = 'cd_03'),
     NULL, -- Kết thúc
     'REJECTED',
     DATE_SUB(NOW(), INTERVAL 1 DAY),
     'Thông tin người mất không khớp với cơ sở dữ liệu quốc gia (Sai số CCCD)',
     '{"deceasedFullName": "Nguyễn Văn X", "dateOfDeath": "2023-12-01"}'
    );

INSERT INTO ops_dossier_logs (dossier_id, actor_id, action, prev_status, next_status, comments)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HK02-0005'),
     (SELECT id FROM sys_users WHERE username = 'cd_03'),
     'NOP_HO_SO', NULL, 'NEW', 'Nộp hồ sơ'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HK02-0005'),
     (SELECT id FROM sys_users WHERE username = 'hc_mc'),
     'CHUYEN_BUOC', 'NEW', 'PENDING', 'Chuyển Tư pháp xác minh'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HK02-0005'),
     (SELECT id FROM sys_users WHERE username = 'hc_tp'),
     'TU_CHOI', 'PENDING', 'REJECTED', 'Sai thông tin nhân thân người mất');


-- 6. HỒ SƠ 6: Đã trả kết quả (RESULT_RETURNED) - Tách thửa
-- Người nộp: cd_05 (Hoàng Văn Em)
-- Đã hoàn thành toàn bộ
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, dossier_status, submission_date, finish_date, form_data)
VALUES
    ('HS-DD03-0006',
     (SELECT id FROM cat_services WHERE service_code = 'DD03_TACHHOP'),
     (SELECT id FROM sys_departments WHERE dept_code = 'WARD-001'),
     (SELECT id FROM sys_users WHERE username = 'cd_05'),
     NULL,
     'RESULT_RETURNED',
     DATE_SUB(NOW(), INTERVAL 20 DAY),
     DATE_SUB(NOW(), INTERVAL 2 DAY),
     '{"landCertificateNumber": "GCN-DN-0003", "numberOfNewPlots": 2}'
    );

INSERT INTO ops_dossier_logs (dossier_id, actor_id, action, prev_status, next_status, comments)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD03-0006'),
     (SELECT id FROM sys_users WHERE username = 'cd_05'),
     'NOP_HO_SO', NULL, 'NEW', 'Nộp hồ sơ tách thửa'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD03-0006'),
     (SELECT id FROM sys_users WHERE username = 'hc_mc'),
     'CHUYEN_BUOC', 'NEW', 'PENDING', 'Chuyển Địa chính đo đạc'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD03-0006'),
     (SELECT id FROM sys_users WHERE username = 'hc_dc'),
     'DO_DAC', 'PENDING', 'PENDING', 'Đã hoàn thành đo đạc thực địa'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD03-0006'),
     (SELECT id FROM sys_users WHERE username = 'hc_dc'),
     'TRINH_KY', 'PENDING', 'VERIFIED', 'Đủ điều kiện tách thửa'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD03-0006'),
     (SELECT id FROM sys_users WHERE username = 'hc_ct'),
     'PHE_DUYET', 'VERIFIED', 'APPROVED', 'Đồng ý tách thửa'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD03-0006'),
     (SELECT id FROM sys_users WHERE username = 'hc_mc'),
     'TRA_KQ', 'APPROVED', 'RESULT_RETURNED', 'Đã trả kết quả cho công dân');

INSERT INTO ops_dossier_results (dossier_id, decision_number, signer_name, e_file_url)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD03-0006'),
     'QD-TACHTHUA-088', 'Nguyễn Văn An', '/results/tachthua_088.pdf');

-- Thanh toán phí cho HS 6
INSERT INTO mod_payments (dossier_id, amount, receipt_number, payment_status, pay_date)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD03-0006'),
     50000.00, 'PAY-009988', 'SUCCESS', DATE_SUB(NOW(), INTERVAL 2 DAY));

-- File đính kèm mẫu
INSERT INTO ops_dossier_files (dossier_id, file_name, file_url, file_type)
SELECT id, 'cmnd_mat_truoc.jpg', '/uploads/cmnd_mt.jpg', 'IMAGE'
FROM ops_dossiers WHERE dossier_code = 'HS-HK01-0001';

INSERT INTO ops_dossier_files (dossier_id, file_name, file_url, file_type)
SELECT id, 'so_do_ban_chinh.pdf', '/uploads/sodo.pdf', 'PDF'
FROM ops_dossiers WHERE dossier_code = 'HS-DD01-0002';


-- =======================================================
-- ADDED: 5 HỒ SƠ CHỜ PHÊ DUYỆT (VERIFIED) CHO CHỦ TỊCH (hc_ct)
-- =======================================================

-- 1. HS-HK01-0020: Khai sinh (VERIFIED)
INSERT INTO ops_dossiers (dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, dossier_status, submission_date, due_date, form_data)
VALUES (
           'HS-HK01-0020',
           (SELECT id FROM cat_services WHERE service_code = 'HK01_TRE'),
           (SELECT id FROM sys_departments WHERE dept_code = 'WARD-001'),
           (SELECT id FROM sys_users WHERE username = 'cd_06'),
           (SELECT id FROM sys_users WHERE username = 'hc_ct'),
           'VERIFIED',
           DATE_SUB(NOW(), INTERVAL 2 DAY),
           DATE_ADD(NOW(), INTERVAL 3 DAY),
           '{"childFullName": "Ngô Văn G", "dateOfBirth": "2023-11-20", "gender": "MALE", "placeOfBirth": "Trạm y tế phường"}'
       );
-- Log
INSERT INTO ops_dossier_logs (dossier_id, actor_id, action, prev_status, next_status, comments)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HK01-0020'), (SELECT id FROM sys_users WHERE username = 'cd_06'), 'NOP_HO_SO', NULL, 'NEW', 'Nộp hồ sơ trực tuyến'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HK01-0020'), (SELECT id FROM sys_users WHERE username = 'hc_mc'), 'CHUYEN_BUOC', 'NEW', 'PENDING', 'Chuyển hồ sơ cho bộ phận chuyên môn'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HK01-0020'), (SELECT id FROM sys_users WHERE username = 'hc_tp'), 'TRINH_KY', 'PENDING', 'VERIFIED', 'Đã thẩm định thông tin khai sinh, trình ký');

-- 2. HS-DD01-0021: Biến động đất đai (VERIFIED)
INSERT INTO ops_dossiers (dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, dossier_status, submission_date, due_date, form_data)
VALUES (
           'HS-DD01-0021',
           (SELECT id FROM cat_services WHERE service_code = 'DD01_BIENDONG'),
           (SELECT id FROM sys_departments WHERE dept_code = 'WARD-001'),
           (SELECT id FROM sys_users WHERE username = 'cd_07'),
           (SELECT id FROM sys_users WHERE username = 'hc_ct'),
           'VERIFIED',
           DATE_SUB(NOW(), INTERVAL 5 DAY),
           DATE_ADD(NOW(), INTERVAL 10 DAY),
           '{"landCertificateNumber": "GCN-DN-0007", "changeType": "Thừa kế", "newOwner": "Bùi Văn Minh muốn sang tên"}'
       );
-- Log
INSERT INTO ops_dossier_logs (dossier_id, actor_id, action, prev_status, next_status, comments)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD01-0021'), (SELECT id FROM sys_users WHERE username = 'cd_07'), 'NOP_HO_SO', NULL, 'NEW', 'Nộp hồ sơ biến động'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD01-0021'), (SELECT id FROM sys_users WHERE username = 'hc_mc'), 'CHUYEN_BUOC', 'NEW', 'PENDING', 'Chuyển hồ sơ cho bộ phận chuyên môn'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-DD01-0021'), (SELECT id FROM sys_users WHERE username = 'hc_dc'), 'TRINH_KY', 'PENDING', 'VERIFIED', 'Đất không tranh chấp, đủ điều kiện sang tên');

-- 3. HS-KD01-0022: Đăng ký kinh doanh (VERIFIED)
INSERT INTO ops_dossiers (dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, dossier_status, submission_date, due_date, form_data)
VALUES (
           'HS-KD01-0022',
           (SELECT id FROM cat_services WHERE service_code = 'KD01_HKD'),
           (SELECT id FROM sys_departments WHERE dept_code = 'WARD-001'),
           (SELECT id FROM sys_users WHERE username = 'cd_08'),
           (SELECT id FROM sys_users WHERE username = 'hc_ct'),
           'VERIFIED',
           DATE_SUB(NOW(), INTERVAL 1 DAY),
           DATE_ADD(NOW(), INTERVAL 2 DAY),
           '{"businessName": "Cửa hàng hoa tươi Ngọc", "registeredCapital": 30000000}'
       );
-- Log
INSERT INTO ops_dossier_logs (dossier_id, actor_id, action, prev_status, next_status, comments)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-KD01-0022'), (SELECT id FROM sys_users WHERE username = 'cd_08'), 'NOP_HO_SO', NULL, 'NEW', 'Nộp hồ sơ ĐKKD'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-KD01-0022'), (SELECT id FROM sys_users WHERE username = 'hc_mc'), 'CHUYEN_BUOC', 'NEW', 'PENDING', 'Chuyển hồ sơ cho bộ phận chuyên môn'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-KD01-0022'), (SELECT id FROM sys_users WHERE username = 'hc_kt'), 'TRINH_KY', 'PENDING', 'VERIFIED', 'Địa điểm kinh doanh hợp lệ');

-- 4. HS-HT01-0023: Kết hôn (VERIFIED)
INSERT INTO ops_dossiers (dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, dossier_status, submission_date, due_date, form_data)
VALUES (
           'HS-HT01-0023',
           (SELECT id FROM cat_services WHERE service_code = 'HT01_KETHON'),
           (SELECT id FROM sys_departments WHERE dept_code = 'WARD-001'),
           (SELECT id FROM sys_users WHERE username = 'cd_09'),
           (SELECT id FROM sys_users WHERE username = 'hc_ct'),
           'VERIFIED',
           DATE_SUB(NOW(), INTERVAL 3 DAY),
           DATE_ADD(NOW(), INTERVAL 1 DAY),
           '{"husbandFullName": "Phan Văn Long", "wifeFullName": "Lê Thị Hồng", "intendedMarriageDate": "2023-12-25"}'
       );
-- Log
INSERT INTO ops_dossier_logs (dossier_id, actor_id, action, prev_status, next_status, comments)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HT01-0023'), (SELECT id FROM sys_users WHERE username = 'cd_09'), 'NOP_HO_SO', NULL, 'NEW', 'Đăng ký kết hôn'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HT01-0023'), (SELECT id FROM sys_users WHERE username = 'hc_mc'), 'CHUYEN_BUOC', 'NEW', 'PENDING', 'Chuyển hồ sơ cho bộ phận chuyên môn'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HT01-0023'), (SELECT id FROM sys_users WHERE username = 'hc_tp'), 'TRINH_KY', 'PENDING', 'VERIFIED', 'Hai bên đủ điều kiện kết hôn');

-- 5. HS-HK02-0024: Khai tử (VERIFIED)
INSERT INTO ops_dossiers (dossier_code, service_id, receiving_dept_id, applicant_id, current_handler_id, dossier_status, submission_date, due_date, form_data)
VALUES (
           'HS-HK02-0024',
           (SELECT id FROM cat_services WHERE service_code = 'HK02_KAITU'),
           (SELECT id FROM sys_departments WHERE dept_code = 'WARD-001'),
           (SELECT id FROM sys_users WHERE username = 'cd_10'),
           (SELECT id FROM sys_users WHERE username = 'hc_ct'),
           'VERIFIED',
           DATE_SUB(NOW(), INTERVAL 2 DAY),
           DATE_ADD(NOW(), INTERVAL 5 DAY),
           '{"deceasedFullName": "Trương Văn Cụ", "dateOfDeath": "2023-12-15", "placeOfDeath": "Tại nhà"}'
       );
-- Log
INSERT INTO ops_dossier_logs (dossier_id, actor_id, action, prev_status, next_status, comments)
VALUES
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HK02-0024'), (SELECT id FROM sys_users WHERE username = 'cd_10'), 'NOP_HO_SO', NULL, 'NEW', 'Báo tử'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HK02-0024'), (SELECT id FROM sys_users WHERE username = 'hc_mc'), 'CHUYEN_BUOC', 'NEW', 'PENDING', 'Chuyển hồ sơ cho bộ phận chuyên môn'),
    ((SELECT id FROM ops_dossiers WHERE dossier_code = 'HS-HK02-0024'), (SELECT id FROM sys_users WHERE username = 'hc_tp'), 'TRINH_KY', 'PENDING', 'VERIFIED', 'Đã kiểm tra giấy báo tử, hợp lệ');



