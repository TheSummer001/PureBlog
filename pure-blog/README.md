# PureBlog Frontend

基于 Vue 3 + TypeScript + Vite 的个人博客前端项目

## 技术栈

- **核心框架**: Vue 3 (Script Setup), TypeScript, Vite
- **UI 框架**: Naive UI (支持深色模式)
- **样式**: UnoCSS (原子化 CSS)
- **状态管理**: Pinia (带持久化)
- **网络请求**: Axios
- **Markdown 编辑器**: MdEditorV3
- **评论系统**: @giscus/vue

## 项目结构

```
src/
├── api/              # API 接口封装
│   ├── auth/         # 认证相关接口
│   ├── article/      # 文章相关接口
│   ├── config/       # 配置相关接口
│   └── file/         # 文件相关接口
├── components/       # 公共组件
│   ├── BizComments/  # 评论组件
│   └── ThemeToggle/  # 主题切换组件
├── hooks/            # 自定义 Hooks
│   └── useImageUpload.ts  # 图片上传 Hook
├── store/            # 状态管理
│   ├── app.ts        # 应用状态 (主题等)
│   └── user.ts       # 用户状态
├── utils/            # 工具类
│   └── request.ts    # 网络请求封装
├── views/            # 页面视图
├── router/           # 路由配置
└── main.ts           # 入口文件
```

## 核心功能

### 1. 网络层封装 (src/utils/request.ts)

- 使用 Axios 封装统一请求拦截器和响应拦截器
- 自动注入 JWT Token 到请求头
- 统一处理响应结果和错误信息
- Token 过期自动跳转登录页

### 2. 主题管理 (src/store/app.ts)

- 支持浅色/深色主题切换
- 使用 Pinia 管理状态并持久化到 localStorage
- 集成 Naive UI 的主题系统

### 3. Giscus 评论适配器 (src/components/BizComments/index.vue)

- 动态获取后端配置渲染评论组件
- 支持加载状态和错误处理

### 4. 图片上传 Hook (src/hooks/useImageUpload.ts)

- 封装图片上传逻辑
- 支持 Markdown 编辑器的图片上传功能

## 开发指南

1. 安装依赖:

   ```bash
   npm install
   ```

2. 启动开发服务器:

   ```bash
   npm run dev
   ```

3. 构建生产版本:
   ```bash
   npm run build
   ```

## 注意事项

- 后端 API 地址通过 Vite 代理转发到 `http://localhost:8080`
- 主题设置会自动保存到 localStorage
- 所有 API 响应遵循统一格式: `{ code: number, message: string, data: T }`
