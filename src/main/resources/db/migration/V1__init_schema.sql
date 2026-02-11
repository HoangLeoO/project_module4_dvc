-- Flyway migration: initial schema for egov_db

CREATE TABLE mock_citizens
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    cccd              VARCHAR(12)  NOT NULL,
    full_name         VARCHAR(100) NOT NULL,
    dob               DATE         NOT NULL,
    gender            VARCHAR(10)  NOT NULL,
    hometown          VARCHAR(255),
    ethnic_group      VARCHAR(50),
    religion          VARCHAR(50),
    permanent_address VARCHAR(255),
    temporary_address VARCHAR(255),
    fingerprint_data  TEXT,
    avatar_url        VARCHAR(255),
    marital_status    VARCHAR(20) DEFAULT 'SINGLE',
    spouse_id         BIGINT,
    is_deceased       BOOLEAN     DEFAULT FALSE,

    created_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status            TINYINT     DEFAULT 1,

    CONSTRAINT uq_mock_citizen_cccd UNIQUE (cccd),
    INDEX idx_mock_citizen_name (full_name)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS mock_citizen_relationships
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    citizen_id        BIGINT      NOT NULL,
    relative_id       BIGINT      NOT NULL,
    relationship_type VARCHAR(50) NOT NULL,

    created_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_mcr_citizen FOREIGN KEY (citizen_id) REFERENCES mock_citizens (id) ON DELETE CASCADE,
    CONSTRAINT fk_mcr_relative FOREIGN KEY (relative_id) REFERENCES mock_citizens (id) ON DELETE CASCADE,

    UNIQUE KEY uq_citizen_relative (citizen_id, relative_id)
) ENGINE = InnoDB;

CREATE TABLE mock_households
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    household_code  VARCHAR(20)  NOT NULL UNIQUE,
    head_citizen_id BIGINT,
    address         VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hh_head FOREIGN KEY (head_citizen_id) REFERENCES mock_citizens (id)
) ENGINE = InnoDB;

CREATE TABLE mock_household_members
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    household_id     BIGINT      NOT NULL,
    citizen_id       BIGINT      NOT NULL,
    relation_to_head VARCHAR(50) NOT NULL,
    move_in_date     DATE,
    status           TINYINT DEFAULT 1,
    CONSTRAINT fk_hm_household FOREIGN KEY (household_id) REFERENCES mock_households (id),
    CONSTRAINT fk_hm_citizen FOREIGN KEY (citizen_id) REFERENCES mock_citizens (id),
    UNIQUE KEY uq_citizen_active (citizen_id, status)
) ENGINE = InnoDB;

CREATE TABLE mock_lands
(
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,

    land_certificate_number VARCHAR(50) NOT NULL UNIQUE,
    issue_date              DATE,
    issue_authority         VARCHAR(100),

    map_sheet_number        VARCHAR(20),
    parcel_number           VARCHAR(20),
    address_detail          VARCHAR(255),

    area_m2                 DECIMAL(10, 2),
    usage_form              VARCHAR(50),
    land_purpose            VARCHAR(100),
    usage_period            VARCHAR(50),

    house_area_m2           DECIMAL(10, 2),
    construction_area_m2    DECIMAL(10, 2),
    asset_notes             TEXT,

    owner_id                BIGINT      NOT NULL,

    land_status             VARCHAR(50),
    CONSTRAINT fk_land_owner FOREIGN KEY (owner_id) REFERENCES mock_citizens (id)
) ENGINE = InnoDB;

CREATE TABLE mock_businesses
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    tax_code       VARCHAR(20)  NOT NULL UNIQUE,
    business_name  VARCHAR(200) NOT NULL,
    capital        DECIMAL(15, 2),
    owner_id       BIGINT       NOT NULL,
    address        VARCHAR(255),
    business_lines VARCHAR(255),
    CONSTRAINT fk_biz_owner FOREIGN KEY (owner_id) REFERENCES mock_citizens (id)
) ENGINE = InnoDB;

CREATE TABLE sys_departments
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    dept_code VARCHAR(50)  NOT NULL UNIQUE,
    dept_name VARCHAR(100) NOT NULL,
    parent_id BIGINT,
    level     INT DEFAULT 1,
    CONSTRAINT fk_dept_parent FOREIGN KEY (parent_id) REFERENCES sys_departments (id)
) ENGINE = InnoDB;

CREATE TABLE sys_users
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    user_type     VARCHAR(20)  NOT NULL,
    citizen_id    BIGINT,
    dept_id       BIGINT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_mock FOREIGN KEY (citizen_id) REFERENCES mock_citizens (id),
    CONSTRAINT fk_user_dept FOREIGN KEY (dept_id) REFERENCES sys_departments (id)
) ENGINE = InnoDB;

CREATE TABLE sys_roles
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name   VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE = InnoDB;

CREATE TABLE sys_user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES sys_users (id),
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES sys_roles (id)
) ENGINE = InnoDB;

CREATE TABLE sys_user_delegations
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id BIGINT    NOT NULL,
    to_user_id   BIGINT    NOT NULL,
    start_time   TIMESTAMP NOT NULL,
    end_time     TIMESTAMP NOT NULL,
    notes        VARCHAR(255),
    status       TINYINT DEFAULT 1,
    CONSTRAINT fk_dlg_from FOREIGN KEY (from_user_id) REFERENCES sys_users (id),
    CONSTRAINT fk_dlg_to FOREIGN KEY (to_user_id) REFERENCES sys_users (id)
) ENGINE = InnoDB;

CREATE TABLE sys_delegation_scopes
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    delegation_id BIGINT       NOT NULL,
    scope_type    VARCHAR(20)  NOT NULL,
    scope_value   VARCHAR(100) NOT NULL,
    CONSTRAINT fk_ds_delegation FOREIGN KEY (delegation_id) REFERENCES sys_user_delegations (id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE sys_configs
(
    config_key   VARCHAR(100) PRIMARY KEY,
    config_value TEXT,
    description  VARCHAR(255)
) ENGINE = InnoDB;

CREATE TABLE sys_audit_logs
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT,
    endpoint    VARCHAR(255),
    method      VARCHAR(10),
    status_code INT,
    payload     TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE cat_services
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_code VARCHAR(50)  NOT NULL UNIQUE,
    service_name VARCHAR(255) NOT NULL,
    domain       VARCHAR(50)  NOT NULL,
    sla_hours    INT            DEFAULT 24,
    fee_amount   DECIMAL(15, 2) DEFAULT 0,
    role_id      BIGINT,
    form_schema  JSON,
    CONSTRAINT fk_service_role FOREIGN KEY (role_id) REFERENCES sys_roles (id)
) ENGINE = InnoDB;

CREATE TABLE cat_workflow_steps
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_id       BIGINT       NOT NULL,
    step_name        VARCHAR(100) NOT NULL,
    step_order       INT          NOT NULL,
    role_required_id BIGINT,
    CONSTRAINT fk_wf_service FOREIGN KEY (service_id) REFERENCES cat_services (id)
) ENGINE = InnoDB;

CREATE TABLE cat_templates
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_id       BIGINT       NOT NULL,
    template_name    VARCHAR(100) NOT NULL,
    file_path        VARCHAR(255) NOT NULL,
    variable_mapping JSON,
    CONSTRAINT fk_tpl_service FOREIGN KEY (service_id) REFERENCES cat_services (id)
) ENGINE = InnoDB;

CREATE TABLE cat_knowledge_base
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_id BIGINT,
    title      VARCHAR(255) NOT NULL,
    content    TEXT,
    CONSTRAINT fk_kb_service FOREIGN KEY (service_id) REFERENCES cat_services (id)
) ENGINE = InnoDB;

CREATE TABLE ops_dossiers
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    dossier_code       VARCHAR(50) NOT NULL UNIQUE,
    service_id         BIGINT      NOT NULL,
    receiving_dept_id  BIGINT      NOT NULL,
    applicant_id       BIGINT      NOT NULL,
    current_handler_id BIGINT,
    dossier_status     VARCHAR(20) DEFAULT 'NEW',

    payment_status     VARCHAR(20) DEFAULT 'UNPAID',
    payment_amount     BIGINT,
    payment_date       TIMESTAMP   NULL,
    transaction_code   VARCHAR(50),

    submission_date    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    due_date           TIMESTAMP   NULL,
    finish_date        TIMESTAMP   NULL,
    form_data          JSON,
    rejection_reason   TEXT,

    CONSTRAINT fk_dos_service FOREIGN KEY (service_id) REFERENCES cat_services (id),
    CONSTRAINT fk_dos_receiving_dept FOREIGN KEY (receiving_dept_id) REFERENCES sys_departments (id),
    CONSTRAINT fk_dos_applicant FOREIGN KEY (applicant_id) REFERENCES sys_users (id),
    CONSTRAINT fk_dos_handler FOREIGN KEY (current_handler_id) REFERENCES sys_users (id)
) ENGINE = InnoDB;

CREATE TABLE ops_dossier_files
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    dossier_id BIGINT       NOT NULL,
    file_name  VARCHAR(255) NOT NULL,
    file_url   VARCHAR(500) NOT NULL,
    file_type  VARCHAR(20)  NOT NULL,
    CONSTRAINT fk_df_dossier FOREIGN KEY (dossier_id) REFERENCES ops_dossiers (id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE ops_dossier_logs
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    dossier_id  BIGINT      NOT NULL,
    actor_id    BIGINT      NOT NULL,
    action      VARCHAR(50) NOT NULL,
    prev_status VARCHAR(20),
    next_status VARCHAR(20),
    comments    TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dl_dossier FOREIGN KEY (dossier_id) REFERENCES ops_dossiers (id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE ops_log_workflow_steps
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_id           BIGINT NOT NULL,
    workflow_step_id BIGINT NOT NULL,
    description      VARCHAR(255),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lws_log FOREIGN KEY (log_id) REFERENCES ops_dossier_logs (id) ON DELETE CASCADE,
    CONSTRAINT fk_lws_step FOREIGN KEY (workflow_step_id) REFERENCES cat_workflow_steps (id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE ops_dossier_results
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    dossier_id      BIGINT       NOT NULL,
    decision_number VARCHAR(50)  NOT NULL UNIQUE,
    signer_name     VARCHAR(100),
    e_file_url      VARCHAR(500) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_res_dossier FOREIGN KEY (dossier_id) REFERENCES ops_dossiers (id)
) ENGINE = InnoDB;

CREATE TABLE mod_personal_vaults
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id  BIGINT       NOT NULL,
    doc_name VARCHAR(100) NOT NULL,
    doc_type VARCHAR(50)  NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    CONSTRAINT fk_vau_user FOREIGN KEY (user_id) REFERENCES sys_users (id)
) ENGINE = InnoDB;

CREATE TABLE mod_feedbacks
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT,
    dossier_id  BIGINT,
    title       VARCHAR(200) NOT NULL,
    content     TEXT,
    rating      INT,
    is_resolved BOOLEAN   DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fb_user FOREIGN KEY (user_id) REFERENCES sys_users (id)
) ENGINE = InnoDB;

CREATE TABLE mod_feedback_attachments
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    feedback_id BIGINT       NOT NULL,
    file_url    VARCHAR(500) NOT NULL,
    CONSTRAINT fk_fba_feedback FOREIGN KEY (feedback_id) REFERENCES mod_feedbacks (id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE mod_notifications
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    title      VARCHAR(200),
    message    TEXT,
    is_read    BOOLEAN   DEFAULT FALSE,
    type       VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_not_user FOREIGN KEY (user_id) REFERENCES sys_users (id)
) ENGINE = InnoDB;

CREATE TABLE mod_payments
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    dossier_id     BIGINT         NOT NULL,
    amount         DECIMAL(15, 2) NOT NULL,
    receipt_number VARCHAR(50),
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    pay_date       TIMESTAMP,
    CONSTRAINT fk_pay_dossier FOREIGN KEY (dossier_id) REFERENCES ops_dossiers (id)
) ENGINE = InnoDB;
