CREATE TABLE IF NOT EXISTS `post` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `title` VARCHAR(100) NULL,
  `markdown` TEXT NULL,
  `location_name` VARCHAR(100) NULL,
  `lat` DECIMAL(10,6) NULL,
  `lng` DECIMAL(10,6) NULL,
  `like_count` INT DEFAULT 0,
  `comment_count` INT DEFAULT 0,
  `view_count` INT DEFAULT 0,
  `cover_url` VARCHAR(255) NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  nickname VARCHAR(64) NULL,
  avatar_url VARCHAR(255) NULL,
  gender VARCHAR(10) NULL,
  birthday DATE NULL,
  bio TEXT NULL,
  phone VARCHAR(11) NULL,
  create_time DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET @col_nickname := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'nickname'
);
SET @sql_nickname := IF(@col_nickname = 0, 'ALTER TABLE `user` ADD COLUMN `nickname` VARCHAR(64) NULL', 'SELECT 1');
PREPARE stmt FROM @sql_nickname;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_avatar_url := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'avatar_url'
);
SET @sql_avatar_url := IF(@col_avatar_url = 0, 'ALTER TABLE `user` ADD COLUMN `avatar_url` VARCHAR(255) NULL', 'SELECT 1');
PREPARE stmt FROM @sql_avatar_url;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_gender := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'gender'
);
SET @sql_gender := IF(@col_gender = 0, 'ALTER TABLE `user` ADD COLUMN `gender` VARCHAR(10) NULL', 'SELECT 1');
PREPARE stmt FROM @sql_gender;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_birthday := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'birthday'
);
SET @sql_birthday := IF(@col_birthday = 0, 'ALTER TABLE `user` ADD COLUMN `birthday` DATE NULL', 'SELECT 1');
PREPARE stmt FROM @sql_birthday;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_bio := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'bio'
);
SET @sql_bio := IF(@col_bio = 0, 'ALTER TABLE `user` ADD COLUMN `bio` TEXT NULL', 'SELECT 1');
PREPARE stmt FROM @sql_bio;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_phone := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'phone'
);
SET @sql_phone := IF(@col_phone = 0, 'ALTER TABLE `user` ADD COLUMN `phone` VARCHAR(11) NULL', 'SELECT 1');
PREPARE stmt FROM @sql_phone;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


CREATE TABLE IF NOT EXISTS `topic` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(50) UNIQUE NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `post_topic` (
  `post_id` BIGINT NOT NULL,
  `topic_id` BIGINT NOT NULL,
  PRIMARY KEY (`post_id`, `topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `notification` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `type` VARCHAR(50) NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `content` TEXT NOT NULL,
  `read_status` TINYINT NOT NULL DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `media` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `post_id` BIGINT NULL,
  `type` VARCHAR(32) NOT NULL,
  `url` VARCHAR(255) NOT NULL,
  `thumbnail_url` VARCHAR(255) NULL,
  `album` VARCHAR(128) NULL,
  `tags` VARCHAR(255) NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `route` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `distance` DECIMAL(10, 2),
  `level` VARCHAR(20),
  `description` TEXT,
  `creator_id` BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `activity` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `route_id` BIGINT NOT NULL,
  `time` DATETIME NOT NULL,
  `max_people` INT NOT NULL,
  `creator_id` BIGINT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `activity_user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `activity_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  UNIQUE KEY `uk_activity_user` (`activity_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET @col_activity_creator := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'activity' AND COLUMN_NAME = 'creator_id'
);
SET @sql_activity_creator := IF(@col_activity_creator = 0, 'ALTER TABLE `activity` ADD COLUMN `creator_id` BIGINT NULL', 'SELECT 1');
PREPARE stmt_activity_creator FROM @sql_activity_creator;
EXECUTE stmt_activity_creator;
DEALLOCATE PREPARE stmt_activity_creator;

UPDATE activity a
JOIN route r ON a.route_id = r.id
SET a.creator_id = r.creator_id
WHERE a.creator_id IS NULL;

CREATE TABLE IF NOT EXISTS `post_image` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `post_id` BIGINT,
  `image_url` VARCHAR(255),
  `description` VARCHAR(100),
  `sort_order` INT,
  `tag` VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `post_video` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `post_id` BIGINT,
  `video_url` VARCHAR(255),
  `cover_url` VARCHAR(255),
  `duration` INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `post_like` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_post_user` (`post_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `parent_id` BIGINT NULL,
  `content` TEXT NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `comment_like` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `comment_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_comment_user` (`comment_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- 装备表
CREATE TABLE IF NOT EXISTS `equipment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `type` VARCHAR(128) NOT NULL,
  `description` TEXT NULL,
  `weight` INT NULL,
  `image_url` VARCHAR(255) NULL,
  `daily_rental_price` DOUBLE NOT NULL,
  `deposit` DOUBLE NOT NULL,
  `stock` INT NOT NULL DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 装备租赁表
CREATE TABLE IF NOT EXISTS `equipment_rental` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `equipment_id` BIGINT NOT NULL,
  `start_time` DATETIME NOT NULL,
  `end_time` DATETIME NOT NULL,
  `rental_days` INT NOT NULL,
  `daily_rental_price` DOUBLE NOT NULL,
  `deposit` DOUBLE NOT NULL,
  `total_price` DOUBLE NOT NULL,
  `payment_status` VARCHAR(32) NOT NULL DEFAULT '未支付',
  `rental_status` VARCHAR(32) NOT NULL DEFAULT '预订中',
  `contact_phone` VARCHAR(11) NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
  FOREIGN KEY (`equipment_id`) REFERENCES `equipment`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 装备清单表
CREATE TABLE IF NOT EXISTS `equipment_checklist` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `route_type` VARCHAR(128) NOT NULL,
  `weather` VARCHAR(128) NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 装备清单项表
CREATE TABLE IF NOT EXISTS `equipment_checklist_item` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `checklist_id` BIGINT NOT NULL,
  `equipment_id` BIGINT NULL,
  `item_name` VARCHAR(255) NULL,
  `item_type` VARCHAR(128) NOT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  `is_packed` BOOLEAN NOT NULL DEFAULT FALSE,
  `is_rental` BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (`checklist_id`) REFERENCES `equipment_checklist`(`id`),
  FOREIGN KEY (`equipment_id`) REFERENCES `equipment`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 个人装备表
CREATE TABLE IF NOT EXISTS `personal_equipment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `type` VARCHAR(128) NOT NULL,
  `brand` VARCHAR(128) NULL,
  `purchase_date` DATETIME NULL,
  `purchase_price` DOUBLE NULL,
  `usage_count` INT NOT NULL DEFAULT 0,
  `last_usage_date` DATETIME NULL,
  `next_maintenance_date` DATETIME NULL,
  `status` VARCHAR(32) NOT NULL DEFAULT '良好',
  `image_url` VARCHAR(255) NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `note` VARCHAR(255) NULL,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `equipment` (`id`, `name`, `type`, `description`, `weight`, `image_url`, `daily_rental_price`, `deposit`, `stock`)
VALUES (1, '轻量双人帐篷', '帐篷', '防风防雨，适合周末露营', 2100, NULL, 35.0, 200.0, 5)
ON DUPLICATE KEY UPDATE
  `name`=VALUES(`name`), `type`=VALUES(`type`), `description`=VALUES(`description`), `weight`=VALUES(`weight`),
  `image_url`=VALUES(`image_url`), `daily_rental_price`=VALUES(`daily_rental_price`), `deposit`=VALUES(`deposit`), `stock`=VALUES(`stock`);

INSERT INTO `equipment` (`id`, `name`, `type`, `description`, `weight`, `image_url`, `daily_rental_price`, `deposit`, `stock`)
VALUES (2, '四季睡袋 -10℃', '睡袋', '保暖耐用，低温可用', 1400, NULL, 22.0, 120.0, 8)
ON DUPLICATE KEY UPDATE
  `name`=VALUES(`name`), `type`=VALUES(`type`), `description`=VALUES(`description`), `weight`=VALUES(`weight`),
  `image_url`=VALUES(`image_url`), `daily_rental_price`=VALUES(`daily_rental_price`), `deposit`=VALUES(`deposit`), `stock`=VALUES(`stock`);

INSERT INTO `equipment` (`id`, `name`, `type`, `description`, `weight`, `image_url`, `daily_rental_price`, `deposit`, `stock`)
VALUES (3, '登山杖(碳纤维) 单支', '器材', '轻便减震，适合长距离徒步', 240, NULL, 8.0, 50.0, 20)
ON DUPLICATE KEY UPDATE
  `name`=VALUES(`name`), `type`=VALUES(`type`), `description`=VALUES(`description`), `weight`=VALUES(`weight`),
  `image_url`=VALUES(`image_url`), `daily_rental_price`=VALUES(`daily_rental_price`), `deposit`=VALUES(`deposit`), `stock`=VALUES(`stock`);

INSERT INTO `equipment` (`id`, `name`, `type`, `description`, `weight`, `image_url`, `daily_rental_price`, `deposit`, `stock`)
VALUES (4, '头灯 350流明', '照明', 'USB充电，夜行必备', 90, NULL, 6.0, 40.0, 15)
ON DUPLICATE KEY UPDATE
  `name`=VALUES(`name`), `type`=VALUES(`type`), `description`=VALUES(`description`), `weight`=VALUES(`weight`),
  `image_url`=VALUES(`image_url`), `daily_rental_price`=VALUES(`daily_rental_price`), `deposit`=VALUES(`deposit`), `stock`=VALUES(`stock`);

INSERT INTO `equipment` (`id`, `name`, `type`, `description`, `weight`, `image_url`, `daily_rental_price`, `deposit`, `stock`)
VALUES (5, '徒步鞋(中帮) 42码', '鞋靴', '防滑耐磨，适合山地徒步', 980, NULL, 18.0, 100.0, 6)
ON DUPLICATE KEY UPDATE
  `name`=VALUES(`name`), `type`=VALUES(`type`), `description`=VALUES(`description`), `weight`=VALUES(`weight`),
  `image_url`=VALUES(`image_url`), `daily_rental_price`=VALUES(`daily_rental_price`), `deposit`=VALUES(`deposit`), `stock`=VALUES(`stock`);

INSERT INTO `equipment` (`id`, `name`, `type`, `description`, `weight`, `image_url`, `daily_rental_price`, `deposit`, `stock`)
VALUES (6, '冲锋衣(防水) L码', '服装', '防风防雨，通勤露营两用', 680, NULL, 16.0, 120.0, 10)
ON DUPLICATE KEY UPDATE
  `name`=VALUES(`name`), `type`=VALUES(`type`), `description`=VALUES(`description`), `weight`=VALUES(`weight`),
  `image_url`=VALUES(`image_url`), `daily_rental_price`=VALUES(`daily_rental_price`), `deposit`=VALUES(`deposit`), `stock`=VALUES(`stock`);

INSERT INTO `user` (`id`, `username`, `password`, `email`, `nickname`, `avatar_url`, `gender`, `create_time`)
VALUES 
(1, 'demo1', '$2a$10$Dow1d1YI7DiJ9N6byN1NsOl7UZ5EKeosFVJeFt3PcTJS3BM4tiTnW', 'demo1@example.com', '探索者', 'https://i.pravatar.cc/150?img=3', 'male', NOW()),
(2, 'demo2', '$2a$10$Dow1d1YI7DiJ9N6byN1NsOl7UZ5EKeosFVJeFt3PcTJS3BM4tiTnW', 'demo2@example.com', '旅人', 'https://i.pravatar.cc/150?img=5', 'female', NOW()),
(3, 'demo3', '$2a$10$Dow1d1YI7DiJ9N6byN1NsOl7UZ5EKeosFVJeFt3PcTJS3BM4tiTnW', 'demo3@example.com', '越野客', 'https://i.pravatar.cc/150?img=7', 'male', NOW())
ON DUPLICATE KEY UPDATE
`username`=VALUES(`username`), `email`=VALUES(`email`), `nickname`=VALUES(`nickname`), `avatar_url`=VALUES(`avatar_url`), `gender`=VALUES(`gender`);

INSERT INTO `route` (`id`, `name`, `distance`, `level`, `description`, `creator_id`)
VALUES
(1, '西湖环线', 13.20, '轻松', '湖畔步道与林荫相间，适合周末慢跑与散步', 1),
(2, '古道穿越', 18.50, '中等', '穿越古道与村落，观景与人文并存', 1),
(3, '山地越野', 25.00, '困难', '高海拔起伏，适合有经验的越野跑者', 2),
(4, '滨海绿道', 9.80, '轻松', '海风与沙滩相伴的城市休闲路线', 2),
(5, '森林栈道', 12.60, '中等', '原始森林栈道，负氧离子爆棚', 3),
(6, '城市夜跑线', 7.40, '轻松', '城市夜景与地标打卡，适合入门夜跑', 3)
ON DUPLICATE KEY UPDATE
`name`=VALUES(`name`), `distance`=VALUES(`distance`), `level`=VALUES(`level`), `description`=VALUES(`description`), `creator_id`=VALUES(`creator_id`);

INSERT INTO `activity` (`id`, `name`, `route_id`, `time`, `max_people`, `creator_id`)
VALUES
(1, '周末环湖跑', 1, '2026-01-10 09:00:00', 30, 1),
(2, '古道轻徒步', 2, '2026-01-11 08:30:00', 20, 1),
(3, '山地越野体验', 3, '2026-01-17 07:00:00', 15, 2),
(4, '滨海观景骑行', 4, '2026-01-12 16:00:00', 25, 2),
(5, '森栈清肺行', 5, '2026-01-09 10:00:00', 18, 3),
(6, '城市灯光夜跑', 6, '2026-01-13 19:30:00', 40, 3)
ON DUPLICATE KEY UPDATE
`name`=VALUES(`name`), `route_id`=VALUES(`route_id`), `time`=VALUES(`time`), `max_people`=VALUES(`max_people`), `creator_id`=VALUES(`creator_id`);

INSERT IGNORE INTO `topic` (`id`, `name`)
VALUES
(1, '徒步'),
(2, '露营'),
(3, '跑步'),
(4, '登山'),
(5, '城市漫步');

INSERT INTO `post` (`id`, `user_id`, `title`, `markdown`, `location_name`, `lat`, `lng`, `like_count`, `comment_count`, `view_count`, `cover_url`, `create_time`)
VALUES
(1, 1, '冬日西湖', '湖面薄雾，跑过栈桥与林荫', '杭州西湖', 30.2500, 120.1600, 120, 24, 600, 'https://images.unsplash.com/photo-1519681393784-d120267933ba?w=1200', '2025-12-18 09:00:00'),
(2, 2, '古道人文穿越', '石板路与老村落，远处炊烟袅袅', '江南古道', 31.2300, 121.4700, 86, 18, 420, 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=1200', '2025-12-17 20:10:00'),
(3, 2, '山地越野初体验', '高心率区间控制与补给点布置', '郊野山地', 30.6300, 104.0700, 210, 35, 980, 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=1200', '2025-12-16 23:00:00'),
(4, 3, '海风与日落', '滨海绿道，夕阳下的跑步与骑行', '滨海绿道', 22.5400, 114.0600, 75, 10, 300, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1200', '2025-12-15 18:30:00'),
(5, 3, '森林栈道负氧', '深呼吸，慢行，听风吹树叶的声音', '城市森林公园', 23.1300, 113.2600, 130, 22, 560, 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=1200', '2025-12-14 09:20:00')
ON DUPLICATE KEY UPDATE
`title`=VALUES(`title`), `markdown`=VALUES(`markdown`), `location_name`=VALUES(`location_name`), `lat`=VALUES(`lat`), `lng`=VALUES(`lng`), `like_count`=VALUES(`like_count`), `comment_count`=VALUES(`comment_count`), `view_count`=VALUES(`view_count`), `cover_url`=VALUES(`cover_url`), `create_time`=VALUES(`create_time`);

INSERT INTO `post_image` (`id`, `post_id`, `image_url`, `description`, `sort_order`, `tag`)
VALUES
(1, 1, 'https://images.unsplash.com/photo-1470770903676-69b98201ea1c?w=1200', '晨跑起点', 1, '跑步'),
(2, 1, 'https://images.unsplash.com/photo-1495395226200-8b01ddb0f3d5?w=1200', '栈桥薄雾', 2, '风景'),
(3, 2, 'https://images.unsplash.com/photo-1493246507139-91e8fad9978e?w=1200', '石板路', 1, '人文'),
(4, 3, 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=1200', '越野路段', 1, '越野')
ON DUPLICATE KEY UPDATE
`post_id`=VALUES(`post_id`), `image_url`=VALUES(`image_url`), `description`=VALUES(`description`), `sort_order`=VALUES(`sort_order`), `tag`=VALUES(`tag`);

INSERT INTO `post_topic` (`post_id`, `topic_id`)
VALUES
(1, 3),
(1, 5),
(2, 1),
(2, 5),
(3, 4),
(4, 3),
(5, 2)
ON DUPLICATE KEY UPDATE `post_id`=`post_id`, `topic_id`=`topic_id`;

INSERT INTO `notification` (`id`, `user_id`, `type`, `title`, `content`, `read_status`, `create_time`)
VALUES
(1, 1, 'activity', '你报名的活动时间提醒', '集合点：市体育馆南门', 0, '2025-12-18 09:00:00'),
(2, 1, 'system', '路线更新提醒', '路线“西湖环线”更新了描述', 0, '2025-12-17 20:10:00'),
(3, 1, 'system', '系统维护公告', '凌晨 1:00-2:00 进行短时维护', 1, '2025-12-16 23:00:00')
ON DUPLICATE KEY UPDATE
`user_id`=VALUES(`user_id`), `type`=VALUES(`type`), `title`=VALUES(`title`), `content`=VALUES(`content`), `read_status`=VALUES(`read_status`), `create_time`=VALUES(`create_time`);

CREATE TABLE IF NOT EXISTS `route_comment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `route_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `user_name` VARCHAR(64) NULL,
  `user_avatar` VARCHAR(255) NULL,
  `content` TEXT NOT NULL,
  `rating` INT NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `like_count` INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `route_favorite` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `route_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_route_user` (`route_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `route_comment` (`id`, `route_id`, `user_id`, `user_name`, `user_avatar`, `content`, `rating`, `create_time`, `like_count`)
VALUES
(1, 1, 1, '探索者', 'https://i.pravatar.cc/150?img=3', '环湖跑线路平缓，风景优美，适合入门', 5, '2025-12-18 10:00:00', 12),
(2, 1, 2, '旅人', 'https://i.pravatar.cc/150?img=5', '人稍微有点多，建议早上出发', 4, '2025-12-18 11:20:00', 6),
(3, 2, 3, '越野客', 'https://i.pravatar.cc/150?img=7', '古道人文氛围不错，石板路要注意防滑', 4, '2025-12-17 09:30:00', 3),
(4, 3, 2, '旅人', 'https://i.pravatar.cc/150?img=5', '越野段难度不小，补给点设置合理', 5, '2025-12-16 08:10:00', 8),
(5, 4, 3, '越野客', 'https://i.pravatar.cc/150?img=7', '滨海风很大，建议带风衣', 3, '2025-12-15 18:40:00', 2)
ON DUPLICATE KEY UPDATE
`route_id`=VALUES(`route_id`), `user_id`=VALUES(`user_id`), `user_name`=VALUES(`user_name`), `user_avatar`=VALUES(`user_avatar`), `content`=VALUES(`content`), `rating`=VALUES(`rating`), `create_time`=VALUES(`create_time`), `like_count`=VALUES(`like_count`);

INSERT INTO `route_favorite` (`route_id`, `user_id`, `create_time`)
VALUES
(1, 1, '2025-12-18 10:05:00'),
(2, 1, '2025-12-17 09:35:00'),
(3, 2, '2025-12-16 08:15:00')
ON DUPLICATE KEY UPDATE
`create_time`=VALUES(`create_time`);

INSERT INTO `comment` (`id`, `post_id`, `user_id`, `parent_id`, `content`, `create_time`)
VALUES
(1, 1, 2, NULL, '西湖冬日太美了！', '2025-12-18 10:05:00'),
(2, 1, 3, NULL, '栈桥薄雾那张图好喜欢', '2025-12-18 10:12:00'),
(3, 2, 1, NULL, '古道穿越想去试试', '2025-12-17 21:10:00'),
(4, 3, 3, NULL, '越野初体验真不错', '2025-12-16 23:20:00')
ON DUPLICATE KEY UPDATE
`post_id`=VALUES(`post_id`), `user_id`=VALUES(`user_id`), `parent_id`=VALUES(`parent_id`), `content`=VALUES(`content`), `create_time`=VALUES(`create_time`);

INSERT INTO `comment_like` (`id`, `comment_id`, `user_id`, `create_time`)
VALUES
(1, 1, 1, '2025-12-18 10:06:00'),
(2, 2, 1, '2025-12-18 10:13:00'),
(3, 3, 2, '2025-12-17 21:12:00')
ON DUPLICATE KEY UPDATE
`comment_id`=VALUES(`comment_id`), `user_id`=VALUES(`user_id`), `create_time`=VALUES(`create_time`);

INSERT INTO `post_like` (`id`, `post_id`, `user_id`, `create_time`)
VALUES
(1, 1, 1, '2025-12-18 10:03:00'),
(2, 1, 2, '2025-12-18 10:04:00'),
(3, 2, 3, '2025-12-17 21:08:00'),
(4, 3, 1, '2025-12-16 23:05:00'),
(5, 5, 2, '2025-12-14 09:25:00')
ON DUPLICATE KEY UPDATE
`post_id`=VALUES(`post_id`), `user_id`=VALUES(`user_id`), `create_time`=VALUES(`create_time`);

INSERT INTO `equipment_rental` (`id`, `user_id`, `equipment_id`, `start_time`, `end_time`, `rental_days`, `daily_rental_price`, `deposit`, `total_price`, `payment_status`, `rental_status`, `contact_phone`, `create_time`)
VALUES
(1, 1, 1, '2026-01-09 09:00:00', '2026-01-11 18:00:00', 3, 35.0, 200.0, 305.0, '已支付', '进行中', '13800000001', '2026-01-08 12:00:00'),
(2, 2, 2, '2026-01-12 08:30:00', '2026-01-13 20:00:00', 2, 22.0, 120.0, 164.0, '未支付', '预订中', '13800000002', '2026-01-10 10:20:00'),
(3, 3, 4, '2026-01-15 18:00:00', '2026-01-16 22:00:00', 2, 6.0, 40.0, 52.0, '已支付', '已归还', '13800000003', '2026-01-14 16:40:00')
ON DUPLICATE KEY UPDATE
`user_id`=VALUES(`user_id`), `equipment_id`=VALUES(`equipment_id`), `start_time`=VALUES(`start_time`), `end_time`=VALUES(`end_time`), `rental_days`=VALUES(`rental_days`), `daily_rental_price`=VALUES(`daily_rental_price`), `deposit`=VALUES(`deposit`), `total_price`=VALUES(`total_price`), `payment_status`=VALUES(`payment_status`), `rental_status`=VALUES(`rental_status`), `contact_phone`=VALUES(`contact_phone`), `create_time`=VALUES(`create_time`);
