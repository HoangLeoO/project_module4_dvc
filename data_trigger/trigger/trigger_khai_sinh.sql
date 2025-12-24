USE egov_db;

DROP TRIGGER IF EXISTS trg_birth_registration_sync;
DELIMITER $$

CREATE TRIGGER trg_birth_registration_sync
AFTER UPDATE ON ops_dossiers
FOR EACH ROW
BEGIN
    DECLARE v_s_code       VARCHAR(50);
    DECLARE v_c_name       VARCHAR(255);
    DECLARE v_c_dob_raw    VARCHAR(50);
    DECLARE v_c_dob        DATE;
    DECLARE v_gender       VARCHAR(20);
    DECLARE v_father_cccd, v_mother_cccd, v_new_cccd VARCHAR(20);
    DECLARE v_father_id, v_mother_id, v_p_id, v_h_id, v_new_child_id BIGINT;
    DECLARE v_h_town, v_relig, v_addr VARCHAR(255);


    -- Chỉ chạy khi trạng thái chuyển sang 'RESULT_RETURNED'
    IF NEW.dossier_status = 'RESULT_RETURNED'
       AND (OLD.dossier_status IS NULL OR OLD.dossier_status <> 'RESULT_RETURNED')
    THEN
        -- Lấy service_code
        SET v_s_code = (SELECT service_code FROM cat_services WHERE id = NEW.service_id LIMIT 1);

        -- Chỉ xử lý cho các dịch vụ Khai sinh
        IF v_s_code IN ('HS-KS-TRE', 'HK01_TRE') THEN

            -- Trích xuất dữ liệu từ JSON (Tên và Ngày sinh là bắt buộc)
            SET v_c_name = COALESCE(
                NULLIF(JSON_UNQUOTE(JSON_EXTRACT(NEW.form_data, '$.childFullName')), 'null'),
                NULLIF(JSON_UNQUOTE(JSON_EXTRACT(NEW.form_data, '$.child_name')), 'null')
            );

            SET v_c_dob_raw = COALESCE(
                NULLIF(JSON_UNQUOTE(JSON_EXTRACT(NEW.form_data, '$.dateOfBirth')), 'null'),
                NULLIF(JSON_UNQUOTE(JSON_EXTRACT(NEW.form_data, '$.dob')), 'null')
            );

            SET v_c_dob = CAST(v_c_dob_raw AS DATE);

            -- Giới tính (mặc định MALE nếu null) và CCCD cha/mẹ
            SET v_gender = COALESCE(NULLIF(JSON_UNQUOTE(JSON_EXTRACT(NEW.form_data, '$.gender')), 'null'), 'MALE');
            SET v_father_cccd = NULLIF(JSON_UNQUOTE(JSON_EXTRACT(NEW.form_data, '$.fatherIdNumber')), 'null');
            SET v_mother_cccd = NULLIF(JSON_UNQUOTE(JSON_EXTRACT(NEW.form_data, '$.motherIdNumber')), 'null');

            -- Thực hiện nếu có đủ thông tin tối thiểu
            IF v_c_name IS NOT NULL AND v_c_dob IS NOT NULL THEN

                -- Tìm ID của cha và mẹ
                IF v_father_cccd IS NOT NULL THEN
                    SET v_father_id = (SELECT id FROM mock_citizens WHERE cccd = TRIM(v_father_cccd) LIMIT 1);
                END IF;
                
                IF v_mother_cccd IS NOT NULL THEN
                    SET v_mother_id = (SELECT id FROM mock_citizens WHERE cccd = TRIM(v_mother_cccd) LIMIT 1);
                END IF;

                -- Tìm Hộ khẩu (ưu tiên cha, sau đó mẹ)
                IF v_father_id IS NOT NULL THEN
                    SET v_h_id = (SELECT household_id FROM mock_household_members WHERE citizen_id = v_father_id AND status = 1 LIMIT 1);
                    SET v_p_id = v_father_id;
                END IF;

                IF v_h_id IS NULL AND v_mother_id IS NOT NULL THEN
                    SET v_h_id = (SELECT household_id FROM mock_household_members WHERE citizen_id = v_mother_id AND status = 1 LIMIT 1);
                    SET v_p_id = v_mother_id;
                END IF;

                -- Lấy thông tin kế thừa từ phụ huynh (ưu tiên cha, sau đó mẹ)
                IF v_father_id IS NOT NULL THEN
                    SELECT hometown, religion, permanent_address 
                    INTO v_h_town, v_relig, v_addr 
                    FROM mock_citizens WHERE id = v_father_id LIMIT 1;
                ELSEIF v_mother_id IS NOT NULL THEN
                    SELECT hometown, religion, permanent_address 
                    INTO v_h_town, v_relig, v_addr 
                    FROM mock_citizens WHERE id = v_mother_id LIMIT 1;
                END IF;

                -- 1. Tạo bản ghi Công dân mới (CCCD 12 số định dạng chuẩn)
                INSERT INTO mock_citizens
                    (cccd, full_name, dob, gender, hometown, ethnic_group, religion,
                     permanent_address, temporary_address, status)
                VALUES
                    (CONCAT('0', LPAD(FLOOR(RAND() * 99999999999), 11, '0')),
                     v_c_name, v_c_dob, v_gender, v_h_town, 'Kinh', v_relig,
                     COALESCE(v_addr, 'Đà Nẵng'), COALESCE(v_addr, 'Đà Nẵng'), 1);

                SET v_new_child_id = LAST_INSERT_ID();

                -- 2. Thêm vào sổ Hộ khẩu (nếu tìm thấy hộ khẩu của cha/mẹ)
                IF v_h_id IS NOT NULL THEN
                    INSERT INTO mock_household_members
                        (household_id, citizen_id, relation_to_head, move_in_date, status)
                    VALUES
                        (v_h_id, v_new_child_id, 'CON', CURDATE(), 1);
                END IF;


                -- 3. Ghi mối quan hệ gia đình vào bảng mock_citizen_relationships (2 chiều)
                -- Nếu có cha: ghi 2 bản ghi
                IF v_father_id IS NOT NULL THEN
                    -- Con -> Cha
                    INSERT INTO mock_citizen_relationships (citizen_id, relative_id, relationship_type)
                    VALUES (v_new_child_id, v_father_id, 'CHA');
                    
                    -- Cha -> Con
                    INSERT INTO mock_citizen_relationships (citizen_id, relative_id, relationship_type)
                    VALUES (v_father_id, v_new_child_id, 'CON');
                END IF;

                -- Nếu có mẹ: ghi 2 bản ghi
                IF v_mother_id IS NOT NULL THEN
                    -- Con -> Mẹ
                    INSERT INTO mock_citizen_relationships (citizen_id, relative_id, relationship_type)
                    VALUES (v_new_child_id, v_mother_id, 'ME');
                    
                    -- Mẹ -> Con
                    INSERT INTO mock_citizen_relationships (citizen_id, relative_id, relationship_type)
                    VALUES (v_mother_id, v_new_child_id, 'CON');
                END IF;


            END IF;
        END IF;
    END IF;
END$$

DELIMITER ;
