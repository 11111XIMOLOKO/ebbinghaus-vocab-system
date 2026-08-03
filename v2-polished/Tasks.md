# Tasks — 开发任务清单

---

## 阶段一：项目脚手架

- [x] T1 初始化 Spring Boot 项目（Maven、pom.xml、application.yml），跑通空项目
- [x] T2 初始化 Vue 3 项目（Vite、TypeScript），跑通前端空页面
- [x] T3 配置 MySQL 数据源 + MyBatis-Plus，写一个测试接口确认数据库连通
- [x] T4 配置 JWT 工具类（生成 token、校验 token、解析用户 ID）
- [x] T5 配置 Knife4j，确认 API 文档页面可访问

## 阶段二：用户认证

- [x] T6 设计 user 表，建表 SQL，生成 User entity + UserMapper
- [x] T6A 实现 AdminInitializer：系统启动时自动检查并创建 admin 账号（幂等，仅在不存在时创建）
- [x] T7 实现注册：AuthService.register() — 校验用户名唯一性、BCrypt 哈希密码、写入数据库
- [x] T8 实现注册接口：AuthController `POST /api/auth/register`
- [x] T9 实现登录：AuthService.login() — 验证密码、生成 JWT、返回 token + 用户信息
- [x] T10 实现登录接口：AuthController `POST /api/auth/login`
- [x] T11 实现登出接口：AuthController `POST /api/auth/logout` — 清除登录态
- [x] T12 实现当前用户信息接口：AuthController `GET /api/auth/me`
- [x] T13 实现 JWT 拦截器 — 校验 Authorization header，未登录返回 401，通过后将用户 ID 写入上下文
- [x] T14 前端：Login.vue + Register.vue，登录成功后 token 存 Pinia，路由守卫拦截未登录用户

## 阶段三：词库与单词

- [x] T15 设计 word_book 表 + word 表，建表 SQL
- [x] T16 生成 WordBook entity + Word entity，对应的 Mapper 接口
- [x] T17 实现词库查询 API（`GET /api/word-books` — 返回 7 个词库列表）
- [x] T17A 前端：WordBooks.vue — 词库浏览与选择页
- [x] T18 导入词库数据（7 个 SQL 脚本，覆盖初中到 SAT）
- [x] T19 管理员后台：词库增删改、单词增删改（`/api/admin/word-books`、`/api/admin/words`）

## 阶段四：学习流程

- [x] T20 设计 review_plan 表 + study_record 表，建表 SQL
- [x] T21 设计 study_plan 表，建表 SQL（daily_goal 为预留表，当前版本未使用）
- [x] T20A 实现学习任务生成：StudyService — 从用户当前词库随机抽取未学单词（数量从 study_plan 读取）
- [x] T20B 实现学习任务接口：`GET /api/study/overview`（首页概览统计）、`POST /api/study/new`（获取待学新词列表）
- [x] T20C 实现学习提交：StudyService — 记录熟悉度，认识(familiarity=3)和不认识(familiarity=1)均创建 review_plan(stage=0)
- [x] T20D 实现学习提交接口：`POST /api/study/submit`
- [x] T20E 前端：Study.vue — 单词卡片展示，用户点击"认识"或"不认识"提交结果
- [x] T25 实现学习计划管理：StudyPlanService — 用户可设置每日新词数、复习倍数
- [x] T26 实现学习计划接口：`GET /api/study-plan`、`PUT /api/study-plan`
- [x] T26A 前端：StudyPlan.vue — 学习计划配置页（新词数、复习倍数）
- [x] T26B 实现复习目标接口：`GET /api/schedule/goal`（获取）、`PUT /api/schedule/goal`（更新复习倍数）

## 阶段五：复习算法（核心）

- [x] T27 设计 review_log 表，建表 SQL
- [x] T28 实现复习任务查询：ScheduleService — 查当前用户所有 `next_review_time <= now` 的 review_plan 记录
- [x] T29 实现复习概览接口：`GET /api/schedule/overview`（到期词数、阶段分布）
- [x] T30 实现复习算法：认识(stage+1，最高7)、模糊(stage-1，不低于0)、不认识(stage重置为0)
- [x] T31 实现复习算法：根据 stage 查间隔表，计算 next_review_time，stage=7 且认识时 status=1
- [x] T32 实现复习提交：复用 `POST /api/study/submit`，写入 review_log 记录
- [x] T33 前端：Schedule.vue — 展示今日待复习词，用户提交复习结果

## 阶段六：错词 + 打卡 + 统计

- [x] T34 设计 wrong_word 表 + checkin_record 表，建表 SQL
- [x] T35 实现错词归集：学习/复习 result=不认识/模糊 → 写入 wrong_word 表
- [x] T36 实现错词查询接口：分页/标记掌握/删除
- [x] T37 实现每日签到：CheckinService
- [x] T38 实现签到接口：`POST /api/schedule/checkin`
- [x] T39 实现统计查询：总览+趋势+弱项分析
- [x] T40 实现统计接口：`GET /api/statistics/*`
- [x] T41 前端：Statistics.vue — KPI+ECharts 饼图+折线图
- [x] T42 前端：WrongWords.vue — 错词表格+分页

## 阶段七：后台管理

- [x] T43 设计 announcement 表 + operation_log 表，建表 SQL
- [x] T44 实现管理员角色校验拦截器（AdminAuthInterceptor），路由 `/api/admin/*` 需要 ADMIN 角色
- [x] T45 实现用户管理接口（`/api/admin/users` — 查看/禁用/删除）
- [x] T46 实现公告管理接口（CRUD）+ 用户端 `GET /api/announcements`
- [x] T47 实现操作日志：OperationLogInterceptor 自动记录
- [x] T48 实现操作日志查询：`GET /api/admin/operation-logs`
- [x] T49 前端：后台管理页面（用户+公告+日志）

## 阶段八：联调与测试

- [x] T50 前端：Dashboard.vue — 首页集成（任务面板、公告轮播、打卡天数、掌握单词数）
- [x] T51 核心链路联调：注册 → 登录 → 选词库 → 学新词 → 提交熟悉度 → 复习 → 统计查询，端到端走通
- [x] T52 修复联调中发现的接口字段不一致问题（前后端 DTO/VO 对齐）
- [x] T53 测试用例：登录/密码错误/空用户名/token 过期 → 全部通过
- [x] T54 测试用例：复习提交 → stage 更新 + review_log 写入 → 全部通过
- [x] T55 对照 Specs 验收标准逐条检查，确认全部功能通过

---

## 阶段九：界面风格统一

> 基于参考项目的设计系统，对全站进行视觉风格统一。

- [x] T-UI-1 移植全局设计系统（global.css — CSS 变量、Element Plus 组件覆盖、侧边栏/顶栏/认证页/卡片布局）
- [x] T-UI-2 升级 App.vue 为侧边栏 + 顶栏 + 内容区布局，路由加 meta.title
- [x] T-UI-3 改造 Dashboard、Login、Register 页面（新卡片风格、公告轮播、auth 页）
- [x] T-UI-4 改造 Study、Schedule、WordBooks、StudyPlan 页面（去页面 header、统一样式）
- [x] T-UI-5 改造 Statistics、WrongWords、Admin 页面（混搭图表、环形饼图、仪表盘）

---

> 🎉 全部 60 个任务已完成。
