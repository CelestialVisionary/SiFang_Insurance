# 四方保险 - 金融保险销售SaaS平台

## 项目简介
四方保险是一个面向保险销售公司和保险产品需求用户的线上SaaS平台，基于SpringCloud Alibaba架构，采用前后端分离开发模式。

## 技术栈

### 后端技术栈
- 核心框架：SpringCloud Alibaba + Spring Boot
- 服务注册与发现：Nacos
- 网关：Spring Cloud Gateway
- 服务调用：OpenFeign
- 分布式配置：Nacos Config
- 流量控制：Sentinel
- 分布式事务：Seata-AT
- 消息队列：RabbitMQ、SpringCloud-Stream
- 数据库：MySQL、InfluxDB
- 缓存：Redis

### 前端技术栈
- PC端：Vue.js
- 小程序端：UniApp

## 目录结构
- backend/：后端微服务代码
- frontend/：前端代码
- docs/：项目文档
- scripts/：部署脚本

## 快速开始
```bash
# 启动后端服务
cd backend
mvn clean install
# 启动各微服务

# 启动前端
cd ../frontend
npm install
npm run serve
```