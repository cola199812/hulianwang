-- 用户表测试数据
INSERT IGNORE INTO `user` (`username`, `password`, `email`, `create_time`)
VALUES
('testuser', '$2a$10$8OU9BvsFwbZ3A24SUzwJeO5Qie5daQuNQE0QCFMV//gTmdFnHQ/8S', 'test@example.com', CURRENT_TIMESTAMP);

-- 装备表测试数据（先删除相关外键数据，再清空表，避免重复）
DELETE FROM `equipment_rental`;
DELETE FROM `equipment_checklist_item` WHERE `equipment_id` IS NOT NULL;
DELETE FROM `equipment`;
INSERT INTO `equipment` (`name`, `type`, `description`, `weight`, `image_url`, `daily_rental_price`, `deposit`, `stock`)
VALUES
('三季帐篷', '帐篷', '适合春、夏、秋季使用的轻量化帐篷，防水指数3000mm', 2000, 'https://example.com/tent1.jpg', 50.00, 200.00, 10),
('四季帐篷', '帐篷', '适合全年使用的专业帐篷，防水指数5000mm，抗风性能好', 3500, 'https://example.com/tent2.jpg', 80.00, 300.00, 5),
('羽绒睡袋', '睡袋', '白鸭绒填充，舒适温度-10℃，重量800g', 800, 'https://example.com/sleepingbag1.jpg', 40.00, 150.00, 8),
('棉睡袋', '睡袋', '保暖棉填充，舒适温度0℃，重量1200g', 1200, 'https://example.com/sleepingbag2.jpg', 25.00, 100.00, 12),
('登山杖', '器材', '铝合金材质，可调节长度，带避震功能', 250, 'https://example.com/trekkingpole1.jpg', 10.00, 50.00, 15),
('头灯', '照明', 'LED头灯，亮度300流明，续航时间10小时', 100, 'https://example.com/headlamp1.jpg', 15.00, 60.00, 20),
('登山鞋', '鞋靴', '防水透气，Vibram大底，适合复杂地形', 1200, 'https://example.com/boots1.jpg', 30.00, 120.00, 7),
('冲锋衣', '服装', 'Gore-Tex面料，防水透气，防风性能好', 500, 'https://example.com/jacket1.jpg', 45.00, 180.00, 9),
('登山包', '器材', '60L容量，背负系统舒适，适合多日徒步', 1800, 'https://example.com/backpack1.jpg', 35.00, 140.00, 6),
('炉头', '器材', '轻量化气炉，火力调节方便，适合户外烹饪', 150, 'https://example.com/stove1.jpg', 20.00, 80.00, 11);
