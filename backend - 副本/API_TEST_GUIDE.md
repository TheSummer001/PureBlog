# 认证模块 API 测试指南

## 📌 模块说明

本模块实现了基于 JWT 的用户认证功能，包括登录、登出、获取用户信息等核心接口。

---

## 🔐 接口列表

### 1. 用户登录

**接口路径**: `POST /api/auth/login`  
**是否需要认证**: 否  
**描述**: 使用用户名和密码登录，返回 JWT Token

#### 请求参数

```json
{
  "username": "admin",
  "password": "admin123"
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "expiration": 1702368000000,
    "userId": 1,
    "username": "admin",
    "nickname": "超级管理员",
    "avatar": null
  },
  "timestamp": 1701763200000
}
```

#### 错误响应

```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "timestamp": 1701763200000
}
```

---

### 2. 用户登出

**接口路径**: `POST /api/auth/logout`  
**是否需要认证**: 是  
**描述**: 清除用户登录状态（前端需清除本地 Token）

#### 请求头

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

#### 响应示例

```json
{
  "code": 200,
  "message": "登出成功",
  "timestamp": 1701763200000
}
```

---

### 3. 获取当前用户信息

**接口路径**: `GET /api/auth/info`  
**是否需要认证**: 是  
**描述**: 获取当前登录用户的详细信息（包含角色和权限）

#### 请求头

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": 1,
    "username": "admin",
    "nickname": "超级管理员",
    "email": "admin@example.com",
    "phone": null,
    "avatar": null,
    "status": 1,
    "roles": ["ROLE_ADMIN"],
    "permissions": []
  },
  "timestamp": 1701763200000
}
```

---

## 🧪 测试步骤

### 使用 cURL 测试

#### 1. 登录获取 Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

#### 2. 使用 Token 获取用户信息

```bash
curl -X GET http://localhost:8080/api/auth/info \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

#### 3. 登出

```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### 使用 Postman 测试

1. **导入环境变量**:

   - `BASE_URL`: `http://localhost:8080`
   - `TOKEN`: (登录后自动设置)

2. **登录接口**:

   - Method: `POST`
   - URL: `{{BASE_URL}}/api/auth/login`
   - Body (JSON):
     ```json
     {
       "username": "admin",
       "password": "admin123"
     }
     ```
   - Tests (自动保存 Token):
     ```javascript
     pm.environment.set("TOKEN", pm.response.json().data.token);
     ```

3. **获取用户信息**:
   - Method: `GET`
   - URL: `{{BASE_URL}}/api/auth/info`
   - Headers:
     - `Authorization`: `Bearer {{TOKEN}}`

---

## 📝 注意事项

1. **Token 格式**: 所有需要认证的接口，请求头必须携带 `Authorization: Bearer <token>`
2. **Token 过期时间**: 默认 7 天（604800000 毫秒）
3. **密码加密**: 数据库中的密码使用 BCrypt 加密存储
4. **默认账户**:
   - 用户名: `admin`
   - 密码: `admin123`
5. **用户状态**: 只有 `status = 1` 的用户才能登录

---

## 🔧 前端集成示例

### Axios 拦截器配置

```javascript
import axios from "axios";

// 创建 Axios 实例
const request = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 5000,
});

// 请求拦截器：自动添加 Token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器：处理 401 未认证
request.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      // 清除 Token，跳转登录页
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default request;
```

### 登录示例

```javascript
import request from "@/utils/request";

// 登录
export function login(username, password) {
  return request({
    url: "/api/auth/login",
    method: "post",
    data: { username, password },
  }).then((res) => {
    // 保存 Token
    localStorage.setItem("token", res.data.token);
    return res;
  });
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: "/api/auth/info",
    method: "get",
  });
}

// 登出
export function logout() {
  return request({
    url: "/api/auth/logout",
    method: "post",
  }).then((res) => {
    localStorage.removeItem("token");
    return res;
  });
}
```

---

## 🎯 Knife4j 在线文档

启动项目后，访问在线 API 文档：

- **文档地址**: http://localhost:8080/doc.html
- **分组**: personal-blog
- **标签**: 认证管理

在文档中可以直接测试所有接口。

---

## ✅ 测试检查清单

- [ ] 登录接口返回正确的 Token
- [ ] 使用错误的密码登录，返回 401 错误
- [ ] 使用禁用用户登录，返回 403 错误
- [ ] 携带 Token 访问 `/api/auth/info` 返回用户信息
- [ ] 不携带 Token 访问需认证接口，返回 401 错误
- [ ] Token 过期后，返回 401 错误
- [ ] 登出接口正常响应
