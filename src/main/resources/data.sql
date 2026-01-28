-- =============================================
-- Auth 相關資料
-- =============================================

-- auth_role (角色)
INSERT INTO auth_role (role_code, role_name, created_at, updated_at, created_by, updated_by) VALUES ('ADMIN', '系統管理員', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO auth_role (role_code, role_name, created_at, updated_at, created_by, updated_by) VALUES ('STOREKEEPER', '庫務', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO auth_role (role_code, role_name, created_at, updated_at, created_by, updated_by) VALUES ('LEADER', '組長', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO auth_role (role_code, role_name, created_at, updated_at, created_by, updated_by) VALUES ('SALES', '業務員', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');

-- auth_user (使用者) - 密碼為 BCrypt 加密後的 "password123"
INSERT INTO auth_user (user_id, email, user_name, password, branch_code, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('A001', 'admin@example.com', '管理員', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrE5sNpLkV8VTFhvN5M5CfBZ5W7KXi', 'BR01', '0912345678', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO auth_user (user_id, email, user_name, password, branch_code, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('U001', 'user1@example.com', '王小明', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrE5sNpLkV8VTFhvN5M5CfBZ5W7KXi', 'BR01', '0923456789', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO auth_user (user_id, email, user_name, password, branch_code, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('U002', 'user2@example.com', '李小華', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrE5sNpLkV8VTFhvN5M5CfBZ5W7KXi', 'BR02', '0934567890', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO auth_user (user_id, email, user_name, password, branch_code, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('U003', 'user3@example.com', '二狗子', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrE5sNpLkV8VTFhvN5M5CfBZ5W7KXi', 'BR02', '0934567891', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');

-- auth_user_branch_role (使用者營業所角色關聯)
INSERT INTO auth_user_branch_role (user_id, branch_code, role_code, created_at, updated_at, created_by, updated_by) VALUES ('A001', 'BR01', 'ADMIN', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO auth_user_branch_role (user_id, branch_code, role_code, created_at, updated_at, created_by, updated_by) VALUES ('U001', 'BR01', 'SALES', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO auth_user_branch_role (user_id, branch_code, role_code, created_at, updated_at, created_by, updated_by) VALUES ('U002', 'BR02', 'LEADER', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO auth_user_branch_role (user_id, branch_code, role_code, created_at, updated_at, created_by, updated_by) VALUES ('U003', 'BR02', 'STOREKEEPER', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');

-- =============================================
-- Master 主檔資料
-- =============================================

-- factory (工廠/供應商) - 味全不同地區廠區
INSERT INTO factory (factory_code, factory_name, address, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('F001', '味全台北廠', '台北市內湖區瑞光路513巷26號', '02-26578900', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO factory (factory_code, factory_name, address, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('F002', '味全桃園廠', '桃園市中壢區中園路220號', '03-4526789', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO factory (factory_code, factory_name, address, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('F003', '味全台中廠', '台中市大里區工業路11號', '04-24961234', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO factory (factory_code, factory_name, address, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('F004', '味全高雄廠', '高雄市仁武區水管路100號', '07-3721234', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');

-- product (產品) - 10筆味全產品資料
INSERT INTO product (product_code, product_name, category, base_unit, base_price, status, created_at, updated_at, created_by, updated_by) VALUES ('P001', '林鳳營鮮乳(936ml)', 'REFRIGERATED', '瓶', 75.00, 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product (product_code, product_name, category, base_unit, base_price, status, created_at, updated_at, created_by, updated_by) VALUES ('P002', '林鳳營鮮乳(1857ml)', 'REFRIGERATED', '瓶', 135.00, 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product (product_code, product_name, category, base_unit, base_price, status, created_at, updated_at, created_by, updated_by) VALUES ('P003', '林鳳營低脂鮮乳(1857ml)', 'REFRIGERATED', '瓶', 130.00, 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product (product_code, product_name, category, base_unit, base_price, status, created_at, updated_at, created_by, updated_by) VALUES ('P004', '林鳳營優酪乳-原味(500ml)', 'REFRIGERATED', '瓶', 45.00, 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product (product_code, product_name, category, base_unit, base_price, status, created_at, updated_at, created_by, updated_by) VALUES ('P005', '每日C柳橙汁(1400ml)', 'REFRIGERATED', '瓶', 99.00, 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product (product_code, product_name, category, base_unit, base_price, status, created_at, updated_at, created_by, updated_by) VALUES ('P006', '每日C葡萄柚汁(1400ml)', 'REFRIGERATED', '瓶', 99.00, 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product (product_code, product_name, category, base_unit, base_price, status, created_at, updated_at, created_by, updated_by) VALUES ('P007', '貝納頌咖啡-經典拿鐵(375ml)', 'REFRIGERATED', '瓶', 42.00, 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product (product_code, product_name, category, base_unit, base_price, status, created_at, updated_at, created_by, updated_by) VALUES ('P008', '貝納頌咖啡-榛果風味(375ml)', 'REFRIGERATED', '瓶', 42.00, 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product (product_code, product_name, category, base_unit, base_price, status, created_at, updated_at, created_by, updated_by) VALUES ('P009', '36法郎典藏咖啡(360ml)', 'REFRIGERATED', '瓶', 55.00, 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product (product_code, product_name, category, base_unit, base_price, status, created_at, updated_at, created_by, updated_by) VALUES ('P010', '木瓜牛乳(936ml)', 'REFRIGERATED', '瓶', 65.00, 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');

-- product_factory (產品工廠關聯)
INSERT INTO product_factory (product_code, factory_code, is_default, created_at, updated_at, created_by, updated_by) VALUES ('P001', 'F001', true, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_factory (product_code, factory_code, is_default, created_at, updated_at, created_by, updated_by) VALUES ('P002', 'F004', true, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_factory (product_code, factory_code, is_default, created_at, updated_at, created_by, updated_by) VALUES ('P003', 'F002', true, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_factory (product_code, factory_code, is_default, created_at, updated_at, created_by, updated_by) VALUES ('P004', 'F001', true, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_factory (product_code, factory_code, is_default, created_at, updated_at, created_by, updated_by) VALUES ('P005', 'F003', true, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_factory (product_code, factory_code, is_default, created_at, updated_at, created_by, updated_by) VALUES ('P006', 'F001', true, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_factory (product_code, factory_code, is_default, created_at, updated_at, created_by, updated_by) VALUES ('P007', 'F003', true, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_factory (product_code, factory_code, is_default, created_at, updated_at, created_by, updated_by) VALUES ('P008', 'F004', true, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_factory (product_code, factory_code, is_default, created_at, updated_at, created_by, updated_by) VALUES ('P009', 'F002', true, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_factory (product_code, factory_code, is_default, created_at, updated_at, created_by, updated_by) VALUES ('P010', 'F001', true, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');

-- product_unit_conversion (產品單位換算)
INSERT INTO product_unit_conversion (product_code, from_unit, to_unit, conversion_rate, created_at, updated_at, created_by, updated_by) VALUES ('P001', '箱', '瓶', 12.0000, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_unit_conversion (product_code, from_unit, to_unit, conversion_rate, created_at, updated_at, created_by, updated_by) VALUES ('P002', '箱', '瓶', 6.0000, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_unit_conversion (product_code, from_unit, to_unit, conversion_rate, created_at, updated_at, created_by, updated_by) VALUES ('P003', '箱', '瓶', 6.0000, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_unit_conversion (product_code, from_unit, to_unit, conversion_rate, created_at, updated_at, created_by, updated_by) VALUES ('P004', '箱', '瓶', 12.0000, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_unit_conversion (product_code, from_unit, to_unit, conversion_rate, created_at, updated_at, created_by, updated_by) VALUES ('P005', '箱', '瓶', 6.0000, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_unit_conversion (product_code, from_unit, to_unit, conversion_rate, created_at, updated_at, created_by, updated_by) VALUES ('P006', '箱', '瓶', 6.0000, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_unit_conversion (product_code, from_unit, to_unit, conversion_rate, created_at, updated_at, created_by, updated_by) VALUES ('P007', '箱', '瓶', 24.0000, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_unit_conversion (product_code, from_unit, to_unit, conversion_rate, created_at, updated_at, created_by, updated_by) VALUES ('P008', '箱', '瓶', 24.0000, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_unit_conversion (product_code, from_unit, to_unit, conversion_rate, created_at, updated_at, created_by, updated_by) VALUES ('P009', '箱', '瓶', 24.0000, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO product_unit_conversion (product_code, from_unit, to_unit, conversion_rate, created_at, updated_at, created_by, updated_by) VALUES ('P010', '箱', '瓶', 12.0000, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');

-- customer (客戶)
INSERT INTO customer (customer_code, customer_name, branch_code, address, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('C001', '全家便利商店-信義店', 'BR01', '台北市信義區信義路四段100號', '02-27001234', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO customer (customer_code, customer_name, branch_code, address, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('C002', '7-ELEVEN-忠孝店', 'BR01', '台北市大安區忠孝東路四段200號', '02-27711234', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO customer (customer_code, customer_name, branch_code, address, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('C003', '萊爾富-松山店', 'BR01', '台北市松山區南京東路五段50號', '02-27651234', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO customer (customer_code, customer_name, branch_code, address, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('C004', '全聯福利中心-北屯店', 'BR02', '台中市北屯區文心路四段500號', '04-22341234', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO customer (customer_code, customer_name, branch_code, address, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('C005', '家樂福-台中店', 'BR02', '台中市西屯區台灣大道四段600號', '04-23521234', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO customer (customer_code, customer_name, branch_code, address, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('C006', '頂好超市-霧峰店', 'BR02', '台中市霧峰區中正路800號', '04-23391234', 'INACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');

-- =============================================
-- Branch 相關資料
-- =============================================

-- branch (營業所)
INSERT INTO branch (branch_code, branch_name, address, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('BR01', '信義總部', '台北市信義區忠孝東路一段1號', '02-12345678', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO branch (branch_code, branch_name, address, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('BR02', '北屯營業所', '台中市北屯區陳平路1號', '04-23456789', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO branch (branch_code, branch_name, address, phone, status, created_at, updated_at, created_by, updated_by) VALUES ('BR03', '霧峰營業所', '台中市霧峰區樹仁路25號', '04-14567890', 'INACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');

-- location (儲位)
INSERT INTO location (location_code, location_name, branch_code, user_id, location_type, status, created_at, updated_at, created_by, updated_by) VALUES ('1000', '信義總部倉庫', 'BR01', NULL, 'WAREHOUSE', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO location (location_code, location_name, branch_code, user_id, location_type, status, created_at, updated_at, created_by, updated_by) VALUES ('1200', '王小明', 'BR01', 'U001', 'CAR', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO location (location_code, location_name, branch_code, user_id, location_type, status, created_at, updated_at, created_by, updated_by) VALUES ('2000', '北屯營業所倉庫', 'BR02', NULL, 'WAREHOUSE', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO location (location_code, location_name, branch_code, user_id, location_type, status, created_at, updated_at, created_by, updated_by) VALUES ('2100', '李小華', 'BR02', 'U002', 'CAR', 'ACTIVE', '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');

-- branch_product_list (營業所商品清單)
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('BR01', 'P001', '林鳳營鮮乳(936ml)', '瓶', 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('BR01', 'P002', '林鳳營鮮乳(1857ml)', '瓶', 2, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('BR01', 'P004', '林鳳營優酪乳-原味(500ml)', '瓶', 3, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('BR01', 'P005', '每日C柳橙汁(1400ml)', '瓶', 4, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('BR01', 'P006', '每日C葡萄柚汁(1400ml)', '瓶', 5, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('BR02', 'P001', '林鳳營鮮乳(936ml)', '瓶', 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('BR02', 'P003', '林鳳營低脂鮮乳(1857ml)', '瓶', 2, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('BR02', 'P007', '貝納頌咖啡-經典拿鐵(375ml)', '瓶', 3, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('BR02', 'P009', '36法郎典藏咖啡(360ml)', '瓶', 4, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');
INSERT INTO branch_product_list (branch_code, product_code, product_name, unit, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('BR02', 'P010', '木瓜牛乳(936ml)', '瓶', 5, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'SYSTEM', 'SYSTEM');

-- branch_purchase_frozen (營業所訂貨凍結狀態)
INSERT INTO branch_purchase_frozen (branch_code, purchase_date, status, frozen_at, frozen_by, created_at, updated_at, created_by, updated_by) VALUES ('BR01', '2023-10-01', 'FROZEN', '2023-09-30 18:00:00', 'U001', '2023-09-30 18:00:00', '2023-09-30 18:00:00', 'U001', 'U001');
INSERT INTO branch_purchase_frozen (branch_code, purchase_date, status, frozen_at, frozen_by, confirmed_at, confirmed_by, created_at, updated_at, created_by, updated_by) VALUES ('BR01', '2023-10-02', 'CONFIRMED', '2023-10-01 18:00:00', 'U001', '2023-10-01 20:00:00', 'U001', '2023-10-01 18:00:00', '2023-10-01 20:00:00', 'U001', 'U001');

-- =============================================
-- Purchase 相關資料
-- =============================================

-- sales_purchase_order (業務訂貨單)
INSERT INTO sales_purchase_order (purchase_no, branch_code, location_code, purchase_date, purchase_user, created_at, updated_at, created_by, updated_by) VALUES ('SPO-20231001-001', 'BR01', '1200', '2023-10-01', 'U001', '2023-09-30 10:00:00', '2023-09-30 10:00:00', 'U001', 'U001');
INSERT INTO sales_purchase_order (purchase_no, branch_code, location_code, purchase_date, purchase_user, created_at, updated_at, created_by, updated_by) VALUES ('SPO-20231002-002', 'BR01', '1200', '2023-10-02', 'U001', '2023-10-01 10:00:00', '2023-10-01 10:00:00', 'U001', 'U001');
INSERT INTO sales_purchase_order (purchase_no, branch_code, location_code, purchase_date, purchase_user, created_at, updated_at, created_by, updated_by) VALUES ('SPO-20231003-003', 'BR02', '2100', '2023-10-03', 'U002', '2023-10-02 10:00:00', '2023-10-02 10:00:00', 'U002', 'U002');

-- sales_purchase_order_detail (業務訂貨單明細)
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirmed_qty, status, created_at, updated_at, created_by, updated_by) VALUES ('SPO-20231001-001', 1, 'P001', '瓶', 100, 100, 'AGGREGATED', '2023-09-30 10:00:00', '2023-09-30 10:00:00', 'U001', 'U001');
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirmed_qty, status, created_at, updated_at, created_by, updated_by) VALUES ('SPO-20231001-001', 2, 'P004', '瓶', 50, 50, 'AGGREGATED', '2023-09-30 10:00:00', '2023-09-30 10:00:00', 'U001', 'U001');
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirmed_qty, status, created_at, updated_at, created_by, updated_by) VALUES ('SPO-20231002-002', 1, 'P005', '瓶', 200, 180, 'AGGREGATED', '2023-10-01 10:00:00', '2023-10-01 10:00:00', 'U001', 'U001');
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirmed_qty, status, created_at, updated_at, created_by, updated_by) VALUES ('SPO-20231003-003', 1, 'P001', '瓶', 150, 0, 'PENDING', '2023-10-02 10:00:00', '2023-10-02 10:00:00', 'U002', 'U002');
INSERT INTO sales_purchase_order_detail (purchase_no, item_no, product_code, unit, qty, confirmed_qty, status, created_at, updated_at, created_by, updated_by) VALUES ('SPO-20231003-003', 2, 'P007', '瓶', 30, 0, 'PENDING', '2023-10-02 10:00:00', '2023-10-02 10:00:00', 'U002', 'U002');

-- sales_purchase_list (業務自訂訂貨清單)
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('1200', 'P001', '瓶', 50, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'U001', 'U001');
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('1200', 'P004', '個', 30, 2, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'U001', 'U001');
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('1200', 'P006', '瓶', 100, 3, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'U001', 'U001');
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('2100', 'P001', '瓶', 80, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'U002', 'U002');
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('2100', 'P003', '瓶', 40, 2, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'U002', 'U002');
INSERT INTO sales_purchase_list (location_code, product_code, unit, qty, sort_order, created_at, updated_at, created_by, updated_by) VALUES ('2100', 'P009', '瓶', 60, 3, '2023-01-01 00:00:00', '2023-01-01 00:00:00', 'U002', 'U002');

-- =============================================
-- Sequence 相關資料
-- =============================================

-- document_sequence (單據序號表) - 此表不需要 audit 欄位
INSERT INTO document_sequence (sequence_type, sequence_date, current_no, version) VALUES ('SPO', '2023-10-01', 1, 0);
INSERT INTO document_sequence (sequence_type, sequence_date, current_no, version) VALUES ('SPO', '2023-10-02', 2, 0);
INSERT INTO document_sequence (sequence_type, sequence_date, current_no, version) VALUES ('SPO', '2023-10-03', 3, 0);
