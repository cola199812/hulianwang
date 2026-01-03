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
