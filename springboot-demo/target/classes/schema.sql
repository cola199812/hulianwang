-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `create_time` DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `post` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `title` VARCHAR(255) NULL,
  `markdown` TEXT NULL,
  `location_name` VARCHAR(255) NULL,
  `lat` DOUBLE NULL,
  `lng` DOUBLE NULL,
  `cover_url` VARCHAR(255) NULL,
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
