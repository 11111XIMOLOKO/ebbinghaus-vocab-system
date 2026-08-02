# Design — 系统设计方案

> 本文档描述系统**怎么做的**。前置文档：Specs.md。

---

## 1. 技术选型

| 层 | 选型 | 为什么选它 | 考虑过的替代方案 |
|---|---|---|---|
| 后端框架 | Spring Boot 3.3.5 | Java 课程教学主力框架，生态成熟 | — |
| ORM | MyBatis-Plus 3.5.7 | 比 JPA 更灵活——复习算法的复杂查询用 MyBatis XML 写更直观 | Spring Data JPA：自动生成 SQL 但对复杂查询不够透明 |
| 前端框架 | Vue 3 + TypeScript | 课程教学主力，响应式数据绑定适合表单交互多的场景 | React：学习曲线更陡 |
| UI 组件库 | Element Plus | Vue 3 生态最成熟，表格/表单/弹窗开箱即用 | — |
| 图表库 | ECharts | 百度开源，统计面板的趋势图和饼图用 | — |
| 数据库 | MySQL 8.0 | 关系型数据（用户-词库-复习记录），需要事务支持 | — |
| 缓存 | Redis | JWT token 黑名单、会话缓存 | 可选——开发环境可降级运行，不影响核心功能 |
| 认证 | JWT (HMAC-SHA256) | 无状态，适合前后端分离架构 | Session：需要服务端存储，不利于横向扩展 |
| API 文档 | Knife4j 4.5.0 | Swagger 的增强版，自动生成接口文档 | — |

---

## 2. 系统架构

### 2.1 总体架构

```
┌──────────────────────────────────────┐
│            Client (Vue 3 SPA)         │
│  Vue Router → Views → Axios → Pinia   │
└──────────────┬───────────────────────┘
               │ HTTP REST JSON
               │ Authorization: Bearer <JWT>
┌──────────────┴───────────────────────┐
│         Server (Spring Boot)          │
│                                       │
│  Interceptor (JWT校验 / 管理员校验)     │
│       ↓                               │
│  Controller (REST 控制器)              │
│       ↓                               │
│  Service (业务逻辑层)                   │
│       ↓                               │
│  Mapper (MyBatis-Plus)                 │
└──────────────┬───────────────────────┘
               │ JDBC
┌──────────────┴───────────────────────┐
│       MySQL 8.0    │    Redis         │
│   (持久化存储)       │  (JWT/会话缓存)   │
└──────────────────────────────────────┘
```

### 2.2 前端架构

```
src/
├── api/           ← axios 封装，统一拦截器自动带 token
├── router/        ← Vue Router，登录/未登录路由分离
├── stores/        ← Pinia：user token、当前词库、学习进度
├── views/         ← 页面组件
└── styles/        ← 全局 CSS
```

**关键设计决策：**
- **Axios 请求拦截器**统一在每次请求前从 Pinia 读取 token 并加到 Authorization header。不需要每个页面手动处理。
- **路由守卫**在 `router/index.ts` 里：未登录用户访问任何需要认证的页面 → 自动跳转登录页。
- **Pinia 持久化**：token 存 localStorage，刷新页面不丢失登录态。

### 2.3 后端架构

```
server/src/main/java/com/ebbinghaus/vocab/
├── config/        ← 拦截器注册（JWT校验、管理员校验、操作日志）
├── controller/    ← REST 控制器，只做参数校验和调用 Service
├── service/       ← 业务逻辑，核心逻辑在这里
├── mapper/        ← MyBatis-Plus Mapper 接口
├── domain/
│   ├── entity/    ← 数据库实体
│   ├── dto/       ← 请求体（RegisterRequest 等）
│   └── vo/        ← 响应体（LoginVO、StatisticsOverviewVO 等）
├── exception/     ← 全局异常处理（@ControllerAdvice）
└── util/          ← JwtUtil（生成/校验 token）
```

**分层原则：**
- Controller 不写业务逻辑——只校验输入、调 Service、返回结果
- Service 是业务逻辑的唯一位置——复习算法、打卡统计、错词归集都在这里
- Mapper 只做数据库操作——MyBatis-Plus 自动生成基础 CRUD，复杂查询手写

---

## 3. 数据库设计（核心表）

### 3.1 user（用户表）

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 主键 |
| username | VARCHAR(50) UNIQUE | 用户名 |
| password | VARCHAR(255) | BCrypt 密码哈希 |
| role | VARCHAR(20) | USER / ADMIN |
| status | TINYINT | 0=禁用 1=正常 |
| created_at | DATETIME | 注册时间 |

### 3.2 word（单词表）

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 主键 |
| word_book_id | BIGINT FK | 所属词库 |
| english | VARCHAR(255) | 英文单词 |
| chinese | VARCHAR(255) | 中文释义 |

### 3.3 review_plan（复习计划表）— 核心表

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 主键 |
| user_id | BIGINT FK | 所属用户 |
| word_id | BIGINT FK | 复习的单词 |
| stage | INT | 当前复习阶段（0-7，7=已掌握） |
| status | INT | 复习状态（0=待复习 1=已完成） |
| familiarity | INT | 最近一次熟悉度（1=不认识 2=模糊 3=认识） |
| next_review_time | DATETIME | 下次复习时间 |
| last_review_time | DATETIME | 最近一次复习时间 |
| first_study_time | DATETIME | 首次学习时间 |

**复习算法逻辑：**
1. 用户提交熟悉度 → Service 查 review_plan 当前 stage
2. 根据 familiarity 计算下一阶段（认识→stage+1 / 模糊→stage-1不低于0 / 不认识→stage重置为0）
3. 更新 next_review_time = now + intervalMinutes[stage]
4. 插入 review_log 记录
5. 判断是否已掌握：stage >= 7 且最近一次 familiarity = 认识

**familiarity 映射：** 1=不认识、2=模糊、3=认识。学习阶段只产生 1 和 3——"不认识"映射为 1，"认识"映射为 3，学习阶段不产生 2（模糊）。进入复习周期后，三个取值均可产生。

**设计理由：学习 vs 复习的判断差异。** 学习阶段使用二元判断（认识/不认识）——首次接触一个词，用户只需确认"记住了吗"，二元足够且减少决策负担。进入复习周期后切换为三元判断（认识/模糊/不认识）——增加中间状态"模糊"能更准确地捕捉记忆程度。

### 3.4 review_log（复习记录表）

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 主键 |
| user_id | BIGINT FK | 用户 |
| word_id | BIGINT FK | 单词 |
| stage_before | INT | 复习前所处轮次 |
| result | VARCHAR(20) | KNOWN / FUZZY / UNKNOWN |
| reviewed_at | DATETIME | 复习时间 |

### 3.5 study_record（学习记录表）

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 主键 |
| user_id | BIGINT FK | 所属用户 |
| study_date | DATE | 学习日期 |
| new_word_count | INT | 当日新学词数 |
| review_word_count | INT | 当日复习词数 |
| mastered_word_count | INT | 当日掌握词数 |
| wrong_word_count | INT | 当日错词数 |
| study_duration | INT | 当日学习时长（分钟） |

### 3.6 word_book（词库表）

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 主键 |
| user_id | BIGINT FK | 创建者（系统词库为 NULL） |
| name | VARCHAR(100) | 词库名称（初中/高中/四级/六级/考研/托福/SAT） |
| category | VARCHAR(50) | 分类 |
| description | VARCHAR(255) | 词库描述 |
| word_count | INT | 词库包含的单词数量 |
| type | INT | 0=系统词库 1=用户自建 |
| status | INT | 0=禁用 1=启用 |

### 3.7 study_plan（学习计划表）

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 主键 |
| user_id | BIGINT FK | 所属用户 |
| book_id | BIGINT FK | 当前学习的词库 |
| plan_word_count | INT | 计划每日学习新词数 |
| review_multiplier | INT | 复习倍数（每日复习数 = 新词数 × 倍数，默认 1） |
| daily_review_count | INT | 每日复习词数（计算值：plan_word_count × review_multiplier） |
| daily_total_count | INT | 每日总任务数（新词数 + 复习数） |

### 3.8 daily_goal（每日目标表，预留）

> 此表在当前版本中未实际使用，属于为后续版本预留的扩展。当前学习目标通过 study_plan 表管理。

### 3.9 checkin_record（签到表）

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 主键 |
| user_id | BIGINT FK | 所属用户 |
| checkin_date | DATE | 签到日期 |
| study_duration | INT | 当日学习时长（分钟） |
| completed_target | INT | 是否完成当日目标（0/1） |

### 3.10 wrong_word（错词表）

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 主键 |
| user_id | BIGINT FK | 所属用户 |
| word_id | BIGINT FK | 错误单词 |
| wrong_count | INT | 累计错误次数 |
| status | INT | 0=待复习 1=已掌握 |

### 3.11 announcement（公告表）

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 主键 |
| title | VARCHAR(255) | 公告标题 |
| content | TEXT | 公告内容 |
| type | VARCHAR(50) | 公告类型 |
| status | INT | 0=草稿 1=已发布 |
| create_by | BIGINT | 发布者 ID |
| create_time | DATETIME | 发布时间 |

### 3.12 operation_log（操作日志表）

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 主键 |
| user_id | BIGINT FK | 操作者 |
| module | VARCHAR(100) | 操作模块 |
| operation | VARCHAR(100) | 操作类型 |
| request_uri | VARCHAR(255) | 请求路径 |
| request_method | VARCHAR(10) | GET/POST/PUT/DELETE |
| ip | VARCHAR(50) | 操作者 IP |
| create_time | DATETIME | 操作时间 |

---

## 4. API 设计（核心接口）

### 4.1 认证

| 方法 | 路径 | 说明 | 认证 |
|---|---|---|---|
| POST | `/api/auth/register` | 注册 | 无 |
| POST | `/api/auth/login` | 登录，返回 JWT | 无 |
| GET | `/api/auth/me` | 当前用户信息 | JWT |
| POST | `/api/auth/logout` | 登出，使当前 token 失效 | JWT |

### 4.2 学习

| 方法 | 路径 | 说明 | 认证 |
|---|---|---|---|
| GET | `/api/word-books` | 获取词库列表 | JWT |
| GET | `/api/study/overview` | 获取学习首页概览（今日任务数、打卡状态、统计摘要） | JWT |
| GET | `/api/study/today` | 获取今日任务（新词 + 到期复习词，混合返回） | JWT |
| POST | `/api/study/new` | 获取待学新词列表（从当前词库随机抽取） | JWT |
| POST | `/api/study/submit` | 提交单个词的学习结果 | JWT |

> 选词库通过更新 study_plan.book_id 实现（见 §4.5），非独立接口。

### 4.3 复习与日程

| 方法 | 路径 | 说明 | 认证 |
|---|---|---|---|
| GET | `/api/schedule/overview` | 获取复习概览（到期词数、阶段分布、今日进度） | JWT |
| GET | `/api/schedule/goal` | 获取当前复习目标设置 | JWT |
| PUT | `/api/schedule/goal` | 更新复习目标（复习倍数） | JWT |
| GET | `/api/schedule/calendar` | 获取打卡日历数据 | JWT |

> 复习提交复用学习提交接口 `POST /api/study/submit`（见 §4.2），通过 familiarity 取值区分学习阶段和复习阶段。

### 4.4 打卡签到

| 方法 | 路径 | 说明 | 认证 |
|---|---|---|---|
| POST | `/api/schedule/checkin` | 每日签到 | JWT |

### 4.5 学习计划

| 方法 | 路径 | 说明 | 认证 |
|---|---|---|---|
| GET | `/api/study-plan` | 获取当前学习计划 | JWT |
| PUT | `/api/study-plan` | 更新学习计划（新词数、复习倍数、词库） | JWT |

> 选词库通过更新 study_plan.book_id 实现。

### 4.6 统计

| 方法 | 路径 | 说明 | 认证 |
|---|---|---|---|
| GET | `/api/statistics/overview` | 学习总览（天数、掌握数、各轮次分布） | JWT |
| GET | `/api/statistics/trend` | 每日学习趋势（折线图数据） | JWT |
| GET | `/api/statistics/weak-analysis` | 薄弱词分析 | JWT |

### 4.7 错词本

| 方法 | 路径 | 说明 | 认证 |
|---|---|---|---|
| GET | `/api/wrong-words/page` | 分页获取错词列表 | JWT |
| GET | `/api/wrong-words/export` | 导出错词列表 | JWT |
| GET | `/api/wrong-words/reinforce` | 获取待强化错词 | JWT |
| POST | `/api/wrong-words/{id}/mastered` | 标记错词已掌握 | JWT |
| DELETE | `/api/wrong-words/{id}` | 删除错词记录 | JWT |
| PUT | `/api/wrong-words/{id}` | 更新错词状态 | JWT |

### 4.8 公告

| 方法 | 路径 | 说明 | 认证 |
|---|---|---|---|
| GET | `/api/announcements` | 获取公告列表（首页轮播用） | JWT |

### 4.9 后台管理

| 方法 | 路径 | 说明 | 认证 |
|---|---|---|---|
| GET/POST/PUT/DELETE | `/api/admin/users` | 用户管理 | ADMIN |
| GET/POST/PUT/DELETE | `/api/admin/words` | 单词管理 | ADMIN |
| GET/POST/PUT/DELETE | `/api/admin/word-books` | 词库管理 | ADMIN |
| GET/POST/PUT/DELETE | `/api/admin/announcements` | 公告管理 | ADMIN |
| GET | `/api/admin/operation-logs` | 操作日志查看 | ADMIN |

- 全部接口需要 JWT 认证
- 管理类接口（`/api/admin/*`）额外需要 AdminAuthInterceptor 校验管理员角色

---

## 5. 关键设计决策

### 5.1 为什么复习算法放在 Service 层而不是数据库存储过程

复习逻辑会变化（比如调整间隔时间），放在 Java 代码里比放在数据库存过里更容易修改和测试。

### 5.2 为什么 Redis 是可选的

开发环境可以直接用内存存储 token，不需要启动 Redis。核心功能（学习、复习、统计）不依赖缓存。Redis 只在需要分布式部署或多实例负载均衡时启用。

### 5.3 为什么用 MyBatis-Plus 而不是 JPA

复习计划的状态更新（stage 的加减、next_review_time 的计算）涉及较复杂的条件查询。MyBatis 的 XML 写 SQL 比 JPA 的 `@Query` 注解更灵活、更透明。

---

---

> 下一步：Tasks.md 描述系统的历史开发任务清单。
