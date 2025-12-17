## 概述

* 目标：基于 Spring Boot + MyBatis + MySQL（后端）与 Vue3 + Element Plus + Axios + Vue Router（前端）实现用户/路线/活动三大模块的最简 Demo。

* 原则：功能优先、结构清晰、必要注释、可启动可验证、无移动端与微服务。

## 后端设计

* 技术栈：Java 8、Spring Boot 2.7.x、MyBatis、MySQL、spring-security-crypto（仅用于密码加密，不启用 Spring Security 过滤器）。

* 项目结构：`springboot-demo`

  * `controller`：UserController、RouteController、ActivityController

  * `service`：UserService、RouteService、ActivityService

  * `mapper`：UserMapper、RouteMapper、ActivityMapper、ActivityUserMapper（接口）

  * `entity`：User、Route、Activity、ActivityUser（POJO）

  * `resources/mapper`：对应 XML 映射文件

  * `resources/application.yml`：数据源、MyBatis 配置、端口、CORS

* 会话与认证：

  * 登录成功后，将 `userId` 写入 `HttpSession`；`/api/user/info` 从 Session 读取。

  * 前端 Axios 开启 `withCredentials`，后端开启 CORS（允许凭证）。

* 密码安全：注册时使用 BCrypt 哈希存储；登录时比对哈希。

* 业务逻辑：

  * 用户：注册（查重、入库）、登录（校验）、用户信息（基于 Session）。

  * 路线：列表分页（简化为全量）、创建（记录 `creator_id`）、详情查询。

  * 活动：创建（基于路线）、列表（返回当前报名人数）、报名（人数上限校验+幂等）。

数据库设计\
账号 root\
密码 qq2027681066
---------------

* 表：

  * `user(id, username, password, create_time)`

  * `route(id, name, distance, level, description, creator_id)`

  * `activity(id, name, route_id, time, max_people)`

  * `activity_user(id, activity_id, user_id)`（当前报名人数通过计数该表获取）

* 索引：`user.username` 唯一索引；`activity_user(activity_id, user_id)` 唯一索引防重复报名。

## 接口设计

* 用户模块：

  * `POST /api/user/register` 请求：`{username, password}`；响应：注册成功/失败

  * `POST /api/user/login` 请求：`{username, password}`；响应：登录成功并写 Session

  * `GET /api/user/info` 响应：`{id, username, createTime}`（未登录返回 401）

* 路线模块：

  * `GET /api/route/list` 响应：路线数组（含基本字段）

  * `POST /api/route/create` 请求：`{name, distance, level, description}`；响应：创建结果与 ID

  * `GET /api/route/{id}` 响应：路线详情

* 活动模块：

  * `POST /api/activity/create` 请求：`{name, routeId, time, maxPeople}`

  * `GET /api/activity/list` 响应：活动数组（含 `currentPeople` 由 `activity_user` 计数）

  * `POST /api/activity/join` 请求：`{activityId}`；响应：报名成功/失败（人数满/已报名）

## 前端设计

* 技术栈：Vite + Vue 3 + Element Plus + Axios + Vue Router。

* 项目结构：`vue-demo`

  * `main.js`：注册 Element Plus、Router、Axios 全局实例

  * `router/index.js`：路由：登录、路线列表、创建路线、活动列表

  * `api/`：`user.js`、`route.js`、`activity.js`（封装 Axios 请求，`withCredentials: true`）

  * `views/`：`Login.vue`、`RouteList.vue`、`CreateRoute.vue`、`ActivityList.vue`

  * `components/`：必要的表单/列表子组件（如简化表单直接在 views 内实现）

* 交互流程：

  * 登录页：提交后写入 Session；跳转路线列表。

  * 路线列表页：展示全部路线；进入详情（可选简化为弹窗）。

  * 创建路线页：表单提交创建。

  * 活动列表页：展示活动与当前人数；报名按钮触发加入。

## 文件与内容清单（将生成的主要文件）

* 后端：

  * `pom.xml`（Spring Boot、MyBatis、mysql-connector-j、spring-security-crypto）

  * `src/main/java/.../DemoApplication.java`

  * `controller/UserController.java`、`RouteController.java`、`ActivityController.java`

  * `service/*Service.java` + `impl/*ServiceImpl.java`

  * `mapper/*Mapper.java` + `resources/mapper/*Mapper.xml`

  * `entity/User.java`、`Route.java`、`Activity.java`、`ActivityUser.java`

  * `config/CorsConfig.java`（允许跨域携带凭证）

  * `resources/application.yml`

* 前端：

  * `package.json`（vite、vue、element-plus、axios、vue-router）

  * `vite.config.js`（可选 devServer 代理）

  * `src/main.js`、`src/router/index.js`

  * `src/api/user.js`、`src/api/route.js`、`src/api/activity.js`

  * `src/views/Login.vue`、`RouteList.vue`、`CreateRoute.vue`、`ActivityList.vue`

* SQL：`db.sql`（建库与建表、索引、基础数据可选）

## 启动与验证

* 步骤：

  1. 创建数据库并执行 `db.sql`
  2. 修改后端 `application.yml` 数据源（host、db、user、password）
  3. 启动后端：`mvn spring-boot:run`（端口 `8080`）
  4. 启动前端：`npm install && npm run dev`（端口 `5173`）
  5. 访问前端登录页，完成登录、创建路线、查看路线列表、创建活动、报名活动验证接口链路。

## 验证与注释

* 提供最小自测：注册/登录流程、跨域与 Session、报名人数上限与去重。

* 代码包含必要注释（控制器、服务、Mapper 方法与 SQL）。

## 交付

* 在确认后，将一次性生成：完整后端代码、完整前端代码、完整 SQL、启动说明，并贴出所有文件内容。

