use egov_db;

INSERT INTO `mock_citizens` (`cccd`, `full_name`, `dob`, `gender`, `marital_status`, `spouse_id`)
VALUES ('010197001001', 'Hoàng Văn Thắng', '1997-07-20', 'MALE', 'MARRIED', 2),
       ('010197001002', 'Đỗ Thị Minh', '1998-05-18', 'FEMALE', 'MARRIED', 1),
       ('025202002003', 'Nguyễn Đức Nam', '2002-01-05', 'MALE', 'SINGLE', NULL),
       ('079301003004', 'Phạm Trà My', '2001-12-12', 'FEMALE', 'SINGLE', NULL),
       ('062290004005', 'Lý Hải Đăng', '1990-09-09', 'MALE', 'SINGLE', NULL),
       ('096000005006', 'Vũ Thị Thanh', '1985-10-05', 'FEMALE', 'SINGLE', NULL),
       ('096000006007', 'Nguyễn Tiến Dũng', '1980-04-14', 'MALE', 'MARRIED', 8),
       ('096000007008', 'Lý Cẩm Vân', '1982-12-03', 'FEMALE', 'MARRIED', 7),
       ('096000008009', 'Hoàng Duy Phúc', '1975-07-27', 'MALE', 'DIVORCED', NULL),
       ('096000009010', 'Trần Phương Nga', '1999-01-01', 'FEMALE', 'SINGLE', NULL),
       ('099001000011', 'Nguyễn Văn Hộ Tịch', '1985-03-12', 'MALE', 'SINGLE', NULL),
       ('099001000012', 'Trần Văn Đất Đai', '1982-07-20', 'MALE', 'SINGLE', NULL),
       ('099001000013', 'Lê Thị Kinh Doanh', '1988-11-05', 'FEMALE', 'SINGLE', NULL),
       ('099001000014', 'Phạm Văn An Sinh', '1979-02-18', 'MALE', 'SINGLE', NULL),
       ('099001000015', 'Hoàng Văn CNTT', '1990-09-09', 'MALE', 'SINGLE', NULL);


INSERT INTO `mock_households` (`household_code`, `head_citizen_id`, `address`)
VALUES ('HHK-001', 1, 'Số 10, Đường Cầu Giấy, Hà Nội'),        -- Chủ hộ: Hoàng Văn Thắng (ID 1)
       ('HHK-002', 7, 'Số 25, Đường Lê Lợi, Đà Nẵng'),         -- Chủ hộ: Nguyễn Tiến Dũng (ID 7)
       ('HHK-003', 3, 'Số 42, Đường Hoàng Văn Thụ, TP.HCM'),   -- Chủ hộ: Nguyễn Đức Nam (ID 3)
       ('HHK-004', 5, 'Số 88, Đường Hai Bà Trưng, Hải Phòng'), -- Chủ hộ: Lý Hải Đăng (ID 5)
       ('HHK-005', 9, 'Số 1A, Đường Nguyễn Huệ, Huế'),         -- Chủ hộ: Hoàng Duy Phúc (ID 9)
       ('HHK-006', 4, 'Số 33, Phố Tràng Tiền, Hà Nội'),        -- Chủ hộ: Phạm Trà My (ID 4)
       ('HHK-007', 6, 'Số 99, Khu phố Tây, Hội An'),           -- Chủ hộ: Vũ Thị Thanh (ID 6)
       ('HHK-008', 10, 'Số 123, Đường 3/2, Cần Thơ'); -- Chủ hộ: Trần Phương Nga (ID 10)


INSERT INTO `mock_household_members` (`household_id`, `citizen_id`, `relation_to_head`, `status`)
VALUES
-- Hộ khẩu 1 (ID: 1)
(1, 1, 'CHU_HO', 1), -- Hoàng Văn Thắng
(1, 2, 'VO', 1),     -- Đỗ Thị Minh (vợ của ID 1)

-- Hộ khẩu 2 (ID: 2)
(2, 7, 'CHU_HO', 1), -- Nguyễn Tiến Dũng
(2, 8, 'VO', 1),     -- Lý Cẩm Vân (vợ của ID 7)

-- Hộ khẩu 3, 4, 5, 6, 7, 8 (Các hộ độc thân/chủ hộ)
(3, 3, 'CHU_HO', 1), -- Nguyễn Đức Nam
(4, 5, 'CHU_HO', 1), -- Lý Hải Đăng
(5, 9, 'CHU_HO', 1), -- Hoàng Duy Phúc
(6, 4, 'CHU_HO', 1), -- Phạm Trà My
(7, 6, 'CHU_HO', 1), -- Vũ Thị Thanh
(8, 10, 'CHU_HO', 1);
-- Trần Phương Nga

-- Thêm các dịch vụ công vào bảng cat_services
-- 1. Dịch vụ Hộ tịch, Cư trú, và Y tế cho trẻ em
INSERT INTO cat_services (service_code, service_name, domain, sla_hours, fee_amount, form_schema)
VALUES ('HK01_TRE',
        'Đăng ký khai sinh, đăng ký thường trú, cấp thẻ bảo hiểm y tế cho trẻ em dưới 6 tuổi',
        'HỘ TỊCH & CƯ TRÚ',
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
        'HỘ TỊCH & CƯ TRÚ & LĐTBXH',
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

INSERT INTO mock_lands
(land_certificate_number,
 issue_date,
 issue_authority,
 map_sheet_number,
 parcel_number,
 address_detail,
 area_m2,
 usage_form,
 land_purpose,
 usage_period,
 house_area_m2,
 construction_area_m2,
 asset_notes,
 owner_id,
 land_status)
VALUES
-- 1. Hoàng Văn Thắng (ID = 1) – Đất ở đô thị, hộ gia đình
('GCN-HN-0001',
 '2015-06-20',
 'UBND Quận Cầu Giấy, Hà Nội',
 '12',
 '345',
 'Phường Dịch Vọng, Quận Cầu Giấy, Hà Nội',
 120.50,
 'Sử dụng riêng',
 'Đất ở đô thị',
 'Lâu dài',
 80.00,
 160.00,
 'Nhà ở 2 tầng, xây kiên cố',
 1,
 'Hợp pháp'),

-- 2. Nguyễn Tiến Dũng (ID = 7) – Đất ở đô thị, vợ chồng
('GCN-DN-0002',
 '2012-03-15',
 'UBND Quận Hải Châu, Đà Nẵng',
 '08',
 '122',
 'Phường Hải Châu I, Quận Hải Châu, Đà Nẵng',
 95.00,
 'Sử dụng chung',
 'Đất ở đô thị',
 'Lâu dài',
 70.00,
 140.00,
 'Nhà ở 2 tầng, tài sản chung vợ chồng',
 7,
 'Hợp pháp'),

-- 3. Nguyễn Đức Nam (ID = 3) – Đất ở nông thôn
('GCN-HCM-0003',
 '2020-11-10',
 'UBND Quận Tân Bình, TP.HCM',
 '03',
 '567',
 'Phường 13, Quận Tân Bình, TP.HCM',
 150.00,
 'Sử dụng riêng',
 'Đất ở tại đô thị',
 'Lâu dài',
 NULL,
 NULL,
 'Chưa xây dựng nhà ở',
 3,
 'Hợp pháp'),

-- 4. Lý Hải Đăng (ID = 5) – Đất trồng cây lâu năm
('GCN-HP-0004',
 '2010-08-01',
 'UBND Quận Lê Chân, Hải Phòng',
 '05',
 '88',
 'Phường An Biên, Quận Lê Chân, Hải Phòng',
 500.00,
 'Sử dụng riêng',
 'Đất trồng cây lâu năm',
 'Đến năm 2050',
 NULL,
 NULL,
 'Trồng cây ăn quả lâu năm',
 5,
 'Hợp pháp'),

-- 5. Hoàng Duy Phúc (ID = 9) – Đất đang thế chấp
('GCN-HUE-0005',
 '2018-09-25',
 'UBND TP Huế',
 '09',
 '301',
 'Phường Phú Hội, TP Huế',
 110.00,
 'Sử dụng riêng',
 'Đất ở đô thị',
 'Lâu dài',
 75.00,
 150.00,
 'Nhà ở 2 tầng, đang thế chấp ngân hàng',
 9,
 'Đang thế chấp'),

-- 6. Phạm Trà My (ID = 4) – Đất ở đô thị, độc thân
('GCN-HN-0006',
 '2021-02-18',
 'UBND Quận Hoàn Kiếm, Hà Nội',
 '01',
 '19',
 'Phố Tràng Tiền, Quận Hoàn Kiếm, Hà Nội',
 60.00,
 'Sử dụng riêng',
 'Đất ở đô thị',
 'Lâu dài',
 50.00,
 100.00,
 'Nhà phố cổ, 2 tầng',
 4,
 'Hợp pháp'),

-- 7. Vũ Thị Thanh (ID = 6) – Đất ở kết hợp kinh doanh
('GCN-QNA-0007',
 '2016-07-07',
 'UBND TP Hội An, Quảng Nam',
 '07',
 '210',
 'Phường Minh An, TP Hội An, Quảng Nam',
 130.00,
 'Sử dụng riêng',
 'Đất ở kết hợp kinh doanh',
 'Lâu dài',
 90.00,
 180.00,
 'Nhà ở kết hợp cửa hàng buôn bán',
 6,
 'Hợp pháp'),

-- 8. Trần Phương Nga (ID = 10) – Đất chưa có tài sản
('GCN-CT-0008',
 '2022-05-30',
 'UBND Quận Ninh Kiều, Cần Thơ',
 '11',
 '402',
 'Phường An Khánh, Quận Ninh Kiều, Cần Thơ',
 200.00,
 'Sử dụng riêng',
 'Đất ở đô thị',
 'Lâu dài',
 NULL,
 NULL,
 'Đất trống, chưa xây dựng',
 10,
 'Hợp pháp');

INSERT INTO mock_businesses
(tax_code,
 business_name,
 capital,
 owner_id,
 address,
 business_lines)
VALUES
-- 1. Hoàng Văn Thắng (ID = 1)
('0101970010',
 'Hộ kinh doanh Dịch vụ Tin học Thắng Hoàng',
 300000000.00,
 1,
 'Số 10, Đường Cầu Giấy, Quận Cầu Giấy, Hà Nội',
 'Dịch vụ sửa chữa máy tính, cài đặt phần mềm, tư vấn CNTT'),

-- 2. Nguyễn Tiến Dũng (ID = 7)
('0401980020',
 'Hộ kinh doanh Nhà nghỉ Dũng Vân',
 800000000.00,
 7,
 'Số 25, Đường Lê Lợi, Quận Hải Châu, Đà Nẵng',
 'Dịch vụ lưu trú ngắn ngày, nhà nghỉ'),

-- 3. Nguyễn Đức Nam (ID = 3)
('0312003001',
 'Hộ kinh doanh Quán cà phê Nam Coffee',
 150000000.00,
 3,
 'Số 42, Đường Hoàng Văn Thụ, Quận Tân Bình, TP.HCM',
 'Dịch vụ đồ uống, cà phê, giải khát'),

-- 4. Lý Hải Đăng (ID = 5)
('0201990040',
 'Hộ kinh doanh Nông sản Hải Đăng',
 500000000.00,
 5,
 'Số 88, Đường Hai Bà Trưng, Quận Lê Chân, Hải Phòng',
 'Trồng trọt, thu mua và kinh doanh nông sản'),

-- 5. Phạm Trà My (ID = 4)
('0102001060',
 'Hộ kinh doanh Thời trang Trà My',
 250000000.00,
 4,
 'Số 33, Phố Tràng Tiền, Quận Hoàn Kiếm, Hà Nội',
 'Bán lẻ quần áo, phụ kiện thời trang'),

-- 6. Vũ Thị Thanh (ID = 6)
('4001985070',
 'Hộ kinh doanh Đặc sản Hội An Thanh Vũ',
 200000000.00,
 6,
 'Số 99, Khu phố Tây, TP Hội An, Quảng Nam',
 'Bán lẻ đặc sản địa phương, quà lưu niệm'),

-- 7. Trần Phương Nga (ID = 10)
('1801999010',
 'Hộ kinh doanh Mỹ phẩm Nga Beauty',
 180000000.00,
 10,
 'Số 123, Đường 3/2, Quận Ninh Kiều, Cần Thơ',
 'Bán lẻ mỹ phẩm, chăm sóc da');



INSERT INTO sys_departments (dept_code, dept_name, parent_id, level)
VALUES
-- ===== CẤP 1: CƠ QUAN QUẢN LÝ CAO NHẤT =====
('UBND_TINH', 'Ủy ban nhân dân cấp Tỉnh/Thành phố', NULL, 1),

-- ===== CẤP 2: SỞ / NGÀNH TRỰC THUỘC =====
('SO_TP', 'Sở Tư pháp', 1, 2),
('SO_TNMT', 'Sở Tài nguyên và Môi trường', 1, 2),
('SO_KHDT', 'Sở Kế hoạch và Đầu tư', 1, 2),
('SO_LDTBXH', 'Sở Lao động - Thương binh và Xã hội', 1, 2),
('SO_TTTT', 'Sở Thông tin và Truyền thông', 1, 2),

-- ===== CẤP 3: PHÒNG / BỘ PHẬN CHUYÊN MÔN =====
('PT_HOTICH', 'Phòng Hộ tịch', 2, 3),
('PT_HONNHAN', 'Phòng Hôn nhân và Gia đình', 2, 3),

('PT_DANGKY_DATDAI', 'Phòng Đăng ký đất đai', 3, 3),
('PT_QUYHOACH', 'Phòng Quy hoạch - Kế hoạch sử dụng đất', 3, 3),

('PT_DKKD', 'Phòng Đăng ký kinh doanh', 4, 3),

('PT_VIECLAM', 'Phòng Việc làm & An sinh xã hội', 5, 3),

('PT_HTCNTT', 'Phòng Hạ tầng CNTT & Chuyển đổi số', 6, 3);


INSERT INTO sys_users
(username,
 password_hash,
 full_name,
 user_type,
 citizen_id,
 dept_id)
VALUES
-- ===== CITIZEN =====
('thanghv', '{bcrypt}$2a$10$hash_thang', 'Hoàng Văn Thắng', 'CITIZEN', 1, NULL),
('minhdt', '{bcrypt}$2a$10$hash_minh', 'Đỗ Thị Minh', 'CITIZEN', 2, NULL),
('namnd', '{bcrypt}$2a$10$hash_nam', 'Nguyễn Đức Nam', 'CITIZEN', 3, NULL),
('mypt', '{bcrypt}$2a$10$hash_my', 'Phạm Trà My', 'CITIZEN', 4, NULL),
('danglh', '{bcrypt}$2a$10$hash_dang', 'Lý Hải Đăng', 'CITIZEN', 5, NULL),
('thanhvt', '{bcrypt}$2a$10$hash_thanh', 'Vũ Thị Thanh', 'CITIZEN', 6, NULL),
('dungnt', '{bcrypt}$2a$10$hash_dung', 'Nguyễn Tiến Dũng', 'CITIZEN', 7, NULL),
('vanlc', '{bcrypt}$2a$10$hash_van', 'Lý Cẩm Vân', 'CITIZEN', 8, NULL),
('phuchd', '{bcrypt}$2a$10$hash_phuc', 'Hoàng Duy Phúc', 'CITIZEN', 9, NULL),
('ngatp', '{bcrypt}$2a$10$hash_nga', 'Trần Phương Nga', 'CITIZEN', 10, NULL),

-- ===== OFFICIAL (đã gắn citizen_id) =====
('cb_hotich', '{bcrypt}$2a$10$hash_cb1', 'Nguyễn Văn Hộ Tịch', 'OFFICIAL', 11, 7),
('cb_datdai', '{bcrypt}$2a$10$hash_cb2', 'Trần Văn Đất Đai', 'OFFICIAL', 12, 9),
('cb_kinhdoanh', '{bcrypt}$2a$10$hash_cb3', 'Lê Thị Kinh Doanh', 'OFFICIAL', 13, 11),
('cb_ldtbxh', '{bcrypt}$2a$10$hash_cb4', 'Phạm Văn An Sinh', 'OFFICIAL', 14, 12),
('cb_cntt', '{bcrypt}$2a$10$hash_cb5', 'Hoàng Văn CNTT', 'OFFICIAL', 15, 13),

-- ===== ADMIN =====
('admin', '{bcrypt}$2a$10$hash_admin', 'System Administrator', 'ADMIN', NULL, NULL);


INSERT INTO sys_roles (role_name, description)
VALUES
-- ===== QUYỀN HỆ THỐNG =====
('ADMIN', 'Quyền quản trị hệ thống, cấu hình, phân quyền và giám sát toàn bộ'),

-- ===== QUYỀN TIẾP NHẬN HỒ SƠ =====
('DOSSIER_RECEIVER', 'Tiếp nhận hồ sơ dịch vụ công từ người dân'),
('DOSSIER_PRECHECK', 'Kiểm tra tính đầy đủ, hợp lệ ban đầu của hồ sơ'),

-- ===== QUYỀN XỬ LÝ NGHIỆP VỤ =====
('DOSSIER_HANDLER', 'Thẩm định và xử lý nghiệp vụ chính của hồ sơ'),
('DOSSIER_EDITOR', 'Cập nhật, bổ sung thông tin hồ sơ trong quá trình xử lý'),

-- ===== QUYỀN PHÊ DUYỆT =====
('DOSSIER_APPROVER', 'Phê duyệt hồ sơ, ra quyết định hành chính'),

-- ===== QUYỀN IN ẤN & KẾT QUẢ =====
('RESULT_PRINTER', 'In và phát hành kết quả, giấy tờ hành chính'),

-- ===== QUYỀN TRA CỨU =====
('DOSSIER_VIEWER', 'Chỉ được xem thông tin hồ sơ, không được chỉnh sửa');


INSERT INTO sys_user_roles (user_id, role_id)
VALUES
-- ===== ADMIN =====
(16, 1),

-- ===== PHÒNG HỘ TỊCH =====
(11, 2), -- DOSSIER_RECEIVER
(11, 3), -- DOSSIER_PRECHECK
(11, 4), -- DOSSIER_HANDLER

-- ===== PHÒNG ĐẤT ĐAI =====
(12, 4), -- DOSSIER_HANDLER
(12, 6), -- DOSSIER_APPROVER

-- ===== PHÒNG ĐĂNG KÝ KINH DOANH =====
(13, 2), -- DOSSIER_RECEIVER
(13, 4), -- DOSSIER_HANDLER

-- ===== PHÒNG LĐTBXH =====
(14, 4), -- DOSSIER_HANDLER

-- ===== PHÒNG CNTT =====
(15, 1), -- ADMIN
(15, 8); -- DOSSIER_VIEWER


INSERT INTO cat_workflow_steps
    (service_id, step_name, step_order, role_required_id)
VALUES
-- =====================================================
-- 1. ĐĂNG KÝ KHAI SINH + THƯỜNG TRÚ + BHYT (HK01_TRE)
-- =====================================================
(1, 'Tiếp nhận hồ sơ', 1, 2),    -- DOSSIER_RECEIVER
(1, 'Kiểm tra hồ sơ', 2, 3),     -- DOSSIER_PRECHECK
(1, 'Xác minh thông tin', 3, 4), -- DOSSIER_HANDLER
(1, 'Phê duyệt kết quả', 4, 6),  -- DOSSIER_APPROVER
(1, 'In và trả kết quả', 5, 7),  -- RESULT_PRINTER

-- =====================================================
-- 2. ĐĂNG KÝ KẾT HÔN (HT01_KETHON)
-- =====================================================
(2, 'Tiếp nhận hồ sơ', 1, 2),
(2, 'Thẩm tra điều kiện kết hôn', 2, 4),
(2, 'Phê duyệt kết hôn', 3, 6),
(2, 'Cấp Giấy chứng nhận kết hôn', 4, 7),

-- =====================================================
-- 3. ĐĂNG KÝ KHAI TỬ (HK02_KAITU)
-- =====================================================
(3, 'Tiếp nhận hồ sơ', 1, 2),
(3, 'Xác minh thông tin khai tử', 2, 4),
(3, 'Phê duyệt khai tử', 3, 6),
(3, 'Cập nhật dữ liệu dân cư', 4, 4),
(3, 'Trả kết quả', 5, 7),

-- =====================================================
-- 4. XÁC NHẬN TÌNH TRẠNG HÔN NHÂN (HT02_XACNHANHN)
-- =====================================================
(4, 'Tiếp nhận hồ sơ', 1, 2),
(4, 'Đối soát dữ liệu hôn nhân', 2, 4),
(4, 'Phê duyệt xác nhận', 3, 6),
(4, 'In giấy xác nhận', 4, 7),

-- =====================================================
-- 5. BIẾN ĐỘNG ĐẤT ĐAI (DD01_BIENDONG)
-- =====================================================
(5, 'Tiếp nhận hồ sơ', 1, 2),
(5, 'Kiểm tra pháp lý đất đai', 2, 4),
(5, 'Lấy ý kiến các bên liên quan', 3, 4),
(5, 'Phê duyệt biến động', 4, 6),
(5, 'Cập nhật hồ sơ địa chính', 5, 4),
(5, 'Trả kết quả', 6, 7),

-- =====================================================
-- 6. CHUYỂN MỤC ĐÍCH SỬ DỤNG ĐẤT (DD02_CHUYENMDSD)
-- =====================================================
(6, 'Tiếp nhận hồ sơ', 1, 2),
(6, 'Thẩm định nhu cầu sử dụng đất', 2, 4),
(6, 'Lấy ý kiến quy hoạch', 3, 4),
(6, 'Phê duyệt chuyển mục đích', 4, 6),
(6, 'Cập nhật hồ sơ địa chính', 5, 4),
(6, 'Trả kết quả', 6, 7),

-- =====================================================
-- 7. TÁCH / HỢP THỬA ĐẤT (DD03_TACHHOP)
-- =====================================================
(7, 'Tiếp nhận hồ sơ', 1, 2),
(7, 'Kiểm tra điều kiện tách/hợp thửa', 2, 4),
(7, 'Đo đạc và cập nhật bản đồ', 3, 4),
(7, 'Phê duyệt kết quả', 4, 6),
(7, 'Cập nhật hồ sơ địa chính', 5, 4),
(7, 'Trả kết quả', 6, 7),

-- =====================================================
-- 8. ĐĂNG KÝ HỘ KINH DOANH (KD01_HKD)
-- =====================================================
(8, 'Tiếp nhận hồ sơ', 1, 2),
(8, 'Thẩm tra thông tin kinh doanh', 2, 4),
(8, 'Phê duyệt đăng ký', 3, 6),
(8, 'Cấp Giấy chứng nhận HKD', 4, 7);


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



INSERT INTO ops_dossiers
(dossier_code, service_id, applicant_id, current_handler_id,
 dossier_status, submission_date, due_date, finish_date, form_data, rejection_reason)
VALUES

-- =====================================================
-- 1. KHAI SINH – ĐANG XỬ LÝ
-- =====================================================
('HS-2025-0001',
 1,
 1,
 11,
 'PENDING',
 NOW() - INTERVAL 2 DAY,
 NOW() + INTERVAL 3 DAY,
 NULL,
 JSON_OBJECT(
         'child_name', 'Nguyễn Minh Anh',
         'date_of_birth', '2025-12-01',
         'father_id', 1,
         'mother_id', 2
 ),
 NULL),

-- =====================================================
-- 2. KẾT HÔN – ĐÃ PHÊ DUYỆT
-- =====================================================
('HS-2025-0002',
 2,
 1,
 11,
 'APPROVED',
 NOW() - INTERVAL 5 DAY,
 NOW() - INTERVAL 2 DAY,
 NOW() - INTERVAL 1 DAY,
 JSON_OBJECT(
         'husband_id', 1,
         'wife_id', 2
 ),
 NULL),

-- =====================================================
-- 3. XÁC NHẬN HÔN NHÂN – BỊ TỪ CHỐI
-- =====================================================
('HS-2025-0003',
 4,
 3,
 11,
 'REJECTED',
 NOW() - INTERVAL 3 DAY,
 NOW() - INTERVAL 1 DAY,
 NOW(),
 JSON_OBJECT(
         'purpose', 'Mua bán nhà đất'
 ),
 'Thông tin tình trạng hôn nhân không khớp với dữ liệu hộ tịch.'),

-- =====================================================
-- 4. BIẾN ĐỘNG ĐẤT ĐAI – ĐANG THẨM ĐỊNH
-- =====================================================
('HS-2025-0004',
 5,
 1,
 12,
 'PENDING',
 NOW() - INTERVAL 7 DAY,
 NOW() + INTERVAL 8 DAY,
 NULL,
 JSON_OBJECT(
         'land_id', 1,
         'change_type', 'Chuyển nhượng'
 ),
 NULL),

-- =====================================================
-- 5. CHUYỂN MỤC ĐÍCH SDĐ – HỒ SƠ MỚI
-- =====================================================
('HS-2025-0005',
 6,
 1,
 NULL,
 'NEW',
 NOW(),
 NOW() + INTERVAL 20 DAY,
 NULL,
 JSON_OBJECT(
         'land_id', 1,
         'new_purpose', 'Đất ở đô thị'
 ),
 NULL),

-- =====================================================
-- 6. ĐĂNG KÝ HỘ KINH DOANH – HOÀN THÀNH
-- =====================================================
('HS-2025-0006',
 8,
 1,
 13,
 'APPROVED',
 NOW() - INTERVAL 4 DAY,
 NOW() - INTERVAL 1 DAY,
 NOW(),
 JSON_OBJECT(
         'business_name', 'Hộ kinh doanh Minh Anh',
         'capital', 200000000,
         'address', 'Số 10 Cầu Giấy, Hà Nội'
 ),
 NULL);


INSERT INTO ops_dossier_files
    (dossier_id, file_name, file_url, file_type)
VALUES
-- =====================================================
-- HS-2025-0001 – KHAI SINH
-- =====================================================
(1, 'Giay_chung_sinh.pdf', '/storage/dossiers/2025/0001/giay_chung_sinh.pdf', 'PDF'),
(1, 'CCCD_cha.jpg', '/storage/dossiers/2025/0001/cccd_cha.jpg', 'IMG'),
(1, 'CCCD_me.jpg', '/storage/dossiers/2025/0001/cccd_me.jpg', 'IMG'),

-- =====================================================
-- HS-2025-0002 – KẾT HÔN
-- =====================================================
(2, 'CCCD_chong.jpg', '/storage/dossiers/2025/0002/cccd_chong.jpg', 'IMG'),
(2, 'CCCD_vo.jpg', '/storage/dossiers/2025/0002/cccd_vo.jpg', 'IMG'),
(2, 'Giay_ket_hon.pdf', '/storage/dossiers/2025/0002/giay_ket_hon.pdf', 'PDF'),

-- =====================================================
-- HS-2025-0003 – XÁC NHẬN HÔN NHÂN (BỊ TỪ CHỐI)
-- =====================================================
(3, 'Don_xac_nhan.docx', '/storage/dossiers/2025/0003/don_xac_nhan.docx', 'DOCX'),

-- =====================================================
-- HS-2025-0004 – BIẾN ĐỘNG ĐẤT ĐAI
-- =====================================================
(4, 'So_do.pdf', '/storage/dossiers/2025/0004/so_do.pdf', 'PDF'),
(4, 'Hop_dong_chuyen_nhuong.pdf',
 '/storage/dossiers/2025/0004/hop_dong_cn.pdf', 'PDF'),

-- =====================================================
-- HS-2025-0005 – CHUYỂN MỤC ĐÍCH SDĐ
-- =====================================================
(5, 'Don_chuyen_muc_dich.docx',
 '/storage/dossiers/2025/0005/don_chuyen_md.docx', 'DOCX'),

-- =====================================================
-- HS-2025-0006 – ĐĂNG KÝ HỘ KINH DOANH
-- =====================================================
(6, 'Don_dang_ky_hkd.docx',
 '/storage/dossiers/2025/0006/don_dk_hkd.docx', 'DOCX'),
(6, 'Giay_chung_nhan_hkd.pdf',
 '/storage/dossiers/2025/0006/giay_cn_hkd.pdf', 'PDF');


INSERT INTO ops_dossier_logs
(dossier_id, actor_id, action, prev_status, next_status, comments, created_at)
VALUES

-- =====================================================
-- HS-2025-0001 – KHAI SINH (ĐANG XỬ LÝ)
-- =====================================================
(1, 1, 'NOP_HO_SO', NULL, 'NEW',
 'Người dân nộp hồ sơ đăng ký khai sinh trực tuyến.',
 NOW() - INTERVAL 2 DAY),

(1, 11, 'TIEP_NHAN', 'NEW', 'PENDING',
 'Tiếp nhận hồ sơ, kiểm tra thành phần hồ sơ hợp lệ.',
 NOW() - INTERVAL 1 DAY),

-- =====================================================
-- HS-2025-0002 – KẾT HÔN (ĐÃ PHÊ DUYỆT)
-- =====================================================
(2, 1, 'NOP_HO_SO', NULL, 'NEW',
 'Nộp hồ sơ đăng ký kết hôn.',
 NOW() - INTERVAL 5 DAY),

(2, 11, 'THAM_DINH', 'NEW', 'PENDING',
 'Thẩm định thông tin hai bên nam nữ.',
 NOW() - INTERVAL 4 DAY),

(2, 11, 'PHE_DUYET', 'PENDING', 'APPROVED',
 'Hồ sơ hợp lệ, phê duyệt cấp Giấy chứng nhận kết hôn.',
 NOW() - INTERVAL 1 DAY),

-- =====================================================
-- HS-2025-0003 – XÁC NHẬN HÔN NHÂN (BỊ TỪ CHỐI)
-- =====================================================
(3, 3, 'NOP_HO_SO', NULL, 'NEW',
 'Nộp hồ sơ xin xác nhận tình trạng hôn nhân.',
 NOW() - INTERVAL 3 DAY),

(3, 11, 'THAM_DINH', 'NEW', 'PENDING',
 'Đối chiếu dữ liệu hộ tịch.',
 NOW() - INTERVAL 2 DAY),

(3, 11, 'TU_CHOI', 'PENDING', 'REJECTED',
 'Tình trạng hôn nhân không khớp dữ liệu lưu trữ.',
 NOW()),

-- =====================================================
-- HS-2025-0004 – BIẾN ĐỘNG ĐẤT ĐAI (ĐANG THẨM ĐỊNH)
-- =====================================================
(4, 1, 'NOP_HO_SO', NULL, 'NEW',
 'Nộp hồ sơ đăng ký biến động đất đai.',
 NOW() - INTERVAL 7 DAY),

(4, 12, 'THAM_DINH', 'NEW', 'PENDING',
 'Kiểm tra hồ sơ pháp lý thửa đất.',
 NOW() - INTERVAL 6 DAY),

-- =====================================================
-- HS-2025-0005 – CHUYỂN MỤC ĐÍCH SDĐ (HỒ SƠ MỚI)
-- =====================================================
(5, 1, 'NOP_HO_SO', NULL, 'NEW',
 'Nộp hồ sơ chuyển mục đích sử dụng đất.',
 NOW()),

-- =====================================================
-- HS-2025-0006 – HỘ KINH DOANH (HOÀN THÀNH)
-- =====================================================
(6, 1, 'NOP_HO_SO', NULL, 'NEW',
 'Nộp hồ sơ đăng ký hộ kinh doanh.',
 NOW() - INTERVAL 4 DAY),

(6, 13, 'THAM_DINH', 'NEW', 'PENDING',
 'Thẩm tra thông tin hộ kinh doanh.',
 NOW() - INTERVAL 3 DAY),

(6, 13, 'PHE_DUYET', 'PENDING', 'APPROVED',
 'Cấp Giấy chứng nhận đăng ký hộ kinh doanh.',
 NOW() - INTERVAL 1 DAY);

USE egov_db;

INSERT INTO ops_dossier_results
    (dossier_id, decision_number, signer_name, e_file_url, created_at)
VALUES
-- =====================================================
-- HS-2025-0002 – ĐĂNG KÝ KẾT HÔN
-- =====================================================
(2,
 'QĐ-HT-2025-0002',
 'Nguyễn Văn Hùng',
 '/storage/results/2025/0002/giay_chung_nhan_ket_hon.pdf',
 NOW() - INTERVAL 1 DAY),

-- =====================================================
-- HS-2025-0006 – ĐĂNG KÝ HỘ KINH DOANH
-- =====================================================
(6,
 'GCN-HKD-2025-0006',
 'Trần Thị Lan',
 '/storage/results/2025/0006/giay_chung_nhan_hkd.pdf',
 NOW() - INTERVAL 1 DAY);
