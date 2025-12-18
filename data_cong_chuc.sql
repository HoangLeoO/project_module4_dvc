-- Thêm cấp 1: Thành phố
use egov_db;

INSERT INTO sys_departments (dept_code, dept_name, level)
VALUES ('CITY-001', 'Thành phố Đà Nẵng', 1);

-- Lấy id của thành phố vừa tạo để làm parent_id cho các phường/xã
SET @city_id = LAST_INSERT_ID();

-- Thêm cấp 2: Phường/Xã
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
 'Cán bộ Kinh tế - Cấp phép kinh doanh, hộ kinh doanh, chợ');


INSERT INTO mock_citizens
(cccd, full_name, dob, gender, hometown, ethnic_group, religion,
 permanent_address, temporary_address, fingerprint_data, avatar_url,
 marital_status, is_deceased)
VALUES ('012345679001', 'Nguyễn Văn An', '1980-05-12', 'MALE', 'Đà Nẵng', 'Kinh', 'Không',
        'Phường Hải Châu, TP Đà Nẵng', 'Phường Hải Châu, TP Đà Nẵng', 'FP_001', '/avatars/1.png', 'MARRIED', FALSE),

       ('012345679002', 'Trần Thị Bình', '1983-08-21', 'FEMALE', 'Quảng Nam', 'Kinh', 'Không',
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

       ('012345679008', 'Võ Thị Lan', '2000-12-01', 'FEMALE', 'Đà Nẵng', 'Kinh', 'Không',
        'Phường Hải Châu, TP Đà Nẵng', NULL, 'FP_008', '/avatars/8.png', 'SINGLE', FALSE),

       ('012345679009', 'Bùi Văn Minh', '1998-06-06', 'MALE', 'Quảng Bình', 'Kinh', 'Không',
        'Phường Thanh Khê, TP Đà Nẵng', NULL, 'FP_009', '/avatars/9.png', 'SINGLE', FALSE),

       ('012345679010', 'Đỗ Thị Ngọc', '1970-10-10', 'FEMALE', 'Huế', 'Kinh', 'Công giáo',
        'Phường Hòa Cường, TP Đà Nẵng', NULL, 'FP_010', '/avatars/10.png', 'MARRIED', FALSE),

       ('012345679011', 'Nguyễn Văn Phúc', '1945-01-01', 'MALE', 'Đà Nẵng', 'Kinh', 'Không',
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
    ('tk_ct',  '$2a$10$tk_ct',  'Bùi Văn Minh',   'OFFICIAL', 7, 4),
    ('tk_pct', '$2a$10$tk_pct', 'Đỗ Thị Ngọc',    'OFFICIAL', 8, 4),
    ('tk_mc',  '$2a$10$tk_mc',  'Phan Văn Long',  'OFFICIAL', 9, 4),
    ('tk_tp',  '$2a$10$tk_tp',  'Trương Thị Quỳnh','OFFICIAL',10,4),
    ('tk_dc',  '$2a$10$tk_dc',  'Võ Thị Lan',     'OFFICIAL',11,4),
    ('tk_kt',  '$2a$10$tk_kt',  'Lương Văn Quân', 'OFFICIAL',12,4);

INSERT INTO sys_users
(username, password_hash, full_name, user_type, citizen_id)
VALUES
    ('cd_07', '$2a$10$cd7', 'Bùi Văn Minh',    'CITIZEN', 7),
    ('cd_08', '$2a$10$cd8', 'Đỗ Thị Ngọc',     'CITIZEN', 8),
    ('cd_09', '$2a$10$cd9', 'Phan Văn Long',   'CITIZEN', 9),
    ('cd_10', '$2a$10$cd10','Trương Thị Quỳnh','CITIZEN',10),
    ('cd_11', '$2a$10$cd11','Võ Thị Lan',      'CITIZEN',11),
    ('cd_12', '$2a$10$cd12','Lương Văn Quân',  'CITIZEN',12);

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
INSERT INTO cat_services (service_code, service_name, domain, sla_hours, fee_amount, form_schema)
VALUES ('HK01_TRE',
        'Đăng ký khai sinh, đăng ký thường trú, cấp thẻ bảo hiểm y tế cho trẻ em dưới 6 tuổi',
        'HỘ TỊCH',
        120, -- 5 ngày làm việc
        0.00,
        NULL);

-- 2. Đăng ký kết hôn
INSERT INTO cat_services (service_code, service_name, domain, sla_hours, fee_amount, form_schema)
VALUES ('HT01_KETHON',
        'Thủ tục đăng ký kết hôn',
        'HỘ TỊCH',
        72, -- 3 ngày làm việc
        100000.00,
        NULL);

-- 3. Đăng ký khai tử, xóa thường trú, chế độ mai táng/tử tuất
INSERT INTO cat_services (service_code, service_name, domain, sla_hours, fee_amount, form_schema)
VALUES ('HK02_KAITU',
        'Đăng ký khai tử, xóa đăng ký thường trú, giải quyết mai táng phí, tử tuất',
        'HỘ TỊCH',
        168, -- 7 ngày làm việc
        0.00,
        NULL);

-- 4. Cấp Giấy xác nhận tình trạng hôn nhân
INSERT INTO cat_services (service_code, service_name, domain, sla_hours, fee_amount,
                          form_schema)
VALUES ('HT02_XACNHANHN',
        'Thủ tục cấp Giấy xác nhận tình trạng hôn nhân',
        'HỘ TỊCH',
        48, -- 2 ngày làm việc
        30000.00,
        NULL);

-- 5. Đăng ký biến động đất đai do thay đổi quyền sử dụng (hộ/vợ chồng)
INSERT INTO cat_services (service_code, service_name, domain, sla_hours, fee_amount,
                          form_schema)
VALUES ('DD01_BIENDONG',
        'Đăng ký biến động đối với trường hợp thay đổi quyền sử dụng đất, quyền sở hữu tài sản gắn liền với đất theo thỏa thuận của các thành viên hộ gia đình hoặc của vợ và chồng',
        'ĐẤT ĐAI',
        360, -- Khoảng 15 ngày làm việc
        50000.00,
        NULL);

-- 6. Chuyển mục đích sử dụng đất
INSERT INTO cat_services (service_code, service_name, domain, sla_hours, fee_amount,
                          form_schema)
VALUES ('DD02_CHUYENMDSD',
        'Thủ tục chuyển mục đích sử dụng đất phải được phép của cơ quan nhà nước có thẩm quyền đối với hộ gia đình, cá nhân',
        'ĐẤT ĐAI',
        480, -- Khoảng 20 ngày làm việc
        500000.00,
        NULL);

-- 7. Tách thửa hoặc hợp thửa đất
INSERT INTO cat_services (service_code, service_name, domain, sla_hours, fee_amount,
                          form_schema)
VALUES ('DD03_TACHHOP',
        'Thủ tục tách thửa hoặc hợp thửa đất',
        'ĐẤT ĐAI',
        360, -- Khoảng 15 ngày làm việc
        50000.00,
        NULL);

-- 8. Đăng ký thành lập hộ kinh doanh
INSERT INTO cat_services (service_code, service_name, domain, sla_hours, fee_amount, form_schema)
VALUES ('KD01_HKD',
        'Đăng ký thành lập hộ kinh doanh',
        'KINH DOANH',
        72, -- 3 ngày làm việc
        50000.00,
        NULL);

# 1️⃣ HK01_TRE – Khai sinh + thường trú + BHYT
INSERT INTO cat_workflow_steps (service_id, step_name, step_order, role_required_id)
SELECT 1, 'Tiếp nhận hồ sơ', 1, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT 1, 'Kiểm tra hồ sơ hộ tịch', 2, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_TU_PHAP'
UNION ALL
SELECT 1, 'Xác minh thông tin cư trú', 3, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_TU_PHAP'
UNION ALL
SELECT 1, 'Phê duyệt kết quả', 4, r.id FROM sys_roles r WHERE r.role_name = 'PHO_CHU_TICH_UBND'
UNION ALL
SELECT 1, 'Trả kết quả', 5, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA';

# 2️⃣ HT01_KETHON – Đăng ký kết hôn
INSERT INTO cat_workflow_steps(service_id, step_name, step_order, role_required_id)
SELECT 2, 'Tiếp nhận hồ sơ', 1, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT 2, 'Thẩm tra điều kiện kết hôn', 2, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_TU_PHAP'
UNION ALL
SELECT 2, 'Phê duyệt kết hôn', 3, r.id FROM sys_roles r WHERE r.role_name = 'CHU_TICH_UBND'
UNION ALL
SELECT 2, 'Trả Giấy chứng nhận kết hôn', 4, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA';


# 3️⃣ HK02_KAITU – Khai tử
INSERT INTO cat_workflow_steps(service_id, step_name, step_order, role_required_id)
SELECT 3, 'Tiếp nhận hồ sơ', 1, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT 3, 'Xác minh thông tin khai tử', 2, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_TU_PHAP'
UNION ALL
SELECT 3, 'Phê duyệt khai tử', 3, r.id FROM sys_roles r WHERE r.role_name = 'PHO_CHU_TICH_UBND'
UNION ALL
SELECT 3, 'Cập nhật dữ liệu dân cư', 4, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_TU_PHAP'
UNION ALL
SELECT 3, 'Trả kết quả', 5, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA';

# 4️⃣ HT02_XACNHANHN – Xác nhận tình trạng hôn nhân
INSERT INTO cat_workflow_steps(service_id, step_name, step_order, role_required_id)
SELECT 4, 'Tiếp nhận hồ sơ', 1, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT 4, 'Đối soát dữ liệu hôn nhân', 2, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_TU_PHAP'
UNION ALL
SELECT 4, 'Phê duyệt xác nhận', 3, r.id FROM sys_roles r WHERE r.role_name = 'PHO_CHU_TICH_UBND'
UNION ALL
SELECT 4, 'Trả giấy xác nhận', 4, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA';

# 5️⃣ DD01_BIENDONG – Biến động đất đai
INSERT INTO cat_workflow_steps(service_id, step_name, step_order, role_required_id)
SELECT 5, 'Tiếp nhận hồ sơ', 1, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT 5, 'Kiểm tra pháp lý đất đai', 2, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT 5, 'Lấy ý kiến liên quan', 3, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT 5, 'Phê duyệt biến động', 4, r.id FROM sys_roles r WHERE r.role_name = 'CHU_TICH_UBND'
UNION ALL
SELECT 5, 'Cập nhật hồ sơ địa chính', 5, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT 5, 'Trả kết quả', 6, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA';

# 6️⃣ DD02_CHUYENMDSD – Chuyển mục đích sử dụng đất
INSERT INTO cat_workflow_steps(service_id, step_name, step_order, role_required_id)
SELECT 6, 'Tiếp nhận hồ sơ', 1, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT 6, 'Thẩm định nhu cầu sử dụng đất', 2, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT 6, 'Lấy ý kiến quy hoạch', 3, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT 6, 'Phê duyệt chuyển mục đích', 4, r.id FROM sys_roles r WHERE r.role_name = 'CHU_TICH_UBND'
UNION ALL
SELECT 6, 'Cập nhật hồ sơ địa chính', 5, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT 6, 'Trả kết quả', 6, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA';

# 7️⃣ DD03_TACHHOP – Tách / hợp thửa
INSERT INTO cat_workflow_steps(service_id, step_name, step_order, role_required_id)
SELECT 7, 'Tiếp nhận hồ sơ', 1, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT 7, 'Kiểm tra điều kiện tách/hợp thửa', 2, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT 7, 'Đo đạc và cập nhật bản đồ', 3, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_DIA_CHINH'
UNION ALL
SELECT 7, 'Phê duyệt kết quả', 4, r.id FROM sys_roles r WHERE r.role_name = 'CHU_TICH_UBND'
UNION ALL
SELECT 7, 'Trả kết quả', 5, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA';

# 8️⃣ KD01_HKD – Hộ kinh doanh
INSERT INTO cat_workflow_steps(service_id, step_name, step_order, role_required_id)
SELECT 8, 'Tiếp nhận hồ sơ', 1, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA'
UNION ALL
SELECT 8, 'Thẩm tra thông tin kinh doanh', 2, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_KINH_TE'
UNION ALL
SELECT 8, 'Phê duyệt đăng ký', 3, r.id FROM sys_roles r WHERE r.role_name = 'PHO_CHU_TICH_UBND'
UNION ALL
SELECT 8, 'Cấp Giấy chứng nhận HKD', 4, r.id FROM sys_roles r WHERE r.role_name = 'CANBO_MOTCUA';


INSERT INTO cat_templates
(service_id, template_name, file_path, variable_mapping)
VALUES
-- =====================================================
-- 1. KHAI SINH – HK01_TRE
-- =====================================================
(1,
 'Giấy khai sinh',
 '/templates/hokhau/giay_khai_sinh.docx',
 JSON_OBJECT(
         'fullName', 'citizen.full_name',
         'dateOfBirth', 'citizen.date_of_birth',
         'gender', 'citizen.gender',
         'placeOfBirth', 'citizen.place_of_birth',
         'fatherName', 'father.full_name',
         'motherName', 'mother.full_name',
         'registerDate', 'dossier.approved_at'
 )),

-- =====================================================
-- 2. KẾT HÔN – HT01_KETHON
-- =====================================================
(2,
 'Giấy chứng nhận kết hôn',
 '/templates/hotich/giay_ket_hon.docx',
 JSON_OBJECT(
         'husbandName', 'husband.full_name',
         'wifeName', 'wife.full_name',
         'husbandDob', 'husband.date_of_birth',
         'wifeDob', 'wife.date_of_birth',
         'marriageDate', 'dossier.approved_at',
         'registerOffice', 'dept.dept_name'
 )),

-- =====================================================
-- 3. KHAI TỬ – HK02_KAITU
-- =====================================================
(3,
 'Giấy chứng tử',
 '/templates/hokhau/giay_chung_tu.docx',
 JSON_OBJECT(
         'fullName', 'citizen.full_name',
         'dateOfDeath', 'death.date',
         'reasonOfDeath', 'death.reason',
         'placeOfDeath', 'death.place',
         'registerDate', 'dossier.approved_at'
 )),

-- =====================================================
-- 4. XÁC NHẬN TÌNH TRẠNG HÔN NHÂN – HT02_XACNHANHN
-- =====================================================
(4,
 'Giấy xác nhận tình trạng hôn nhân',
 '/templates/hotich/xac_nhan_hon_nhan.docx',
 JSON_OBJECT(
         'fullName', 'citizen.full_name',
         'dateOfBirth', 'citizen.date_of_birth',
         'currentStatus', 'marital.status',
         'validUntil', 'certificate.expired_at',
         'issueDate', 'dossier.approved_at'
 )),

-- =====================================================
-- 5. BIẾN ĐỘNG ĐẤT ĐAI – DD01_BIENDONG
-- =====================================================
(5,
 'Quyết định biến động đất đai',
 '/templates/datdai/quyet_dinh_bien_dong.docx',
 JSON_OBJECT(
         'ownerName', 'land.owner_name',
         'landPlotNo', 'land.plot_no',
         'landMapNo', 'land.map_no',
         'changeType', 'land.change_type',
         'decisionDate', 'dossier.approved_at'
 )),

-- =====================================================
-- 6. CHUYỂN MỤC ĐÍCH SỬ DỤNG ĐẤT – DD02_CHUYENMDSD
-- =====================================================
(6,
 'Quyết định cho phép chuyển mục đích sử dụng đất',
 '/templates/datdai/quyet_dinh_chuyen_md.docx',
 JSON_OBJECT(
         'ownerName', 'land.owner_name',
         'currentPurpose', 'land.current_purpose',
         'newPurpose', 'land.new_purpose',
         'area', 'land.area',
         'decisionDate', 'dossier.approved_at'
 )),

-- =====================================================
-- 7. TÁCH / HỢP THỬA – DD03_TACHHOP
-- =====================================================
(7,
 'Quyết định tách/hợp thửa đất',
 '/templates/datdai/quyet_dinh_tach_hop.docx',
 JSON_OBJECT(
         'ownerName', 'land.owner_name',
         'oldPlotInfo', 'land.old_plot',
         'newPlotInfo', 'land.new_plot',
         'decisionDate', 'dossier.approved_at'
 )),

-- =====================================================
-- 8. HỘ KINH DOANH – KD01_HKD
-- =====================================================
(8,
 'Giấy chứng nhận đăng ký hộ kinh doanh',
 '/templates/kinhdoanh/giay_dk_hkd.docx',
 JSON_OBJECT(
         'businessName', 'business.business_name',
         'taxCode', 'business.tax_code',
         'ownerName', 'business.owner_name',
         'address', 'business.address',
         'issueDate', 'dossier.approved_at'
 ));

INSERT INTO cat_knowledge_base (service_id, title, content)
VALUES
-- =====================================================
-- TRI THỨC CHUNG (service_id = NULL)
-- =====================================================
(NULL,
 'Hướng dẫn nộp hồ sơ dịch vụ công trực tuyến',
 'Người dân đăng nhập hệ thống, chọn dịch vụ công phù hợp, điền thông tin theo biểu mẫu điện tử, đính kèm giấy tờ cần thiết và nộp hồ sơ. Sau khi nộp, có thể theo dõi trạng thái xử lý tại mục "Hồ sơ của tôi".'),
(NULL,
 'Các trạng thái xử lý hồ sơ dịch vụ công',
 'Hồ sơ dịch vụ công bao gồm các trạng thái: Tiếp nhận → Thẩm tra → Xử lý nghiệp vụ → Phê duyệt → Trả kết quả. Người dân sẽ nhận thông báo khi hồ sơ thay đổi trạng thái.'),

-- =====================================================
-- 1. KHAI SINH – HK01_TRE
-- =====================================================
(1,
 'Thủ tục đăng ký khai sinh cho trẻ em',
 'Đăng ký khai sinh cho trẻ em dưới 6 tuổi bao gồm việc cấp Giấy khai sinh, đăng ký thường trú và cấp thẻ BHYT. Hồ sơ cần nộp trong vòng 60 ngày kể từ ngày sinh.'),
(1,
 'Hồ sơ đăng ký khai sinh gồm những gì?',
 'Giấy chứng sinh, CCCD của cha/mẹ, sổ hộ khẩu hoặc giấy xác nhận cư trú, tờ khai đăng ký khai sinh theo mẫu.'),

-- =====================================================
-- 2. KẾT HÔN – HT01_KETHON
-- =====================================================
(2,
 'Điều kiện đăng ký kết hôn',
 'Nam từ đủ 20 tuổi, nữ từ đủ 18 tuổi, việc kết hôn do nam nữ tự nguyện quyết định và không thuộc các trường hợp cấm kết hôn theo quy định pháp luật.'),
(2,
 'Hồ sơ đăng ký kết hôn',
 'CCCD của hai bên nam nữ, Giấy xác nhận tình trạng hôn nhân (nếu đăng ký khác nơi thường trú).'),

-- =====================================================
-- 3. KHAI TỬ – HK02_KAITU
-- =====================================================
(3,
 'Thủ tục đăng ký khai tử',
 'Thân nhân người đã mất có trách nhiệm đăng ký khai tử trong thời hạn 15 ngày kể từ ngày người đó chết.'),
(3,
 'Hồ sơ đăng ký khai tử',
 'Giấy báo tử hoặc giấy tờ thay thế, CCCD của người đi khai tử.'),

-- =====================================================
-- 4. XÁC NHẬN TÌNH TRẠNG HÔN NHÂN
-- =====================================================
(4,
 'Giấy xác nhận tình trạng hôn nhân dùng để làm gì?',
 'Giấy xác nhận tình trạng hôn nhân được sử dụng để đăng ký kết hôn, mua bán nhà đất hoặc thực hiện các giao dịch dân sự khác.'),

-- =====================================================
-- 5. BIẾN ĐỘNG ĐẤT ĐAI
-- =====================================================
(5,
 'Các trường hợp đăng ký biến động đất đai',
 'Thay đổi người sử dụng đất, thay đổi thông tin thửa đất, thay đổi mục đích sử dụng đất theo thỏa thuận hoặc theo quyết định của cơ quan nhà nước.'),

-- =====================================================
-- 6. CHUYỂN MỤC ĐÍCH SỬ DỤNG ĐẤT
-- =====================================================
(6,
 'Điều kiện chuyển mục đích sử dụng đất',
 'Việc chuyển mục đích sử dụng đất phải phù hợp quy hoạch, kế hoạch sử dụng đất và được cơ quan nhà nước có thẩm quyền cho phép.'),

-- =====================================================
-- 7. TÁCH / HỢP THỬA
-- =====================================================
(7,
 'Khi nào được tách thửa đất?',
 'Việc tách thửa phải đảm bảo diện tích tối thiểu theo quy định của UBND cấp tỉnh và phù hợp với quy hoạch.'),

-- =====================================================
-- 8. ĐĂNG KÝ HỘ KINH DOANH
-- =====================================================
(8,
 'Điều kiện đăng ký hộ kinh doanh',
 'Cá nhân hoặc nhóm cá nhân là công dân Việt Nam đủ 18 tuổi, có năng lực hành vi dân sự đầy đủ.'),
(8,
 'Hồ sơ đăng ký hộ kinh doanh',
 'Đơn đăng ký hộ kinh doanh, CCCD của chủ hộ kinh doanh, địa điểm kinh doanh hợp pháp.');


# 1. Hồ sơ NEW – vừa nộp (Khai sinh)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id, dossier_status, form_data)
SELECT
    'HS_TRE_001',
    s.id,
    d.id,
    u.id,
    'NEW',
    JSON_OBJECT(
            'child_name','Nguyễn Minh An',
            'dob','2025-01-10',
            'father','Nguyễn Văn An',
            'mother','Trần Thị Bình'
    )
FROM cat_services s
         JOIN sys_users u ON u.username = 'cd_01'
         JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'HK01_TRE';

# 2. Hồ sơ PENDING – đang xử lý (Đăng ký kết hôn)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id,
 current_handler_id, dossier_status, due_date, form_data)
SELECT
    'HS_KH_001',
    s.id,
    d.id,
    app.id,
    cb.id,
    'PENDING',
    DATE_ADD(NOW(), INTERVAL s.sla_hours HOUR),
    JSON_OBJECT(
            'husband','Nguyễn Văn An',
            'wife','Trần Thị Bình',
            'marriage_date','2025-02-01'
    )
FROM cat_services s
         JOIN sys_users app ON app.username = 'cd_01'
         JOIN sys_users cb ON cb.username = 'hc_tp'
         JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'HT01_KETHON';

# 3. Hồ sơ APPROVED – đã hoàn tất (Xác nhận tình trạng hôn nhân)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id,
 current_handler_id, dossier_status, finish_date, form_data)
SELECT
    'HS_XNHN_001',
    s.id,
    d.id,
    app.id,
    cb.id,
    'APPROVED',
    NOW(),
    JSON_OBJECT(
            'requester','Nguyễn Văn An',
            'purpose','Bổ sung hồ sơ vay vốn'
    )
FROM cat_services s
         JOIN sys_users app ON app.username = 'cd_01'
         JOIN sys_users cb ON cb.username = 'hc_tp'
         JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'HT02_XACNHANHN';

# 4. Hồ sơ REJECTED – bị trả lại (Khai tử)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id,
 current_handler_id, dossier_status, finish_date, rejection_reason, form_data)
SELECT
    'HS_KT_001',
    s.id,
    d.id,
    app.id,
    cb.id,
    'REJECTED',
    NOW(),
    'Thiếu giấy chứng tử hợp lệ',
    JSON_OBJECT(
            'deceased_name','Nguyễn Văn C',
            'death_date','2024-12-20'
    )
FROM cat_services s
         JOIN sys_users app ON app.username = 'cd_02'
         JOIN sys_users cb ON cb.username = 'hc_tp'
         JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'HK02_KAITU';

# 5. Hồ sơ đất đai – đang xử lý (Biến động đất đai)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id,
 current_handler_id, dossier_status, due_date, form_data)
SELECT
    'HS_DD_001',
    s.id,
    d.id,
    app.id,
    cb.id,
    'PENDING',
    DATE_ADD(NOW(), INTERVAL s.sla_hours HOUR),
    JSON_OBJECT(
            'certificate_number','SR123456',
            'change_reason','Chia tài sản sau hôn nhân'
    )
FROM cat_services s
         JOIN sys_users app ON app.username = 'cd_05'
         JOIN sys_users cb ON cb.username = 'hc_dc'
         JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'DD01_BIENDONG';

# 6. Hồ sơ kinh doanh – APPROVED (Hộ kinh doanh)
INSERT INTO ops_dossiers
(dossier_code, service_id, receiving_dept_id, applicant_id,
 current_handler_id, dossier_status, finish_date, form_data)
SELECT
    'HS_KD_001',
    s.id,
    d.id,
    app.id,
    cb.id,
    'APPROVED',
    NOW(),
    JSON_OBJECT(
            'business_name','Hộ kinh doanh Minh An',
            'business_line','Bán lẻ tạp hóa'
    )
FROM cat_services s
         JOIN sys_users app ON app.username = 'cd_06'
         JOIN sys_users cb ON cb.username = 'hc_kt'
         JOIN sys_departments d ON d.dept_name = 'Phường Hải Châu'
WHERE s.service_code = 'KD01_HKD';

# III. KIỂM TRA TỔNG HỢP
SELECT
    o.dossier_code,
    cs.service_name,
    o.dossier_status,
    u.full_name AS applicant,
    h.full_name AS handler,
    d.dept_name
FROM ops_dossiers o
         JOIN cat_services cs ON o.service_id = cs.id
         JOIN sys_users u ON o.applicant_id = u.id
         LEFT JOIN sys_users h ON o.current_handler_id = h.id
         JOIN sys_departments d ON o.receiving_dept_id = d.id
ORDER BY o.submission_date;


# 1️⃣ Hồ sơ khai sinh trẻ em – HS_TRE_001
INSERT INTO ops_dossier_files (dossier_id, file_name, file_url, file_type)
SELECT o.id,
       'Giay_chung_sinh.pdf',
       '/uploads/HS_TRE_001/giay_chung_sinh.pdf',
       'PDF'
FROM ops_dossiers o
WHERE o.dossier_code = 'HS_TRE_001';

INSERT INTO ops_dossier_files (dossier_id, file_name, file_url, file_type)
SELECT o.id,
       'CCCD_cha_me.jpg',
       '/uploads/HS_TRE_001/cccd_cha_me.jpg',
       'IMG'
FROM ops_dossiers o
WHERE o.dossier_code = 'HS_TRE_001';

# 2️⃣ Hồ sơ đăng ký kết hôn – HS_KH_001
INSERT INTO ops_dossier_files(dossier_id, file_name, file_url, file_type)
SELECT o.id,
       'To_khai_dang_ky_ket_hon.pdf',
       '/uploads/HS_KH_001/to_khai_ket_hon.pdf',
       'PDF'
FROM ops_dossiers o
WHERE o.dossier_code = 'HS_KH_001';

INSERT INTO ops_dossier_files(dossier_id, file_name, file_url, file_type)
SELECT o.id,
       'Giay_xac_nhan_tinh_trang_hon_nhan.pdf',
       '/uploads/HS_KH_001/xac_nhan_hon_nhan.pdf',
       'PDF'
FROM ops_dossiers o
WHERE o.dossier_code = 'HS_KH_001';

# 3️⃣ Hồ sơ xác nhận tình trạng hôn nhân – HS_XNHN_001
INSERT INTO ops_dossier_files(dossier_id, file_name, file_url, file_type)
SELECT o.id,
       'Don_xin_xac_nhan.pdf',
       '/uploads/HS_XNHN_001/don_xac_nhan.pdf',
       'PDF'
FROM ops_dossiers o
WHERE o.dossier_code = 'HS_XNHN_001';

# 4️⃣ Hồ sơ khai tử (bị từ chối) – HS_KT_001
INSERT INTO ops_dossier_files(dossier_id, file_name, file_url, file_type)
SELECT o.id,
       'Giay_bao_tu.jpg',
       '/uploads/HS_KT_001/giay_bao_tu.jpg',
       'IMG'
FROM ops_dossiers o
WHERE o.dossier_code = 'HS_KT_001';

# 5️⃣ Hồ sơ biến động đất đai – HS_DD_001
INSERT INTO ops_dossier_files(dossier_id, file_name, file_url, file_type)
SELECT o.id,
       'So_do_do.pdf',
       '/uploads/HS_DD_001/so_do_do.pdf',
       'PDF'
FROM ops_dossiers o
WHERE o.dossier_code = 'HS_DD_001';

INSERT INTO ops_dossier_files(dossier_id, file_name, file_url, file_type)
SELECT o.id,
       'Hop_dong_chia_tai_san.pdf',
       '/uploads/HS_DD_001/hop_dong_chia_tai_san.pdf',
       'PDF'
FROM ops_dossiers o
WHERE o.dossier_code = 'HS_DD_001';

# 6️⃣ Hồ sơ hộ kinh doanh – HS_KD_001
INSERT INTO ops_dossier_files(dossier_id, file_name, file_url, file_type)
SELECT o.id,
       'Don_dang_ky_HKD.pdf',
       '/uploads/HS_KD_001/don_dang_ky_hkd.pdf',
       'PDF'
FROM ops_dossiers o
WHERE o.dossier_code = 'HS_KD_001';

INSERT INTO ops_dossier_files(dossier_id, file_name, file_url, file_type)
SELECT o.id,
       'CCCD_chu_ho_kinh_doanh.jpg',
       '/uploads/HS_KD_001/cccd_chu_hkd.jpg',
       'IMG'
FROM ops_dossiers o
WHERE o.dossier_code = 'HS_KD_001';

# III. KIỂM TRA KẾT QUẢ
SELECT
    o.dossier_code,
    f.file_name,
    f.file_type,
    f.file_url
FROM ops_dossier_files f
         JOIN ops_dossiers o ON f.dossier_id = o.id
ORDER BY o.dossier_code;

# 1️⃣ HS_TRE_001 – Hồ sơ mới nộp (NEW)
INSERT INTO ops_dossier_logs
(dossier_id, actor_id, action, prev_status, next_status, comments)
SELECT d.id, u.id, 'TIEP_NHAN', NULL, 'NEW',
       'Hồ sơ được tiếp nhận qua cổng dịch vụ công'
FROM ops_dossiers d
         JOIN sys_users u ON u.username = 'hc_mc'
WHERE d.dossier_code = 'HS_TRE_001';

# 2️⃣ HS_KH_001 – Đang xử lý (PENDING)
-- Tiếp nhận
INSERT INTO ops_dossier_logs(dossier_id, actor_id, action, prev_status, next_status, comments)
SELECT d.id, u.id, 'TIEP_NHAN', 'NEW', 'PENDING',
       'Tiếp nhận hồ sơ đăng ký kết hôn'
FROM ops_dossiers d
         JOIN sys_users u ON u.username = 'hc_mc'
WHERE d.dossier_code = 'HS_KH_001';

-- Thẩm định
INSERT INTO ops_dossier_logs(dossier_id, actor_id, action, prev_status, next_status, comments)
SELECT d.id, u.id, 'THAM_DINH', 'PENDING', 'PENDING',
       'Hồ sơ hợp lệ, đủ điều kiện kết hôn'
FROM ops_dossiers d
         JOIN sys_users u ON u.username = 'hc_tp'
WHERE d.dossier_code = 'HS_KH_001';

-- Phê duyệt
INSERT INTO ops_dossier_logs(dossier_id, actor_id, action, prev_status, next_status, comments)
SELECT d.id, u.id, 'PHE_DUYET', 'PENDING', 'APPROVED',
       'Chấp thuận đăng ký kết hôn'
FROM ops_dossiers d
         JOIN sys_users u ON u.username = 'hc_ct'
WHERE d.dossier_code = 'HS_KH_001';

# 3️⃣ HS_XNHN_001 – Đã hoàn tất (APPROVED)
INSERT INTO ops_dossier_logs(dossier_id, actor_id, action, prev_status, next_status, comments)
SELECT d.id, u.id, 'THAM_DINH', 'PENDING', 'PENDING',
       'Đối soát dữ liệu hôn nhân trên hệ thống'
FROM ops_dossiers d
         JOIN sys_users u ON u.username = 'hc_tp'
WHERE d.dossier_code = 'HS_XNHN_001';

INSERT INTO ops_dossier_logs(dossier_id, actor_id, action, prev_status, next_status, comments)
SELECT d.id, u.id, 'PHE_DUYET', 'PENDING', 'APPROVED',
       'Phê duyệt cấp giấy xác nhận tình trạng hôn nhân'
FROM ops_dossiers d
         JOIN sys_users u ON u.username = 'hc_pct'
WHERE d.dossier_code = 'HS_XNHN_001';

INSERT INTO ops_dossier_logs(dossier_id, actor_id, action, prev_status, next_status, comments)
SELECT d.id, u.id, 'TRA_KET_QUA', 'APPROVED', 'APPROVED',
       'Đã trả kết quả cho công dân'
FROM ops_dossiers d
         JOIN sys_users u ON u.username = 'hc_mc'
WHERE d.dossier_code = 'HS_XNHN_001';

# 4️⃣ HS_KT_001 – Hồ sơ bị từ chối (REJECTED)
INSERT INTO ops_dossier_logs(dossier_id, actor_id, action, prev_status, next_status, comments)
SELECT d.id, u.id, 'THAM_DINH', 'PENDING', 'PENDING',
       'Phát hiện thiếu giấy chứng tử hợp lệ'
FROM ops_dossiers d
         JOIN sys_users u ON u.username = 'hc_tp'
WHERE d.dossier_code = 'HS_KT_001';

INSERT INTO ops_dossier_logs(dossier_id, actor_id, action, prev_status, next_status, comments)
SELECT d.id, u.id, 'TU_CHOI', 'PENDING', 'REJECTED',
       'Hồ sơ không đủ điều kiện xử lý'
FROM ops_dossiers d
         JOIN sys_users u ON u.username = 'hc_pct'
WHERE d.dossier_code = 'HS_KT_001';

# 5️⃣ HS_DD_001 – Đất đai (đang xử lý)
INSERT INTO ops_dossier_logs(dossier_id, actor_id, action, prev_status, next_status, comments)
SELECT d.id, u.id, 'THAM_DINH', 'PENDING', 'PENDING',
       'Kiểm tra pháp lý thửa đất'
FROM ops_dossiers d
         JOIN sys_users u ON u.username = 'hc_dc'
WHERE d.dossier_code = 'HS_DD_001';

# 6️⃣ HS_KD_001 – Hộ kinh doanh (hoàn tất)
INSERT INTO ops_dossier_logs(dossier_id, actor_id, action, prev_status, next_status, comments)
SELECT d.id, u.id, 'THAM_DINH', 'PENDING', 'PENDING',
       'Thẩm tra thông tin đăng ký hộ kinh doanh'
FROM ops_dossiers d
         JOIN sys_users u ON u.username = 'hc_kt'
WHERE d.dossier_code = 'HS_KD_001';

INSERT INTO ops_dossier_logs(dossier_id, actor_id, action, prev_status, next_status, comments)
SELECT d.id, u.id, 'PHE_DUYET', 'PENDING', 'APPROVED',
       'Chấp thuận cấp Giấy chứng nhận HKD'
FROM ops_dossiers d
         JOIN sys_users u ON u.username = 'hc_pct'
WHERE d.dossier_code = 'HS_KD_001';

INSERT INTO ops_dossier_logs(dossier_id, actor_id, action, prev_status, next_status, comments)
SELECT d.id, u.id, 'TRA_KET_QUA', 'APPROVED', 'APPROVED',
       'Đã trả Giấy chứng nhận HKD cho công dân'
FROM ops_dossiers d
         JOIN sys_users u ON u.username = 'hc_mc'
WHERE d.dossier_code = 'HS_KD_001';

# III. KIỂM TRA TIMELINE HỒ SƠ
SELECT
    o.dossier_code,
    l.action,
    l.prev_status,
    l.next_status,
    u.full_name AS actor,
    l.comments,
    l.created_at
FROM ops_dossier_logs l
         JOIN ops_dossiers o ON l.dossier_id = o.id
         JOIN sys_users u ON l.actor_id = u.id
ORDER BY o.dossier_code, l.created_at;

# 1️⃣ HS_XNHN_001 – Giấy xác nhận tình trạng hôn nhân
INSERT INTO ops_dossier_results
(dossier_id, decision_number, signer_name, e_file_url)
SELECT
    d.id,
    'XN-HN-2025-0001',
    'Trần Thị Bình',
    '/results/HS_XNHN_001/giay_xac_nhan_tinh_trang_hon_nhan.pdf'
FROM ops_dossiers d
WHERE d.dossier_code = 'HS_XNHN_001'
  AND d.dossier_status = 'APPROVED';

# 2️⃣ HS_KH_001 – Giấy chứng nhận kết hôn
INSERT INTO ops_dossier_results(dossier_id, decision_number, signer_name, e_file_url)
SELECT
    d.id,
    'GCN-KH-2025-0001',
    'Nguyễn Văn An',
    '/results/HS_KH_001/giay_chung_nhan_ket_hon.pdf'
FROM ops_dossiers d
WHERE d.dossier_code = 'HS_KH_001'
  AND d.dossier_status = 'APPROVED';

# 3️⃣ HS_KD_001 – Giấy chứng nhận hộ kinh doanh
INSERT INTO ops_dossier_results(dossier_id, decision_number, signer_name, e_file_url)
SELECT
    d.id,
    'GCN-HKD-2025-0001',
    'Trần Thị Bình',
    '/results/HS_KD_001/giay_chung_nhan_hkd.pdf'
FROM ops_dossiers d
WHERE d.dossier_code = 'HS_KD_001'
  AND d.dossier_status = 'APPROVED';

# 4️⃣ (Tuỳ chọn) HS_TRE_001 – Giấy khai sinh + xác nhận cư trú
#
# Chỉ chạy nếu anh/chị đã cập nhật trạng thái sang APPROVED

INSERT INTO ops_dossier_results(dossier_id, decision_number, signer_name, e_file_url)
SELECT
    d.id,
    'KS-2025-0001',
    'Trần Thị Bình',
    '/results/HS_TRE_001/giay_khai_sinh.pdf'
FROM ops_dossiers d
WHERE d.dossier_code = 'HS_TRE_001'
  AND d.dossier_status = 'APPROVED';

# III. KIỂM TRA KẾT QUẢ ĐÃ BAN HÀNH
SELECT
    o.dossier_code,
    r.decision_number,
    r.signer_name,
    r.e_file_url,
    r.created_at
FROM ops_dossier_results r
         JOIN ops_dossiers o ON r.dossier_id = o.id
ORDER BY r.created_at;

