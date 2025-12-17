-- 数据库：outdoor_demo
CREATE DATABASE IF NOT EXISTS outdoor_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE outdoor_demo;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  create_time DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE UNIQUE INDEX idx_user_username ON user(username);
CREATE UNIQUE INDEX idx_user_email ON user(email);

-- 路线表
CREATE TABLE IF NOT EXISTS route (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  distance DOUBLE NOT NULL,
  level VARCHAR(20) NOT NULL,
  description TEXT,
  creator_id BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX idx_route_creator ON route(creator_id);

-- 活动表
CREATE TABLE IF NOT EXISTS activity (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  route_id BIGINT NOT NULL,
  time DATETIME NOT NULL,
  max_people INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX idx_activity_route ON activity(route_id);

-- 活动成员表
CREATE TABLE IF NOT EXISTS activity_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  activity_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE UNIQUE INDEX idx_activity_user_unique ON activity_user(activity_id, user_id);
CREATE INDEX idx_activity_user_activity ON activity_user(activity_id);

