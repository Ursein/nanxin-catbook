# 待办事项

> 文档中描述但未完全实现的功能清单

## 🔴 F8 行为日志（完全未实现）

**文档描述：** 自动记录用户浏览、搜索、点赞、评分、关注等行为，为推荐算法提供热度数据源。

**现状：**
- `LogService` 已编写（含 `record()` 方法）
- `ActivityLogRepository` 存在
- `activity_log` 表结构完整
- **但 LogService 从未被任何 Controller/Service 调用**
- **前端无任何行为埋点**

**修复方向：** 在 Controller/Service 的关键操作处调用 `LogService.record()`；或添加 AOP 切面统一拦截。

---

## 🔴 F3 毛色分类浏览（前端缺失）

**文档描述：** 按毛色分类（狸花/奶牛/橘猫/纯色/玳瑁及三花等）聚合展示猫咪。

**现状：**
- 数据库 `cat.colour_tags` 字段存在
- 后端支持 `colourTags` 关键词搜索
- 但前端 **Search.vue** 无任何毛色分类 UI（无颜色选择器、分类 Tab 等）

---

## 🟡 F5 照片审核流程（不完整）

**文档描述：** 用户上传照片后状态为 PENDING（待审核），管理员审核通过后变更为 APPROVED。

**现状：**
- 后端 `PhotoService.approve()` / `reject()` 方法存在
- API 端点 `PUT /api/v1/photos/{id}/approve` 和 `reject` 存在
- 但 `PhotoService.upload()` **直接设为 APPROVED**，跳过 PENDING
- 前端 **无照片审核管理页面**

---

## 🟡 管理员功能（无专用后台）

**文档描述：** ADMIN 拥有所有管理权限。

**现状：**
- 启动时自动创建 admin/admin123 账号
- 管理员可在猫咪详情页编辑/删除猫咪和评论
- **但无专用管理后台**（如照片审核队列、用户管理、系统设置等页面）

---

## 🟡 猫咪关系管理（前端缺失）

**文档描述：** 支持记录猫咪的父母/好友关系，在详情页展示关系图谱。

**现状：**
- 数据库 `father_id`、`mother_id`（外键）、`friend_ids`（VARCHAR）字段存在
- 后端 `Cat` 实体有对应字段
- `CatService` 可读写 `fatherId`/`motherId`
- 但 `friend_ids` 在 Service 中被忽略（`applyRequest()` 未设置）
- 前端 **CatDetail.vue** 不展示父母/好友信息
- 前端 **AddCat.vue / EditCat.vue** 无父母/好友输入项