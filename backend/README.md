# Personal Blog System

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/JDK-21-blue" alt="JDK">
  <img src="https://img.shields.io/badge/MySQL-8.0-orange" alt="MySQL">
  <img src="https://img.shields.io/badge/MyBatis--Plus-3.5.9-red" alt="MyBatis-Plus">
  <img src="https://img.shields.io/badge/License-Apache%202.0-green" alt="License">
</p>

## 📖 项目简介

基于 **Spring Boot 3 + DDD + Clean Architecture** 的企业级个人博客系统基础架构。

采用 **模块化单体（Modular Monolith）** 设计理念，兼顾代码清晰度与系统扩展性，适合作为开源项目或学习参考。

## ✨ 核心特性

### 🏗️ 架构设计

- ✅ **DDD 领域驱动设计**：模块化分层架构，清晰的领域边界
- ✅ **Clean Architecture**：高内聚低耦合，易于测试和维护
- ✅ **模块化单体**：common、framework、module.system、module.infra

### 🔐 安全认证

- ✅ **Spring Security + JWT**：无状态认证，Token 过期管理
- ✅ **BCrypt 密码加密**：安全可靠的密码存储方案
- ✅ **统一认证拦截**：自定义过滤器链，灵活的权限控制

### 💾 数据访问

- ✅ **MyBatis-Plus**：强大的 CRUD 增强，减少样板代码
- ✅ **自动填充审计字段**：createTime、updateTime、deleted 自动管理
- ✅ **逻辑删除**：软删除机制，数据安全可追溯
- ✅ **分页插件**：开箱即用的分页查询

### 🛠️ 基础设施

- ✅ **统一响应体**：Result<T> 封装，标准的 API 返回格式
- ✅ **全局异常处理**：统一拦截业务异常、参数校验异常、系统异常
- ✅ **AOP 请求日志**：自动记录 API 请求参数、耗时、响应结果
- ✅ **动态配置中心**：基于数据库的配置管理，预留 Redis 缓存

### 📦 存储抽象层

- ✅ **OSS 接口抽象**：FileStorageService 统一接口
- ✅ **策略模式 + 工厂模式**：动态切换存储实现（Local/OSS/MinIO）
- ✅ **配置驱动**：通过数据库配置切换存储类型

### 📚 API 文档

- ✅ **Knife4j 增强文档**：美观的 API 在线文档
- ✅ **OpenAPI 3 标准**：符合行业规范

## 🚀 快速开始

### 1. 环境要求

- **JDK**: 21+
- **MySQL**: 8.0+
- **Maven**: 3.6+

### 2. 克隆项目

```bash
git clone https://github.com/toran/personal-blog.git
cd personal-blog
```

### 3. 初始化数据库

执行数据库脚本：

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

**默认管理员账号：**

- 用户名：`admin`
- 密码：`admin123`

### 4. 修改配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/personal_blog
    username: root
    password: your_password # 修改为你的数据库密码
```

### 5. 启动项目

```bash
mvn clean install
mvn spring-boot:run
```

### 6. 访问文档

- **Knife4j 文档**：http://localhost:8080/doc.html
- **健康检查**：http://localhost:8080/api/public/system/health
- **系统信息**：http://localhost:8080/api/public/system/info

## 📂 项目结构

详细结构请参考 [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md)

```
com.github.toran
├── common/                 # 通用模块（Result、异常、常量）
├── framework/              # 框架层（Security、AOP、异常处理）
├── module/
│   ├── system/            # 系统模块（用户、角色、配置）
│   ├── infra/             # 基础设施（OSS、邮件）
│   └── content/           # 内容模块（预留）
└── PersonalBlogApplication.java
```

## 🗄️ 核心表设计

| 表名          | 说明         | 核心字段                               |
| ------------- | ------------ | -------------------------------------- |
| sys_user      | 用户表       | username, password, email, status      |
| sys_role      | 角色表       | role_name, role_key, status            |
| sys_user_role | 用户角色关联 | user_id, role_id                       |
| sys_config    | 系统配置表   | config_key, config_value, config_group |

## 🔧 核心功能

### 1. 统一响应体

所有 API 返回统一格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1702234567890
}
```

### 2. 动态配置中心

通过数据库配置管理系统参数，示例：

```sql
INSERT INTO sys_config (config_key, config_value, config_group)
VALUES ('oss.storage.type', 'local', 'oss');
```

在代码中使用：

```java
@Autowired
private ISysConfigService sysConfigService;

String storageType = sysConfigService.getConfigValue("oss.storage.type", "local");
```

### 3. OSS 存储抽象

动态切换存储实现：

```java
@Autowired
private FileStorageFactory fileStorageFactory;

FileStorageService storage = fileStorageFactory.getFileStorageService();
String fileUrl = storage.upload("avatar/", "user.jpg", inputStream);
```

## 📚 技术栈详情

### 后端框架

- Spring Boot 3.5.8
- Spring Security
- Spring AOP

### 持久层

- MyBatis-Plus 3.5.9
- MySQL Connector

### 工具库

- Lombok
- Hutool 5.8.34
- Jackson

### 安全认证

- JWT (jjwt 0.12.6)
- BCrypt

### API 文档

- Knife4j 4.5.0
- OpenAPI 3

## 🎯 设计原则

1. **单一职责原则**：每个类只负责一件事
2. **开闭原则**：对扩展开放，对修改关闭（如 OSS 抽象层）
3. **依赖倒置原则**：面向接口编程（FileStorageService）
4. **DRY 原则**：Don't Repeat Yourself（BaseEntity、Result）

## 🛣️ 开发路线

### 已完成 ✅

- [x] 项目架构搭建
- [x] 统一响应体 & 异常处理
- [x] JWT 认证 & Security 配置
- [x] MyBatis-Plus 自动填充
- [x] 动态配置中心
- [x] OSS 抽象层
- [x] API 文档集成

### 进行中 🚧

- [ ] 用户管理模块
- [ ] 文章管理模块
- [ ] 评论系统（Giscus）

### 计划中 📋

- [ ] Redis 缓存
- [ ] ElasticSearch 搜索
- [ ] 定时任务
- [ ] Docker 部署

## 📄 许可证

本项目采用 [Apache License 2.0](LICENSE) 许可证。

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📧 联系方式

- **作者**：toran
- **邮箱**：toran@example.com
- **GitHub**：https://github.com/toran

---

⭐ 如果觉得项目不错，欢迎 Star 支持！
