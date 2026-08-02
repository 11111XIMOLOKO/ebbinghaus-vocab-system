# Tasks — 开发任务清单

---

## 阶段一：项目脚手架

- [ ] T1 初始化 Spring Boot 项目（Maven、pom.xml、application.yml），跑通空项目
- [ ] T2 初始化 Vue 3 项目（Vite、TypeScript），跑通前端空页面
- [ ] T3 配置 MySQL 数据源 + MyBatis-Plus，写一个测试接口确认数据库连通
- [ ] T4 配置 JWT 工具类（生成 token、校验 token、解析用户 ID）
- [ ] T5 配置 Knife4j，确认 API 文档页面可访问

## 阶段二：用户认证

- [ ] T6 设计 user 表，建表 SQL，生成 User entity + UserMapper
- [ ] T6A 实现 AdminInitializer：系统启动时自动检查并创建 admin 账号（幂等，仅在不存在时创建）
- [ ] T7 实现注册：AuthService.register() — 校验用户名唯一性、BCrypt 哈希密码、写入数据库
- [ ] T8 实现注册接口：AuthController `POST /api/auth/register`
- [ ] T9 实现登录：AuthService.login() — 验证密码、生成 JWT、返回 token + 用户信息
- [ ] T10 实现登录接口：AuthController `POST /api/auth/login`
- [ ] T11 实现登出接口：AuthController `POST /api/auth/logout` — 清除登录态
- [ ] T12 实现当前用户信息接口：AuthController `GET /api/auth/me`
- [ ] T13 实现 JWT 拦截器 — 校验 Authorization header，未登录返回 401，通过后将用户 ID 写入上下文
- [ ] T14 前端：Login.vue + Register.vue，登录成功后 token 存 Pinia，路由守卫拦截未登录用户
## 阶段三：词库与单词

- [ ] T15 设计 word_book 表 + word 表，建表 SQL
- [ ] T16 生成 WordBook entity + Word entity，对应的 Mapper 接口
- [ ] T17 实现词库查询 API（`GET /api/word-books` — 返回 7 个词库列表）
- [ ] T17A 前端：WordBooks.vue — 词库浏览与选择页
- [ ] T18 导入词库数据（7 个 SQL 脚本，覆盖初中到 SAT）
- [ ] T19 管理员后台：词库增删改、单词增删改（`/api/admin/word-books`、`/api/admin/words`）

## 阶段四：学习流程

- [ ] T20 设计 review_plan 表 + study_record 表，建表 SQL
- [ ] T21 设计 study_plan 表，建表 SQL（daily_goal 为预留表，当前版本未使用）
- [ ] T20A 实现学习任务生成：StudyService — 从用户当前词库随机抽取未学单词（数量从 study_plan 读取）
- [ ] T20B 实现学习任务接口：`GET /api/study/today`（新词+复习词混合返回）和 `GET /api/study/overview`（首页概览统计）、`POST /api/study/new`（获取待学新词列表）
- [ ] T20C 实现学习提交：StudyService — 记录熟悉度，认识(familiarity=3)和不认识(familiarity=1)均创建 review_plan(stage=0)
- [ ] T20D 实现学习提交接口：`POST /api/study/submit`
- [ ] T20E 前端：Study.vue — 单词卡片展示，用户点击"认识"或"不认识"提交结果
- [ ] T25 实现学习计划管理：StudyPlanService — 用户可设置每日新词数、复习倍数
- [ ] T26 实现学习计划接口：`GET /api/study-plan`、`PUT /api/study-plan`
- [ ] T26A 前端：StudyPlan.vue — 学习计划配置页（新词数、复习倍数）
- [ ] T26B 实现复习目标接口：`GET /api/schedule/goal`（获取）、`PUT /api/schedule/goal`（更新复习倍数）

## 阶段五：复习算法（核心）

- [ ] T27 设计 review_log 表，建表 SQL
- [ ] T28 实现复习任务查询：ScheduleService — 查当前用户所有 `next_review_time <= now` 的 review_plan 记录
- [ ] T29 实现复习概览接口：`GET /api/schedule/overview` 和 `GET /api/schedule/calendar`（打卡日历）
- [ ] T30 实现复习算法：ReviewScheduleService.updateStage() — 认识(stage+1，最高7)、模糊(stage-1，不低于0)、不认识(stage重置为0)
- [ ] T31 实现复习算法：ReviewScheduleService.calcNextReview() — 根据 stage 查间隔表，计算 `next_review_time = now + intervalMinutes[stage]`，stage=7 且认识时 status 设为 1（已掌握）
- [ ] T32 实现复习提交：复用 `POST /api/study/submit` — 更新 review_plan，写入 review_log 记录
- [ ] T33 前端：Schedule.vue — 展示今日待复习词，用户提交复习结果

## 阶段六：错词 + 打卡 + 统计

- [ ] T34 设计 wrong_word 表 + checkin_record 表，建表 SQL
- [ ] T35 实现错词归集：学习 result=不认识，或复习 result=不认识/模糊 → WrongWordService 写入或更新 wrong_word 表
- [ ] T36 实现错词查询接口：`GET /api/wrong-words/page`（分页）、`GET /api/wrong-words/reinforce`（待强化）、`GET /api/wrong-words/export`（导出）、`POST /api/wrong-words/{id}/mastered`（标记掌握）、`DELETE /api/wrong-words/{id}`（删除）
- [ ] T37 实现每日签到：CheckinService — 用户在打卡页面手动签到
- [ ] T38 实现签到接口：`POST /api/schedule/checkin`
- [ ] T39 实现统计查询：StatisticsService — 学习总览（天数、掌握数、各轮次分布）、每日趋势（折线图数据）
- [ ] T40 实现统计接口：`GET /api/statistics/overview`、`GET /api/statistics/trend`、`GET /api/statistics/weak-analysis`（薄弱词分析）
- [ ] T41 前端：Statistics.vue — ECharts 可视化（趋势图、轮次饼图、掌握率仪表盘）
- [ ] T42 前端：WrongWords.vue — 错词列表，支持重新学习

## 阶段七：后台管理

- [ ] T43 设计 announcement 表 + operation_log 表，建表 SQL
- [ ] T44 实现管理员角色校验拦截器（AdminAuthInterceptor），路由 `/api/admin/*` 需要 ADMIN 角色
- [ ] T45 实现用户管理接口（`/api/admin/users` — 查看/禁用/删除）
- [ ] T46 实现公告管理接口（`/api/admin/announcements` — CRUD）+ 用户端公告获取接口（`GET /api/announcements`）
- [ ] T47 实现操作日志：OperationLogInterceptor — 自动记录管理员的每次操作
- [ ] T48 实现操作日志查询接口：`GET /api/admin/operation-logs`
- [ ] T49 前端：后台管理页面（AdminDashboard / UserManage / WordManage / AnnouncementManage / OperationLogs）

## 阶段八：联调与测试

- [ ] T50 前端：Dashboard.vue — 首页集成（任务面板、公告轮播、打卡天数、掌握单词数）
- [ ] T51 核心链路联调：注册 → 登录 → 选词库 → 学新词 → 提交熟悉度 → 复习 → 统计查询，端到端走通
- [ ] T52 修复联调中发现的接口字段不一致问题（前后端 DTO/VO 对齐）
- [ ] T53 测试用例：正确登录成功 / 错误密码返回错误提示 / 空用户名登录失败 / token 过期返回 401
- [ ] T54 测试用例：复习结果提交后 review_plan.stage 正确更新 / review_log 正确写入
- [ ] T55 对照 Specs 验收标准逐条检查，确认全部功能通过