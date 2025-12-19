-- Mật khẩu là: password@123
-- Hash BCrypt: $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.S67v16C

INSERT INTO sys_users (username, password_hash, full_name, user_type, citizen_id)
VALUES ('048090000123', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.S67v16C', 'Nguyễn Văn An', 'CITIZEN', 1);

-- Gán quyền cho user
INSERT INTO sys_user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM sys_users u, sys_roles r 
WHERE u.username = '048090000123' AND r.role_name = 'ADMIN'; -- Tạm thời cho hẳn quyền ADMIN để test
