ALTER TABLE cat_services
    ADD COLUMN dept_id BIGINT,
    ADD CONSTRAINT fk_cat_services_dept_id
        FOREIGN KEY (dept_id) REFERENCES sys_departments(id);