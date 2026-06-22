# 南信猫友记（NanXin CatBook）

校园流浪猫信息管理与推荐系统 — 软件工程课程项目

---

## 项目概况

| 项目 | 内容 |
|------|------|
| 项目名称 | 南信猫友记（NanXin CatBook） |
| 课程 | 软件工程（程勇老师） |
| 学校 | 南京信息工程大学 软件学院 |
| 技术栈 | 后端 Spring Boot 3.4.5 + Java 17，前端 Vue 3 + Vite |
| 数据库 | MySQL 8，库名 `nanxin_maopu_v2` |
| 核心算法 | 余弦相似度 + 热度因子融合推荐 |

---

## 功能列表

1. **用户注册登录** — 注册、登录、退出；三级角色权限（USER / VERIFIED / ADMIN）
2. **猫咪档案管理** — 添加/编辑/删除猫咪；记录亲缘关系；管理猫咪状态
3. **猫咪搜索筛选** — 列表分页；状态 Tab 分类；毛色分类；关键词搜索；条件筛选
4. **相似度推荐** — 特征提取 → One-Hot 编码 → 余弦相似度 → 热度因子融合 → Top-N 推荐
5. **留言评论** — 发表/查看/删除留言
6. **照片管理** — 上传、审核（PENDING→APPROVED/REJECTED）、删除；照片点赞
7. **关注猫咪** — 关注/取消关注；已关注列表
8. **猫友评分** — 1-5 星评分；统计平均分与评分人数
9. **行为日志** — 自动记录浏览、搜索、点赞、评分、关注等行为

---

## 课程进度

| 内容 | 产出 | 状态 |
|:----:|------|:----:|
| 内容一：需求分析 | `docs/需求分析/需求规格说明书.md` + 3张DFD图 | ✅ |
| 内容二：概要设计 | `docs/概要设计/概要设计说明书.md` | ✅ |
| 内容三：详细设计 | `docs/详细设计/详细设计说明书.md` + 算法伪代码 + 界面设计 | ✅ |
| 编码实现 | Spring Boot 后端 + Vue 3 前端 + 推荐算法 | ✅ |
| 内容四：软件测试 | `docs/测试/测试报告.md`（44个用例，100%通过） | ✅ |

---

## 项目结构

```
nanxin-catbook/
├── backend/                        # Spring Boot 后端
│   ├── src/main/java/com/nanxin/catbook/
│   │   ├── config/                 # JWT、CORS、异常处理
│   │   ├── controller/             # 6个REST控制器
│   │   ├── dto/                    # 16个数据传输对象
│   │   ├── entity/                 # 8个实体类
│   │   ├── repository/             # 8个数据访问接口
│   │   └── service/                # 7个业务服务 + 核心算法引擎
│   └── src/main/resources/
│       ├── application.yml
│       └── schema.sql              # 数据库建表脚本
├── frontend/                       # Vue 3 前端
│   └── src/
│       ├── router/                 # 路由配置
│       ├── api/                    # Axios + API封装
│       └── views/                  # 8个页面组件
└── docs/
    ├── 需求分析/
    ├── 概要设计/
    ├── 详细设计/
    ├── 测试/
    └── diagram/                    # 3张数据流图SVG
```

---

## 算法公式

```
Score = α · cos(θ) + β₁ · LikeNorm + β₂ · FollowNorm + β₃ · RatingNorm

cos(θ) = (A · B) / (|A| · |B|)
```

- α=0.7, β₁=β₂=β₃=0.1（可配置）
- 特征：毛色标签、性格标签、位置、性别（One-Hot编码）
- 热度：点赞数、关注数、评分均分（Min-Max归一化）

---

## 小组协作指南

### 1. 环境准备

需要安装以下工具：

| 工具 | 用途 | 下载 |
|:----|:----|:----|
| JDK 17+ | 后端运行 | https://adoptium.net |
| Maven 3.9+ | 后端构建 | https://maven.apache.org |
| MySQL 8.0 | 数据库 | https://dev.mysql.com/downloads |
| Node.js 18+ | 前端运行 | https://nodejs.org |
| Git | 代码管理 | https://git-scm.com |

### 2. 克隆仓库

```bash
git clone https://github.com/Ursein/nanxin-catbook.git
cd nanxin-catbook
```

### 3. 创建数据库

打开 MySQL 命令行：
```bash
# 建库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS nanxin_maopu_v2 DEFAULT CHARACTER SET utf8mb4;"

# 建表
mysql -u root -p nanxin_maopu_v2 < backend/src/main/resources/schema.sql
```

### 4. 配置数据库密码

编辑 `backend/src/main/resources/application.yml`，把数据库密码改成你自己的：

```yaml
spring:
  datasource:
    password: 你的密码
```

### 5. 启动后端

```bash
cd backend
mvn spring-boot:run
```

看到 `Started NanxinCatbookApplication` 就说明成功了，后端跑在 `http://localhost:8080`。

### 6. 启动前端

```bash
cd frontend
npm install        # 第一次需要装依赖
npm run dev
```

打开 `http://localhost:5173` 就能看到页面了。

### 7. 改代码

- **前端页面** → `frontend/src/views/` 下的 `.vue` 文件，改完浏览器自动刷新
- **后端接口** → `backend/src/main/java/com/nanxin/catbook/` 下的 Java 文件，改完自动重启
- **文档** → `docs/` 下的 Markdown 文件

### 8. 提交代码

```bash
git add .
git commit -m "改了什么"
git push
```

---

## 许可证

MIT License