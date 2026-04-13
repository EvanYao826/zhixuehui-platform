# 知学汇 - 在线智慧教育平台

基于微服务架构的教育平台系统，提供全方位的在线学习解决方案

## 📌 项目简介

知学汇项目是一款大型在线学习网站，基于 **SpringCloud Alibaba** 微服务技术栈，集成在线教育、社交、电商等服务。引入 **Spring AI 框架**构建智能 **Agent** 服务，为学生端提供个性化课程推荐，为管理端提供自动化运营助手（周报/月报生成、数据查询）。学生端核心业务包括视频点播、积分排行、互动问答、课程购买等；管理端核心业务包括用户管理、课程管理、优惠券管理等。

### 核心功能

**👑 后台管理系统**

- 完善的用户权限管理（RBAC）
- 课程管理与发布
- 学习数据统计与分析
- 考试与测评管理

**📱 前端系统**

- 课程浏览与学习
- 在线考试与测评
- 学习进度跟踪
- 互动与讨论

**💡 核心价值**：提供一站式在线教育解决方案，支持多种学习场景和教学模式

## ✨ 核心特性

### 🎯 技术亮点

| 特性    | 说明                        |
| ----- | ------------------------- |
| 微服务架构 | 模块化设计，独立部署，弹性扩展           |
| 分布式架构 | 服务注册与发现，配置中心，负载均衡         |
| 高可用设计 | 服务容错，熔断降级，分布式事务           |
| 数据安全  | 权限认证，数据加密，请求拦截            |
| 多存储支持 | MySQL，Redis，Elasticsearch |
| 云存储集成 | 阿里云OSS，腾讯云COS             |
| 消息队列  | 异步解耦，可靠消息传递               |

## 🛠️ 技术栈

### 核心框架

- Java 11
- Spring Boot 2.7.2
- Spring Cloud 2021.0.3
- Spring Cloud Alibaba 2021.0.1.0

### 微服务生态

- Nacos：服务注册 + 配置中心
- Sentinel：流量控制 + 降级熔断
- Seata 1.5.1：分布式事务
- XXL-Job 2.3.1：分布式任务调度

### 数据存储

- MySQL 8.0.23：关系型数据库
- Redis：缓存
- Elasticsearch 7.12.1：搜索引擎
- 阿里云OSS：对象存储
- 腾讯云COS：对象存储

### 中间件

- RabbitMQ：消息队列
- Redisson 3.13.6：Redis客户端

### 工具库

- MyBatis-Plus 3.4.3：ORM框架
- Hutool 5.7.17：Java工具库
- Swagger 3.0.3：API文档
- Lombok 1.18.20：代码简化工具

## 🏗️ 架构设计

### 系统架构

- **API网关**：统一入口，请求路由，权限控制
- **微服务集群**：业务服务独立部署，服务间通过RPC调用
- **数据存储**：多种存储方案，满足不同业务场景
- **中间件**：消息队列，缓存，搜索引擎
- **第三方服务**：云存储，支付，短信

### 代码分层设计

- **Controller**：请求处理，参数验证，响应封装
- **Service**：业务逻辑，事务处理
- **Mapper**：数据访问，SQL执行
- **DTO/VO**：数据传输对象，视图对象
- **Entity**：实体对象，数据库映射
- **Config**：配置类，Bean定义
- **Utils**：工具类，通用方法

## 📁 项目结构

```
tianji/
├── tj-common/                    # 公共模块
│   ├── src/main/java/com/tianji/common/  # 公共代码
│   └── pom.xml
├── tj-api/                       # API接口模块
│   ├── src/main/java/com/tianji/api/     # 接口定义
│   └── pom.xml
├── tj-auth/                      # 认证授权服务
│   ├── tj-auth-common/           # 认证通用模块
│   ├── tj-auth-gateway-sdk/      # 网关认证SDK
│   ├── tj-auth-resource-sdk/     # 资源认证SDK
│   ├── tj-auth-service/          # 认证服务
│   └── pom.xml
├── tj-gateway/                   # API网关
│   ├── src/main/java/com/tianji/gateway/  # 网关代码
│   └── pom.xml
├── tj-user/                      # 用户服务
├── tj-course/                    # 课程服务
├── tj-learning/                  # 学习服务
├── tj-exam/                      # 考试服务
├── tj-media/                     # 媒体服务
├── tj-message/                   # 消息服务
├── tj-pay/                       # 支付服务
├── tj-trade/                     # 交易服务
├── tj-search/                    # 搜索服务
├── tj-data/                      # 数据服务
├── tj-remark/                    # 评论服务
├── pom.xml                       # 父POM
└── startup.sh                    # 启动脚本
```

## 🚀 快速开始

### 环境要求

- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.0+
- RabbitMQ 3.8+

### 本地开发

1. **克隆项目**
   ```bash
   git clone http://192.168.150.101:10880/tjxt/tianji.git
   cd tianji
   ```
2. **修改配置**
   - 配置Nacos地址：修改各服务的`bootstrap.yml`文件
   - 配置数据库连接：修改各服务的配置文件
   - 配置Redis连接：修改各服务的配置文件
3. **编译项目**
   ```bash
   mvn clean install -DskipTests
   ```
4. **启动服务**
   - 先启动Nacos服务
   - 依次启动各微服务
   - 启动网关服务

### 部署说明

- **开发环境**：本地启动，使用`bootstrap-dev.yml`配置
- **测试环境**：服务器部署，使用`bootstrap-test.yml`配置
- **生产环境**：容器化部署，使用`bootstrap-prod.yml`配置

## 📖 文档

### API文档

- 使用Swagger 3.0生成API文档
- 访问地址：`http://localhost:8080/doc.html`

### 开发文档

- 代码规范：遵循Java代码规范
- 命名规范：包名小写，类名驼峰，方法名驼峰
- 注释规范：类、方法、参数都需要添加注释

## 🔧 核心模块

### 认证授权模块

- JWT token生成与验证
- RBAC权限管理
- 用户登录与注册

### 课程模块

- 课程管理与发布
- 课程分类与搜索
- 课程内容管理

### 学习模块

- 学习进度跟踪
- 学习记录管理
- 互动与讨论

### 考试模块

- 题库管理
- 考试创建与管理
- 成绩统计与分析

### 媒体模块

- 文件上传与管理
- 视频处理与播放
- 云存储集成

## 🤝 贡献

欢迎各位开发者贡献代码，提交Issue和Pull Request。

### 贡献流程

1. Fork本仓库
2. 创建特性分支
3. 提交代码
4. 提交Pull Request

## 📄 许可证

本项目采用MIT许可证。

## 📞 联系我们

- 项目地址：<http://192.168.150.101:10880/tjxt/tianji.git>
- 开发者：知学汇团队

***

**知学汇** - 让教育更智能，让学习更高效！
