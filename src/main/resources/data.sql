-- =============================================
-- Auth 相關資料
-- =============================================

-- auth_role (角色)
INSERT INTO auth_role (role_code, role_name) VALUES ('ADMIN', '系統管理員');
INSERT INTO auth_role (role_code, role_name) VALUES ('USER', '一般使用者');
INSERT INTO auth_role (role_code, role_name) VALUES ('MANAGER', '主管');
INSERT INTO auth_role (role_code, role_name) VALUES ('SALES', '業務員');

-- auth_user (使用者) - 密碼為 BCrypt 加密後的 "password123"
INSERT INTO auth_user (user_id, email, user_name, password, branch_code, phone, status) VALUES ('A001', 'admin@example.com', '管理員', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrE5sNpLkV8VTFhvN5M5CfBZ5W7KXi', 'BR01', '0912345678', 'ACTIVE');
INSERT INTO auth_user (user_id, email, user_name, password, branch_code, phone, status) VALUES ('U001', 'user1@example.com', '王小明', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrE5sNpLkV8VTFhvN5M5CfBZ5W7KXi', 'BR01', '0923456789', 'ACTIVE');
INSERT INTO auth_user (user_id, email, user_name, password, branch_code, phone, status) VALUES ('U002', 'user2@example.com', '李小華', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrE5sNpLkV8VTFhvN5M5CfBZ5W7KXi', 'BR02', '0934567890', 'ACTIVE');

-- auth_user_branch_role (使用者營業所角色關聯)
INSERT INTO auth_user_branch_role (user_id, branch_code, role_code) VALUES ('A001', 'BR01', 'ADMIN');
INSERT INTO auth_user_branch_role (user_id, branch_code, role_code) VALUES ('U001', 'BR01', 'SALES');
INSERT INTO auth_user_branch_role (user_id, branch_code, role_code) VALUES ('U002', 'BR02', 'MANAGER');

-- =============================================
-- Branch 相關資料
-- =============================================

-- branch (營業所)
INSERT INTO branch (branch_code, branch_name, address, phone, status) VALUES ('BR01', '台北總店', '台北市中正區忠孝東路一段1號', '02-12345678', 'ACTIVE');
INSERT INTO branch (branch_code, branch_name, address, phone, status) VALUES ('BR02', '台中分店', '台中市西區台灣大道二段2號', '04-23456789', 'ACTIVE');
INSERT INTO branch (branch_code, branch_name, address, phone, status) VALUES ('BR03', '高雄分店', '高雄市前鎮區中山二路3號', '07-34567890', 'INACTIVE');

-- location (儲位)
INSERT INTO location (location_code, location_name, branch_code, user_id, location_type, status) VALUES ('1000', '台北倉庫', 'BR01', NULL, 'WAREHOUSE', 'ACTIVE');
INSERT INTO location (location_code, location_name, branch_code, user_id, location_type, status) VALUES ('1200', '王小明車輛', 'BR01', 'U001', 'CAR', 'ACTIVE');
INSERT INTO location (location_code, location_name, branch_code, user_id, location_type, status) VALUES ('2000', '台中倉庫', 'BR02', NULL, 'WAREHOUSE', 'ACTIVE');
INSERT INTO location (location_code, location_name, branch_code, user_id, location_type, status) VALUES ('2100', '李小華車輛', 'BR02', 'U002', 'CAR', 'ACTIVE');

-- branch_product_list (營業所商品清單)
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order) VALUES ('BR01', 'P001', '商品A', 'PCS', 1);
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order) VALUES ('BR01', 'P002', '商品B', 'BOX', 2);
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order) VALUES ('BR01', 'P003', '商品C', 'PCS', 3);
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order) VALUES ('BR02', 'P001', '商品A', 'PCS', 1);
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order) VALUES ('BR02', 'P004', '商品D', 'KG', 2);

-- branch_purchase_frozen (營業所訂貨凍結狀態)
INSERT INTO branch_purchase_frozen (branch_code, purchase_date, frozen_status) VALUES ('BR01', '2023-10-01', 'FROZEN');
INSERT INTO branch_purchase_frozen (branch_code, purchase_date, frozen_status) VALUES ('BR01', '2023-10-02', 'FROZEN');
INSERT INTO branch_purchase_frozen (branch_code, purchase_date, frozen_status) VALUES ('BR02', '2023-10-03', NULL);

-- =============================================
-- Purchase 相關資料
-- =============================================

-- sales_purchase_order (業務訂貨單)
INSERT INTO sales_purchase_order (purchase_no, branch_code, location_code, purchase_date, purchase_user) VALUES ('SPO-20231001-001', 'BR01', '1200', '2023-10-01', 'U001');
INSERT INTO sales_purchase_order (purchase_no, branch_code, location_code, purchase_date, purchase_user) VALUES ('SPO-20231002-002', 'BR01', '1200', '2023-10-02', 'U001');
INSERT INTO sales_purchase_order (purchase_no, branch_code, location_code, purchase_date, purchase_user) VALUES ('SPO-20231003-003', 'BR02', '2100', '2023-10-03', 'U002');

-- sales_purchase_order_detail (業務訂貨單明細)
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirmed_qty, status) VALUES ('SPO-20231001-001', 1, 'P001', 'PCS', 100, 100, 'CONFIRMED');
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirmed_qty, status) VALUES ('SPO-20231001-001', 2, 'P002', 'BOX', 50, 50, 'CONFIRMED');
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirmed_qty, status) VALUES ('SPO-20231002-002', 1, 'P003', 'PCS', 200, 180, 'CONFIRMED');
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirmed_qty, status) VALUES ('SPO-20231003-003', 1, 'P001', 'PCS', 150, 0, 'PENDING');
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirmed_qty, status) VALUES ('SPO-20231003-003', 2, 'P004', 'KG', 30, 0, 'PENDING');

-- sales_purchase_list (業務自訂訂貨清單)
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order) VALUES ('1200', 'P001', 'PCS', 300, 1);
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order) VALUES ('1200', 'P002', 'BOX', 100, 2);
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order) VALUES ('2100', 'P001', 'PCS', 400, 1);
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order) VALUES ('2100', 'P004', 'KG', 100, 2);

-- =============================================
-- Sequence 相關資料
-- =============================================

-- document_sequence (單據序號表)
INSERT INTO document_sequence (sequence_type, sequence_date, current_no, version) VALUES ('SPO', '2023-10-01', 1, 0);
INSERT INTO document_sequence (sequence_type, sequence_date, current_no, version) VALUES ('SPO', '2023-10-02', 2, 0);
INSERT INTO document_sequence (sequence_type, sequence_date, current_no, version) VALUES ('SPO', '2023-10-03', 3, 0);
