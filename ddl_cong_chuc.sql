-- 0. INITIALIZATION
DROP DATABASE IF EXISTS egov_db;
CREATE DATABASE egov_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE egov_db;

/* ==========================================================================
   MODULE 1: MOCK NATIONAL DATABASE (LÕI DỮ LIỆU GIẢ LẬP)
   Chứa dữ liệu cơ bản về Công dân, Hộ khẩu, Đất đai và Doanh nghiệp.
   ========================================================================== */

-- 1.1. Công dân (Mock Citizens)
/* Bảng chứa thông tin cơ bản và định danh của tất cả công dân giả lập. */
CREATE TABLE mock_citizens
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính, ID nội bộ của công dân',
    cccd              VARCHAR(12)  NOT NULL COMMENT 'Số định danh cá nhân/Căn cước công dân (Unique)',
    full_name         VARCHAR(100) NOT NULL COMMENT 'Họ và tên đầy đủ',
    dob               DATE         NOT NULL COMMENT 'Ngày tháng năm sinh',
    gender            VARCHAR(10)  NOT NULL COMMENT 'Giới tính (MALE, FEMALE, OTHER)',
    hometown          VARCHAR(255) COMMENT 'Quê quán',
    ethnic_group      VARCHAR(50) COMMENT 'Dân tộc (Kinh, Tày, Nùng...)',
    religion          VARCHAR(50) COMMENT 'Tôn giáo',
    permanent_address VARCHAR(255) COMMENT 'Địa chỉ thường trú.',
    temporary_address VARCHAR(255) COMMENT 'Địa chỉ tạm trú hiện tại',
    fingerprint_data  TEXT COMMENT 'Giả lập dữ liệu sinh trắc học (dấu vân tay)',
    avatar_url        VARCHAR(255) COMMENT 'Đường dẫn đến ảnh đại diện',
    marital_status    VARCHAR(20) DEFAULT 'SINGLE' COMMENT 'Tình trạng hôn nhân (SINGLE, MARRIED, DIVORCED, WIDOWED)',
    spouse_id         BIGINT COMMENT 'ID Vợ/Chồng (Self-referencing đến mock_citizens.id)',
    is_deceased       BOOLEAN     DEFAULT FALSE COMMENT 'Trạng thái khai tử (TRUE nếu đã chết, FALSE nếu còn sống)',

    created_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo bản ghi',
    updated_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời điểm cập nhật bản ghi gần nhất',
    status            TINYINT     DEFAULT 1 COMMENT 'Trạng thái hoạt động của dữ liệu (1: Active, 0: Inactive)',

    CONSTRAINT uq_mock_citizen_cccd UNIQUE (cccd),
    INDEX idx_mock_citizen_name (full_name)
) ENGINE = InnoDB COMMENT ='Thông tin cơ bản của công dân';

-- 2. Sổ Hộ Khẩu (Mock Households)
/* Bảng chứa thông tin về các Sổ Hộ Khẩu/Địa chỉ cư trú. */
CREATE TABLE mock_households
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của sổ hộ khẩu',
    household_code  VARCHAR(20)  NOT NULL UNIQUE COMMENT 'Số sổ hộ khẩu (Duy nhất)',
    head_citizen_id BIGINT COMMENT 'ID của công dân là Chủ hộ',
    address         VARCHAR(255) NOT NULL COMMENT 'Địa chỉ cụ thể của hộ khẩu',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo sổ hộ khẩu',
    CONSTRAINT fk_hh_head FOREIGN KEY (head_citizen_id) REFERENCES mock_citizens (id)
) ENGINE = InnoDB COMMENT ='Thông tin về Sổ Hộ Khẩu';

-- 3. Thành viên Hộ khẩu (Mock Household Members)
/* Bảng liên kết công dân với hộ khẩu và xác định quan hệ. */
CREATE TABLE mock_household_members
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của thành viên hộ khẩu',
    household_id     BIGINT      NOT NULL COMMENT 'ID của sổ hộ khẩu',
    citizen_id       BIGINT      NOT NULL COMMENT 'ID của công dân là thành viên',
    relation_to_head VARCHAR(50) NOT NULL COMMENT 'Quan hệ với Chủ hộ (CHU_HO, VO, CHONG, CON, BO_ME,...)',
    move_in_date     DATE COMMENT 'Ngày chuyển đến hộ khẩu',
    status           TINYINT DEFAULT 1 COMMENT 'Trạng thái cư trú (1: Đang ở, 0: Chuyển đi)',
    CONSTRAINT fk_hm_household FOREIGN KEY (household_id) REFERENCES mock_households (id),
    CONSTRAINT fk_hm_citizen FOREIGN KEY (citizen_id) REFERENCES mock_citizens (id),
    UNIQUE KEY uq_citizen_active (citizen_id, status) COMMENT 'Đảm bảo một công dân chỉ có thể ở một hộ khẩu active'
) ENGINE = InnoDB COMMENT ='Thông tin thành viên và quan hệ trong Hộ Khẩu';

-- 4. Đất đai (Mock Lands)
/* Bảng chứa thông tin chi tiết về các tài sản đất đai theo Sổ đỏ/GCN mới nhất. */
CREATE TABLE mock_lands
(
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của đất đai',
    -- Thông tin pháp lý (Sổ đỏ)
    land_certificate_number VARCHAR(50) NOT NULL UNIQUE COMMENT 'Số Sổ đỏ/Giấy chứng nhận (Số phát hành)',
    issue_date              DATE COMMENT 'Ngày cấp Giấy chứng nhận',
    issue_authority         VARCHAR(100) COMMENT 'Cơ quan cấp Giấy chứng nhận',
    -- Thông tin vị trí
    map_sheet_number        VARCHAR(20) COMMENT 'Số tờ bản đồ',
    parcel_number           VARCHAR(20) COMMENT 'Số thửa đất',
    address_detail          VARCHAR(255) COMMENT 'Địa chỉ chi tiết thửa đất (xã/phường, quận/huyện, tỉnh/thành)',
    -- Thông tin về đất
    area_m2                 DECIMAL(10, 2) COMMENT 'Diện tích đất (mét vuông)',
    usage_form              VARCHAR(50) COMMENT 'Hình thức sử dụng (VD: Sử dụng riêng, Sử dụng chung)',
    land_purpose            VARCHAR(100) COMMENT 'Mục đích sử dụng đất (VD: Đất ở đô thị, Đất trồng cây lâu năm)',
    usage_period            VARCHAR(50) COMMENT 'Thời hạn sử dụng đất (VD: Lâu dài, Đến ngày dd/mm/yyyy)',
    -- Thông tin về tài sản gắn liền với đất (Nếu có)
    house_area_m2           DECIMAL(10, 2) COMMENT 'Diện tích xây dựng nhà ở (mét vuông)',
    construction_area_m2    DECIMAL(10, 2) COMMENT 'Diện tích sàn xây dựng (mét vuông)',
    asset_notes             TEXT COMMENT 'Ghi chú về các tài sản khác gắn liền với đất (VD: Công trình, cây trồng)',
    -- Thông tin chủ sở hữu
    owner_id                BIGINT      NOT NULL COMMENT 'ID của chủ sở hữu (công dân) - Liên kết tới mock_citizens',
    -- Trạng thái
    land_status             VARCHAR(50) COMMENT 'Trạng thái đất đai (VD: Đang thế chấp, Đã chuyển nhượng, Hợp pháp)',
    CONSTRAINT fk_land_owner FOREIGN KEY (owner_id) REFERENCES mock_citizens (id)
) ENGINE = InnoDB COMMENT ='Thông tin tài sản đất đai chi tiết theo Sổ đỏ mới nhất';
-- 5. Doanh nghiệp (Mock Businesses)
/* Bảng chứa thông tin về các doanh nghiệp giả lập. */
CREATE TABLE mock_businesses
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của doanh nghiệp',
    tax_code       VARCHAR(20)  NOT NULL UNIQUE COMMENT 'Mã số thuế (Duy nhất)',
    business_name  VARCHAR(200) NOT NULL COMMENT 'Tên đầy đủ của doanh nghiệp',
    capital        DECIMAL(15, 2) COMMENT 'Vốn điều lệ',
    owner_id       BIGINT       NOT NULL COMMENT 'ID của người đại diện/chủ sở hữu (công dân)',
    address        VARCHAR(255) COMMENT 'Địa chỉ trụ sở chính',
    business_lines VARCHAR(255) COMMENT 'Ngành nghề kinh doanh chính',
    CONSTRAINT fk_biz_owner FOREIGN KEY (owner_id) REFERENCES mock_citizens (id)
) ENGINE = InnoDB COMMENT ='Thông tin doanh nghiệp';

/* ==========================================================================
   MODULE 2: HỆ THỐNG (SYSTEM)
   Quản lý Người dùng, Phân quyền, Phòng ban và Log hệ thống.
   ========================================================================== */

-- 6. Phòng ban (System Departments)
/* Bảng chứa thông tin và cấu trúc phân cấp của các phòng ban/đơn vị hành chính. */
CREATE TABLE sys_departments
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của phòng ban',
    dept_code VARCHAR(50)  NOT NULL UNIQUE COMMENT 'Mã phòng ban (Duy nhất)',
    dept_name VARCHAR(100) NOT NULL COMMENT 'Tên đầy đủ của phòng ban',
    parent_id BIGINT COMMENT 'ID của phòng ban cấp trên (để tạo phân cấp)',
    level     INT DEFAULT 1 COMMENT 'Cấp độ của phòng ban (1: Cao nhất)',
    CONSTRAINT fk_dept_parent FOREIGN KEY (parent_id) REFERENCES sys_departments (id)
) ENGINE = InnoDB COMMENT ='Danh sách các Phòng ban/Đơn vị';

-- 7. Người dùng (System Users)
/* Bảng chứa thông tin tài khoản đăng nhập của cán bộ/công chức và công dân. */
CREATE TABLE sys_users
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của người dùng',
    username      VARCHAR(50)  NOT NULL UNIQUE COMMENT 'Tên đăng nhập (Duy nhất)',
    password_hash VARCHAR(255) NOT NULL COMMENT 'Mã băm của mật khẩu',
    full_name     VARCHAR(100) NOT NULL COMMENT 'Họ và tên người dùng',
    user_type     VARCHAR(20)  NOT NULL COMMENT 'Loại người dùng (VD: CITIZEN, OFFICIAL, ADMIN)',
    citizen_id    BIGINT COMMENT 'Liên kết đến ID công dân (Nếu là công dân/cán bộ)',
    dept_id       BIGINT COMMENT 'Liên kết đến ID phòng ban (Nếu là cán bộ)',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo tài khoản',
    CONSTRAINT fk_user_mock FOREIGN KEY (citizen_id) REFERENCES mock_citizens (id),
    CONSTRAINT fk_user_dept FOREIGN KEY (dept_id) REFERENCES sys_departments (id)
) ENGINE = InnoDB COMMENT ='Thông tin tài khoản Người dùng hệ thống';

-- 8. Roles (System Roles)
/* Bảng định nghĩa các vai trò (quyền hạn) trong hệ thống. */
CREATE TABLE sys_roles
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của vai trò',
    role_name   VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên vai trò (VD: HO_SO_VIEWER, HO_SO_EDITOR, ADMIN)',
    description VARCHAR(255) COMMENT 'Mô tả chi tiết về vai trò'
) ENGINE = InnoDB COMMENT ='Danh sách các Vai trò (Roles) trong hệ thống';

-- 9. User Roles (System User Roles)
/* Bảng liên kết N:N giữa người dùng và vai trò để gán quyền. */
CREATE TABLE sys_user_roles
(
    user_id BIGINT NOT NULL COMMENT 'ID của Người dùng',
    role_id BIGINT NOT NULL COMMENT 'ID của Vai trò được gán',
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES sys_users (id),
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES sys_roles (id)
) ENGINE = InnoDB COMMENT ='Liên kết Người dùng và Vai trò';

-- 10. Ủy quyền (System User Delegations)
/* Bảng ghi nhận việc ủy quyền xử lý công việc  */
CREATE TABLE sys_user_delegations
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của ủy quyền',
    from_user_id BIGINT    NOT NULL COMMENT 'ID của cán bộ ủy quyền',
    to_user_id   BIGINT    NOT NULL COMMENT 'ID của cán bộ được ủy quyền',
    start_time   TIMESTAMP NOT NULL COMMENT 'Thời điểm bắt đầu ủy quyền',
    end_time     TIMESTAMP NOT NULL COMMENT 'Thời điểm kết thúc ủy quyền',
    notes        VARCHAR(255) COMMENT 'Ghi chú về nội dung ủy quyền',
    CONSTRAINT fk_dlg_from FOREIGN KEY (from_user_id) REFERENCES sys_users (id),
    CONSTRAINT fk_dlg_to FOREIGN KEY (to_user_id) REFERENCES sys_users (id)
) ENGINE = InnoDB COMMENT ='Thông tin Ủy quyền xử lý công việc';

-- 11. Configs (System Configurations)
/* Bảng lưu trữ các tham số cấu hình tĩnh của hệ thống. */
CREATE TABLE sys_configs
(
    config_key   VARCHAR(100) PRIMARY KEY COMMENT 'Tên tham số cấu hình (Duy nhất)',
    config_value TEXT COMMENT 'Giá trị của tham số cấu hình',
    description  VARCHAR(255) COMMENT 'Mô tả ý nghĩa của tham số'
) ENGINE = InnoDB COMMENT ='Cấu hình hệ thống';

-- 12. Audit Logs (System Audit Logs)
/* Bảng ghi lại lịch sử truy cập, thao tác quan trọng để phục vụ kiểm toán. */
CREATE TABLE sys_audit_logs
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của log',
    user_id     BIGINT COMMENT 'ID của người dùng thực hiện hành động',
    endpoint    VARCHAR(255) COMMENT 'API endpoint hoặc chức năng được truy cập',
    method      VARCHAR(10) COMMENT 'Phương thức HTTP (GET, POST, PUT, DELETE) hoặc loại hành động',
    status_code INT COMMENT 'Mã trạng thái trả về (HTTP status code)',
    payload     TEXT COMMENT 'Dữ liệu đầu vào hoặc kết quả đầu ra',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm hành động xảy ra'
) ENGINE = InnoDB COMMENT ='Nhật ký kiểm toán hành động người dùng';


/* ==========================================================================
   MODULE 3: DANH MỤC (CATALOG)
   Định nghĩa các Dịch vụ công, Quy trình, Biểu mẫu và Tri thức.
   ========================================================================== */

-- 13. Dịch vụ (Catalog Services)
/* Danh sách các dịch vụ hành chính công mà hệ thống cung cấp. */
CREATE TABLE cat_services
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của dịch vụ',
    service_code VARCHAR(50)  NOT NULL UNIQUE COMMENT 'Mã dịch vụ công (Duy nhất)',
    service_name VARCHAR(255) NOT NULL COMMENT 'Tên đầy đủ của dịch vụ công',
    domain       VARCHAR(50)  NOT NULL COMMENT 'Lĩnh vực/Ngành của dịch vụ (VD: Đất đai, Hộ tịch)',
    sla_hours    INT            DEFAULT 24 COMMENT 'Thời gian xử lý cam kết (Service Level Agreement) tính bằng giờ',
    fee_amount   DECIMAL(15, 2) DEFAULT 0 COMMENT 'Mức phí/lệ phí phải nộp',
    form_schema  JSON COMMENT 'Cấu trúc JSON Schema của biểu mẫu nộp hồ sơ'
) ENGINE = InnoDB COMMENT ='Danh sách Dịch vụ công';

-- 14. Quy trình (Catalog Workflow Steps)
/* Định nghĩa các bước xử lý theo trình tự cho từng dịch vụ. */
CREATE TABLE cat_workflow_steps
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của bước quy trình',
    service_id       BIGINT       NOT NULL COMMENT 'ID của dịch vụ công mà bước này thuộc về',
    step_name        VARCHAR(100) NOT NULL COMMENT 'Tên của bước trong quy trình (VD: Tiền xử lý, Thẩm định, Phê duyệt)',
    step_order       INT          NOT NULL COMMENT 'Thứ tự của bước trong quy trình',
    role_required_id BIGINT COMMENT 'ID của vai trò cần thiết để thực hiện bước này',
    CONSTRAINT fk_wf_service FOREIGN KEY (service_id) REFERENCES cat_services (id)
) ENGINE = InnoDB COMMENT ='Các bước trong Quy trình xử lý dịch vụ';

-- 15. Biểu mẫu in (Catalog Templates)
/* Danh sách các biểu mẫu (ví dụ: quyết định, giấy chứng nhận) để in ra. */
CREATE TABLE cat_templates
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của biểu mẫu',
    service_id       BIGINT       NOT NULL COMMENT 'ID của dịch vụ liên quan đến biểu mẫu',
    template_name    VARCHAR(100) NOT NULL COMMENT 'Tên của biểu mẫu',
    file_path        VARCHAR(255) NOT NULL COMMENT 'Đường dẫn vật lý/s3 đến tệp mẫu (VD: .docx, .pdf)',
    variable_mapping JSON COMMENT 'Ánh xạ các trường dữ liệu trong hồ sơ với các biến trong biểu mẫu',
    CONSTRAINT fk_tpl_service FOREIGN KEY (service_id) REFERENCES cat_services (id)
) ENGINE = InnoDB COMMENT ='Danh sách Biểu mẫu/Quyết định cần in ấn';

-- 16. Kho tri thức (Catalog Knowledge Base)
/* Các bài viết, hướng dẫn, FAQ liên quan đến dịch vụ. */
CREATE TABLE cat_knowledge_base
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của bài viết',
    service_id BIGINT COMMENT 'ID của dịch vụ liên quan (NULL nếu là tri thức chung)',
    title      VARCHAR(255) NOT NULL COMMENT 'Tiêu đề bài viết',
    content    TEXT COMMENT 'Nội dung chi tiết của bài viết/hướng dẫn',
    CONSTRAINT fk_kb_service FOREIGN KEY (service_id) REFERENCES cat_services (id)
) ENGINE = InnoDB COMMENT ='Kho Tri thức/Hướng dẫn';

/* ==========================================================================
   MODULE 4: VẬN HÀNH (OPERATIONS)
   Quản lý vòng đời Hồ sơ dịch vụ công: Nộp, Xử lý, Log và Kết quả.
   ========================================================================== */

-- 17. Hồ sơ (Operations Dossiers)
/* Bảng chính lưu trữ thông tin về từng hồ sơ dịch vụ công. */
CREATE TABLE ops_dossiers
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của hồ sơ',
    dossier_code       VARCHAR(50) NOT NULL UNIQUE COMMENT 'Mã hồ sơ (Duy nhất)',
    service_id         BIGINT      NOT NULL COMMENT 'Dịch vụ công được áp dụng',
    applicant_id       BIGINT      NOT NULL COMMENT 'ID của người nộp hồ sơ (sys_users.id)',
    current_handler_id BIGINT COMMENT 'ID của cán bộ đang thụ lý hồ sơ hiện tại',
    dossier_status     VARCHAR(20) DEFAULT 'NEW' COMMENT 'Trạng thái xử lý hồ sơ (VD: NEW, PENDING, APPROVED, REJECTED)',
    submission_date    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm nộp hồ sơ',
    due_date           TIMESTAMP   NULL COMMENT 'Thời hạn phải hoàn thành xử lý (dựa trên SLA)',
    finish_date        TIMESTAMP   NULL COMMENT 'Thời điểm hồ sơ được hoàn tất (APPROVED/REJECTED)',
    form_data          JSON COMMENT 'Dữ liệu chi tiết từ biểu mẫu nộp hồ sơ',
    rejection_reason   TEXT COMMENT 'Lý do bị từ chối/trả lại hồ sơ',
    CONSTRAINT fk_dos_service FOREIGN KEY (service_id) REFERENCES cat_services (id),
    CONSTRAINT fk_dos_applicant FOREIGN KEY (applicant_id) REFERENCES sys_users (id),
    CONSTRAINT fk_dos_handler FOREIGN KEY (current_handler_id) REFERENCES sys_users (id)
) ENGINE = InnoDB COMMENT ='Thông tin các Hồ sơ dịch vụ công';

ALTER TABLE ops_dossiers
    ADD COLUMN receiving_dept_id BIGINT NOT NULL COMMENT 'ID cơ quan tiếp nhận hồ sơ ban đầu'
        AFTER service_id,
    ADD CONSTRAINT fk_dos_receiving_dept
        FOREIGN KEY (receiving_dept_id)
            REFERENCES sys_departments (id);

-- 18. File đính kèm (Operations Dossier Files)
/* Danh sách các tệp đính kèm đi kèm hồ sơ. */
CREATE TABLE ops_dossier_files
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của tệp đính kèm',
    dossier_id BIGINT       NOT NULL COMMENT 'ID của Hồ sơ liên quan',
    file_name  VARCHAR(255) NOT NULL COMMENT 'Tên tệp gốc',
    file_url   VARCHAR(500) NOT NULL COMMENT 'Đường dẫn lưu trữ tệp',
    file_type  VARCHAR(20)  NOT NULL COMMENT 'Loại tệp (VD: PDF, DOCX, IMG)',
    CONSTRAINT fk_df_dossier FOREIGN KEY (dossier_id) REFERENCES ops_dossiers (id) ON DELETE CASCADE
) ENGINE = InnoDB COMMENT ='Tệp đính kèm của Hồ sơ';

-- 19. Nhật ký xử lý (Operations Dossier Logs)
/* Chi tiết từng hành động được thực hiện trên hồ sơ. */
CREATE TABLE ops_dossier_logs
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của nhật ký',
    dossier_id  BIGINT      NOT NULL COMMENT 'ID của Hồ sơ liên quan',
    actor_id    BIGINT      NOT NULL COMMENT 'ID của người thực hiện hành động (sys_users.id)',
    action      VARCHAR(50) NOT NULL COMMENT 'Loại hành động (VD: CHUYEN_BUOC, THAM_DINH, PHE_DUYET)',
    prev_status VARCHAR(20) COMMENT 'Trạng thái hồ sơ trước khi hành động',
    next_status VARCHAR(20) COMMENT 'Trạng thái hồ sơ sau khi hành động',
    comments    TEXT COMMENT 'Ghi chú/ý kiến của cán bộ xử lý',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm xảy ra hành động',
    CONSTRAINT fk_dl_dossier FOREIGN KEY (dossier_id) REFERENCES ops_dossiers (id) ON DELETE CASCADE
) ENGINE = InnoDB COMMENT ='Nhật ký xử lý chi tiết Hồ sơ';CREATE TABLE ops_dossier_logs
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của nhật ký',
    dossier_id  BIGINT      NOT NULL COMMENT 'ID của Hồ sơ liên quan',
    actor_id    BIGINT      NOT NULL COMMENT 'ID của người thực hiện hành động (sys_users.id)',
    action      VARCHAR(50) NOT NULL COMMENT 'Loại hành động (VD: CHUYEN_BUOC, THAM_DINH, PHE_DUYET)',
    prev_status VARCHAR(20) COMMENT 'Trạng thái hồ sơ trước khi hành động',
    next_status VARCHAR(20) COMMENT 'Trạng thái hồ sơ sau khi hành động',
    comments    TEXT COMMENT 'Ghi chú/ý kiến của cán bộ xử lý',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm xảy ra hành động',
    CONSTRAINT fk_dl_dossier FOREIGN KEY (dossier_id) REFERENCES ops_dossiers (id) ON DELETE CASCADE
) ENGINE = InnoDB COMMENT ='Nhật ký xử lý chi tiết Hồ sơ';

-- 20. Kết quả (Operations Dossier Results)
/* Bảng lưu trữ thông tin về kết quả xử lý cuối cùng của hồ sơ. */
CREATE TABLE ops_dossier_results
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của kết quả',
    dossier_id      BIGINT       NOT NULL COMMENT 'ID của Hồ sơ liên quan',
    decision_number VARCHAR(50)  NOT NULL UNIQUE COMMENT 'Số quyết định/Giấy phép được ban hành (Duy nhất)',
    signer_name     VARCHAR(100) COMMENT 'Tên người ký quyết định',
    e_file_url      VARCHAR(500) NOT NULL COMMENT 'Đường dẫn đến tệp kết quả điện tử',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm ban hành kết quả',
    CONSTRAINT fk_res_dossier FOREIGN KEY (dossier_id) REFERENCES ops_dossiers (id)
) ENGINE = InnoDB COMMENT ='Kết quả xử lý Hồ sơ (Quyết định/Giấy phép)';


/* ==========================================================================
   MODULE 5: MỞ RỘNG (EXTENSIONS)
   Các chức năng hỗ trợ: Vault cá nhân, Phản ánh, Thông báo, Thanh toán.
   ========================================================================== */

-- 21. Kho cá nhân (Personal Vaults)
/* Kho lưu trữ các tài liệu cá nhân của người dùng. */
CREATE TABLE mod_personal_vaults
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của tài liệu cá nhân',
    user_id  BIGINT       NOT NULL COMMENT 'ID của người dùng sở hữu tài liệu',
    doc_name VARCHAR(100) NOT NULL COMMENT 'Tên của tài liệu',
    doc_type VARCHAR(50)  NOT NULL COMMENT 'Loại tài liệu (VD: Bằng cấp, Giấy tờ xe)',
    file_url VARCHAR(500) NOT NULL COMMENT 'Đường dẫn lưu trữ tệp',
    CONSTRAINT fk_vau_user FOREIGN KEY (user_id) REFERENCES sys_users (id)
) ENGINE = InnoDB COMMENT ='Kho lưu trữ tài liệu cá nhân';

-- 22. Phản ánh (Feedbacks)
/* Bảng ghi nhận các phản ánh, góp ý hoặc đánh giá của người dùng. */
CREATE TABLE mod_feedbacks
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của phản ánh',
    user_id     BIGINT COMMENT 'ID của người dùng gửi phản ánh (NULL nếu là ẩn danh)',
    dossier_id  BIGINT COMMENT 'ID của Hồ sơ liên quan đến phản ánh',
    title       VARCHAR(200) NOT NULL COMMENT 'Tiêu đề/Tóm tắt phản ánh',
    content     TEXT COMMENT 'Nội dung chi tiết phản ánh',
    rating      INT COMMENT 'Đánh giá (rating) chất lượng dịch vụ (1-5 sao)',
    is_resolved BOOLEAN   DEFAULT FALSE COMMENT 'Trạng thái xử lý (TRUE nếu đã được giải quyết)',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm gửi phản ánh',
    CONSTRAINT fk_fb_user FOREIGN KEY (user_id) REFERENCES sys_users (id)
) ENGINE = InnoDB COMMENT ='Phản ánh và góp ý của người dân';

-- 23. Ảnh phản ánh (Feedback Attachments)
/* Các tệp đính kèm (ví dụ: hình ảnh) cho phản ánh. */
CREATE TABLE mod_feedback_attachments
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của tệp đính kèm',
    feedback_id BIGINT       NOT NULL COMMENT 'ID của Phản ánh liên quan',
    file_url    VARCHAR(500) NOT NULL COMMENT 'Đường dẫn lưu trữ tệp',
    CONSTRAINT fk_fba_feedback FOREIGN KEY (feedback_id) REFERENCES mod_feedbacks (id) ON DELETE CASCADE
) ENGINE = InnoDB COMMENT ='Tệp đính kèm cho Phản ánh';

-- 24. Thông báo (Notifications)
/* Bảng lưu trữ các thông báo hệ thống gửi đến từng người dùng. */
CREATE TABLE mod_notifications
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của thông báo',
    user_id    BIGINT NOT NULL COMMENT 'ID của người dùng nhận thông báo',
    title      VARCHAR(200) COMMENT 'Tiêu đề thông báo',
    message    TEXT COMMENT 'Nội dung chi tiết thông báo',
    is_read    BOOLEAN   DEFAULT FALSE COMMENT 'Trạng thái đã đọc (TRUE nếu người dùng đã đọc)',
    type       VARCHAR(50) COMMENT 'Loại thông báo (VD: STATUS_UPDATE, DELEGATION, NEWS)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm gửi thông báo',
    CONSTRAINT fk_not_user FOREIGN KEY (user_id) REFERENCES sys_users (id)
) ENGINE = InnoDB COMMENT ='Hệ thống Thông báo cho người dùng';

-- 25. Thanh toán (Payments)
/* Bảng quản lý giao dịch thanh toán phí/lệ phí liên quan đến hồ sơ. */
CREATE TABLE mod_payments
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính của giao dịch thanh toán',
    dossier_id     BIGINT         NOT NULL COMMENT 'ID của Hồ sơ cần thanh toán phí',
    amount         DECIMAL(15, 2) NOT NULL COMMENT 'Số tiền thanh toán',
    receipt_number VARCHAR(50) COMMENT 'Mã hóa đơn/Biên lai (Nếu có)',
    payment_status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'Trạng thái thanh toán (PENDING, SUCCESS, FAILED)',
    pay_date       TIMESTAMP COMMENT 'Thời điểm thanh toán thành công',
    CONSTRAINT fk_pay_dossier FOREIGN KEY (dossier_id) REFERENCES ops_dossiers (id)
) ENGINE = InnoDB COMMENT ='Thông tin giao dịch Thanh toán phí dịch vụ';