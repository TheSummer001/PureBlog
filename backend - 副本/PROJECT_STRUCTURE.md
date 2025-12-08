# Personal Blog System - 项目架构说明

## 📦 项目概述

这是一个基于 **Spring Boot 3 + DDD + Clean Architecture** 的开源级个人博客系统基础架构。
采用模块化单体（Modular Monolith）设计，高扩展、易维护。

## 🏗️ 技术栈

### 核心框架

- **Spring Boot**: 3.5.8
- **JDK**: 21
- **MySQL**: 8.0+
- **MyBatis-Plus**: 3.5.9（支持自动填充审计字段）

### 安全认证

- **Spring Security**: 无状态认证
- **JWT**: Token 认证方案
- **BCrypt**: 密码加密

### 工具库

- **Lombok**: 简化 POJO 代码
- **Hutool**: Java 工具类库
- **Jackson**: JSON 序列化

### 文档

- **Knife4j**: 增强版 Swagger 3

## 📁 项目结构

```
com.github.toran
├── common                          # 通用模块
│   ├── constant                   # 常量定义
│   │   ├── CommonConstant         # 通用常量
│   │   └── ResultCode             # 响应状态码
│   ├── core.domain                # 核心领域对象
│   │   ├── BaseEntity             # 基础实体类（id, createTime, updateTime, deleted）
│   │   └── Result<T>              # 统一响应体（code, message, data, timestamp）
│   └── exception                  # 自定义异常
│       ├── BaseException          # 基础异常
│       └── BizException           # 业务异常
│
├── framework                       # 框架层（核心基础设施）
│   ├── aspectj                    # AOP 切面
│   │   └── WebLogAspect           # 请求日志切面（记录 API 请求参数、耗时、响应）
│   ├── config                     # 框架配置
│   │   ├── MybatisPlusConfig      # MyBatis-Plus 配置（分页、乐观锁）
│   │   ├── MybatisPlusMetaObjectHandler  # 字段自动填充处理器
│   │   └── Knife4jConfig          # API 文档配置
│   ├── handler                    # 全局处理器
│   │   └── GlobalExceptionHandler # 全局异常处理器（统一异常拦截）
│   └── security                   # 安全模块
│       ├── config
│       │   ├── JwtProperties      # JWT 配置属性
│       │   └── SecurityConfig     # Security 配置（拦截规则、过滤器链）
│       ├── domain
│       │   └── LoginUser          # 登录用户信息（实现 UserDetails）
│       ├── filter
│       │   └── JwtAuthenticationTokenFilter  # JWT 认证过滤器
│       └── util
│           └── JwtTokenUtil       # JWT 工具类（生成、解析、验证 Token）
│
├── module                          # 业务模块
│   ├── system                     # 系统模块（用户、角色、配置）
│   │   ├── entity                # 实体类
│   │   │   ├── SysUser           # 用户实体
│   │   │   ├── SysRole           # 角色实体
│   │   │   ├── SysUserRole       # 用户角色关联
│   │   │   └── SysConfig         # 系统配置实体
│   │   ├── mapper                # Mapper 接口
│   │   │   └── SysConfigMapper
│   │   ├── service               # 服务层
│   │   │   ├── ISysConfigService # 配置服务接口
│   │   │   └── impl
│   │   │       └── SysConfigServiceImpl  # 配置服务实现（支持动态读取配置，预留 Redis 缓存）
│   │   └── controller            # 控制器
│   │       └── SystemController  # 系统健康检查 API
│   │
│   ├── infra                      # 基础设施模块（OSS、邮件等）
│   │   ├── service
│   │   │   ├── FileStorageService  # 文件存储接口（抽象层）
│   │   │   └── impl
│   │   │       └── LocalFileStorageServiceImpl  # 本地存储实现
│   │   └── factory
│   │       └── FileStorageFactory  # 文件存储工厂（根据配置动态选择实现）
│   │
│   └── content                    # 内容模块（预留：文章、标签等）
│       └── (待实现)
│
└── PersonalBlogApplication.java   # 启动类
```

## 🗄️ 数据库设计

### 核心表结构

#### 1. sys_user（用户表）

```sql
- id: 主键（雪花算法）
- username: 用户名（唯一）
- password: 密码（BCrypt 加密）
- nickname: 昵称
- email: 邮箱
- phone: 手机号
- avatar: 头像 URL
- status: 状态（0-禁用，1-启用）
- create_time, update_time, deleted（审计字段）
```

#### 2. sys_role（角色表）

```sql
- id: 主键
- role_name: 角色名称
- role_key: 角色标识（如 ROLE_ADMIN）
- sort: 排序
- status: 状态
- create_time, update_time, deleted
```

#### 3. sys_user_role（用户角色关联表）

```sql
- id: 主键
- user_id: 用户 ID
- role_id: 角色 ID
- create_time, update_time, deleted
```

#### 4. sys_config（系统配置表）

**核心功能：动态配置中心**

```sql
- id: 主键
- config_key: 配置键（唯一，如 oss.storage.type）
- config_value: 配置值
- description: 配置描述
- is_encrypted: 是否加密
- config_group: 配置分组（oss、email、giscus）
- create_time, update_time, deleted
```

**配置示例：**

```sql
oss.storage.type = local          # 存储类型（local/oss/minio）
oss.local.path = D:/upload/       # 本地存储路径
oss.local.url.prefix = http://localhost:8080/files/
```

## 🔑 核心设计思想

### 1. 统一响应体（Result<T>）

所有 API 返回统一格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1234567890
}
```

### 2. 全局异常处理

- 业务异常：`BizException` → 返回 400 + 业务错误信息
- 参数校验异常：`@Validated` → 返回 400 + 校验失败详情
- 认证异常：`AuthenticationException` → 返回 401
- 权限异常：`AccessDeniedException` → 返回 403
- 系统异常：`Exception` → 返回 500

### 3. MyBatis-Plus 自动填充

- `createTime`：插入时自动填充当前时间
- `updateTime`：插入/更新时自动填充当前时间
- `deleted`：插入时自动填充 0（未删除）

### 4. 动态配置中心

`SysConfigService` 支持：

- 从数据库读取配置
- 预留 Redis 缓存接口（提升性能）
- 动态切换 OSS 实现类（Local/OSS/MinIO）

### 5. OSS 抽象层

**设计模式：策略模式 + 工厂模式**

```java
FileStorageFactory.getFileStorageService()
  → 从 SysConfig 读取 oss.storage.type
  → 动态返回对应实现类（LocalFileStorageServiceImpl / OssFileStorageServiceImpl）
```

未来扩展 OSS 实现类时，只需：

1. 实现 `FileStorageService` 接口
2. 注册为 Spring Bean（命名规则：`{type}FileStorageService`）
3. 在数据库配置 `oss.storage.type = oss`

## 🚀 快速开始

### 1. 环境准备

- JDK 21+
- MySQL 8.0+
- Maven 3.6+

### 2. 初始化数据库

```bash
执行 src/main/resources/db/schema.sql
```

**默认管理员账号：**

- 用户名：`admin`
- 密码：`admin123`

### 3. 修改配置

编辑 `application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/personal_blog
    username: root
    password: your_password
```

### 4. 启动项目

```bash
mvn spring-boot:run
```

### 5. 访问 API 文档

- Knife4j 文档：http://localhost:8080/doc.html
- 健康检查：http://localhost:8080/api/public/system/health

## 📋 后续扩展方向

### 业务模块（module.content）

- [ ] 文章管理（发布、编辑、删除、分类、标签）
- [ ] 评论系统（集成 Giscus）
- [ ] 文件上传（头像、文章图片）
- [ ] 搜索功能（ElasticSearch）

### 系统增强

- [ ] Redis 缓存（配置、Token）
- [ ] 分布式锁（Redisson）
- [ ] 定时任务（文章定时发布）
- [ ] 消息队列（文章发布通知）

### DevOps

- [ ] Docker 部署
- [ ] CI/CD 流水线
- [ ] 监控告警（Prometheus + Grafana）

## 📄 许可证

Apache License 2.0

---

**作者：** toran  
**版本：** 1.0.0  
**日期：** 2025-12-08
