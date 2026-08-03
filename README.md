# 艾宾浩斯单词背诵系统

> 这不是一个"我做了个单词 App"的项目。这是一个关于 **AI 时代怎么开发软件** 的对比实验。

## 实验设计

同一套 SDD 文档（人主导设计，AI 辅助产出），两次开发。唯一变量：**开发过程中人有没有参与迭代**。

| | v1（`v1-sdd-only/`） | v2（`v2-polished/`） |
|---|---|---|
| **开发方式** | AI 按文档独立完成，人不碰代码 | AI 写 → 人测 → 人反馈 → AI 改 → 循环 |
| **任务数** | 55 项 | 60 项（含 UI 风格统一） |
| **迭代轮次** | 0（一次通过） | 每项 Task 平均 2-3 轮 |
| **Bug 修复** | — | 14 项 |
| **体验优化** | — | 11 项 |
| **词库数据** | 159 个示例词 | 54,356 个真实词 |

**一样的 Specs。一样的 Design。结果天差地别。**

## 快速对比

| | v1（纯 SDD） | v2（人机协作） |
|---|---|---|
| 首页 | ![v1](docs/screenshots/v1-dashboard.png) | ![v2](docs/screenshots/v2-dashboard.png) |
| 词库 | ![v1](docs/screenshots/v1-wordbooks.png) | ![v2](docs/screenshots/v2-wordbooks.png) |
| 复习 | ![v1](docs/screenshots/v1-review.png) | ![v2](docs/screenshots/v2-review.png) |
| 统计 | ![v1](docs/screenshots/v1-statistics.png) | ![v2](docs/screenshots/v2-statistics.png) |

## 不是"AI 不行"——是"只有文档不够"

v1 跑起来之后，功能都在——Specs 的验收标准全部通过。但交互粗糙、缺乏反馈、体验割裂。

v2 修复了 14 个 Bug、完成了 11 项体验优化。差的那口气不是高深技术——是坐在浏览器前真的用了一遍，发现哪里不对，然后告诉 AI 怎么改。**SDD 保证功能正确。人机协作保证可用的体验。**

## 两个版本均可独立运行

各目录内 README 包含完整启动步骤。

```
git clone https://github.com/11111XIMOLOKO/ebbinghaus-vocab-system.git
cd ebbinghaus-vocab-system/v1-sdd-only   # 或 v2-polished
```

## SDD 文档

[`sdd/`](./sdd/) 目录包含完整设计产出——Specs + Design + Tasks + CLAUDE.md。所有文档与代码实际行为完全对齐。

## 我学到了什么

**写清楚需求本身就是一种能力。** 大多数人跟 AI 说"帮我做一个 XX"，你把需求拆成了 60 项任务。

**AI 不是自动驾驶，是副驾驶。** 架构你定、验收标准你给、每一轮迭代的体验你判断。

**这套方法论迁移到了科研。** 同样的 SDD 方法 + 人机协作节奏，正在用于扩散模型工业缺陷检测 Pipeline。

## 仓库结构

```
├── README.md
├── sdd/                        # SDD 文档（Specs/Design/Tasks/CLAUDE/STATUS）
├── v1-sdd-only/                # 对照组：纯 SDD 生成
├── v2-polished/                # 实验组：人机协作精修
└── docs/screenshots/           # 对比截图
```
