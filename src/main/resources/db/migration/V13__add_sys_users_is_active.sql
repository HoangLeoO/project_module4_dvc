-- Add missing is_active flag for sys_users to match SysUser entity.
ALTER TABLE sys_users
    ADD COLUMN  is_active BOOLEAN NOT NULL DEFAULT 1;
