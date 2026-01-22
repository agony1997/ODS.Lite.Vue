-- =============================================
-- Auth 相關資料
-- =============================================

-- auth_role (角色)
INSERT INTO auth_role (role_code, role_name) VALUES ('ADMIN', '系統管理員');
INSERT INTO auth_role (role_code, role_name) VALUES ('USER', '一般使用者');
INSERT INTO auth_role (role_code, role_name) VALUES ('MANAGER', '主管');

-- auth_user (使用者) - 密碼為 BCrypt 加密後的 "password123"
INSERT INTO auth_user (emp_no, email, emp_name, password) VALUES ('A001', 'admin@example.com', '管理員', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrE5sNpLkV8VTFhvN5M5CfBZ5W7KXi');
INSERT INTO auth_user (emp_no, email, emp_name, password) VALUES ('U001', 'user1@example.com', '王小明', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrE5sNpLkV8VTFhvN5M5CfBZ5W7KXi');
INSERT INTO auth_user (emp_no, email, emp_name, password) VALUES ('U002', 'user2@example.com', '李小華', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrE5sNpLkV8VTFhvN5M5CfBZ5W7KXi');

-- auth_user_role (使用者角色關聯)
INSERT INTO auth_user_role (emp_no, role_code) VALUES ('A001', 'ADMIN');
INSERT INTO auth_user_role (emp_no, role_code) VALUES ('U001', 'USER');
INSERT INTO auth_user_role (emp_no, role_code) VALUES ('U002', 'MANAGER');

-- =============================================
-- Branch 相關資料
-- =============================================

-- branch (分店)
INSERT INTO branch (branch_code, branch_name, location_code, is_enable) VALUES ('BR01', '台北總店', '1000', true);
INSERT INTO branch (branch_code, branch_name, location_code, is_enable) VALUES ('BR02', '台中分店', '2000', true);
INSERT INTO branch (branch_code, branch_name, location_code, is_enable) VALUES ('BR03', '高雄分店', '3000', false);

-- location (據點)
INSERT INTO location (location_code, location_name, branch_code, emp_no, emp_name, is_enable) VALUES ('1000', '台北倉庫', 'BR01', 'A001', '管理員', true);
INSERT INTO location (location_code, location_name, branch_code, emp_no, emp_name, is_enable) VALUES ('1200', '台北門市', 'BR01', 'U001', '王小明', true);
INSERT INTO location (location_code, location_name, branch_code, emp_no, emp_name, is_enable) VALUES ('2000', '台中倉庫', 'BR02', 'U002', '李小華', true);

-- branch_product_list (分店商品清單)
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order) VALUES ('BR01', 'P001', '商品A', 'PCS', 1);
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order) VALUES ('BR01', 'P002', '商品B', 'BOX', 2);
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order) VALUES ('BR01', 'P003', '商品C', 'PCS', 3);
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order) VALUES ('BR02', 'P001', '商品A', 'PCS', 1);
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order) VALUES ('BR02', 'P004', '商品D', 'KG', 2);

-- branch_purchase_frozen (分店進貨凍結狀態)
INSERT INTO branch_purchase_frozen (branch_code, is_frozen) VALUES ('BR01', false);
INSERT INTO branch_purchase_frozen (branch_code, is_frozen) VALUES ('BR02', false);
INSERT INTO branch_purchase_frozen (branch_code, is_frozen) VALUES ('BR03', true);

-- =============================================
-- Purchase 相關資料
-- =============================================

-- sales_purchase_order (進貨訂單)
INSERT INTO sales_purchase_order (purchase_no, branch_code, location_code, purchase_date, purchase_user, is_frozen) VALUES ('SPO-20231001-001', 'BR01', '1200', '2023-10-01', 'A001', false);
INSERT INTO sales_purchase_order (purchase_no, branch_code, location_code, purchase_date, purchase_user, is_frozen) VALUES ('SPO-20231002-002', 'BR01', '1000', '2023-10-02', 'A001', true);
INSERT INTO sales_purchase_order (purchase_no, branch_code, location_code, purchase_date, purchase_user, is_frozen) VALUES ('SPO-20231003-003', 'BR02', '2000', '2023-10-03', 'U002', false);

-- sales_purchase_order_detail (進貨訂單明細)
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirm_qty) VALUES ('SPO-20231001-001', 1, 'P001', 'PCS', 100, 100);
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirm_qty) VALUES ('SPO-20231001-001', 2, 'P002', 'BOX', 50, 50);
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirm_qty) VALUES ('SPO-20231002-002', 1, 'P003', 'PCS', 200, 180);
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirm_qty) VALUES ('SPO-20231003-003', 1, 'P001', 'PCS', 150, 0);
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirm_qty) VALUES ('SPO-20231003-003', 2, 'P004', 'KG', 30, 0);

-- sales_purchase_list (進貨清單)
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order) VALUES ('1000', 'P001', 'PCS', 500, 1);
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order) VALUES ('1000', 'P002', 'BOX', 200, 2);
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order) VALUES ('1200', 'P001', 'PCS', 300, 1);
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order) VALUES ('2000', 'P001', 'PCS', 400, 1);
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order) VALUES ('2000', 'P004', 'KG', 100, 2);

-- =============================================
-- Sequence 相關資料
-- =============================================

-- document_sequence (單據序號表)
INSERT INTO document_sequence (sequence_type, sequence_date, current_no, version) VALUES ('SPO', '2023-10-01', 1, 0);
INSERT INTO document_sequence (sequence_type, sequence_date, current_no, version) VALUES ('SPO', '2023-10-02', 2, 0);
INSERT INTO document_sequence (sequence_type, sequence_date, current_no, version) VALUES ('SPO', '2023-10-03', 3, 0);
