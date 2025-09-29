# CHATWEB - 智能客服聊天系统

## 项目简介

CHATWEB是一个基于Spring Boot和Netty的智能客服聊天系统，集成了AI对话功能、商品管理、用户管理等多个模块。系统支持实时聊天、智能回复、商品展示等功能，为电商平台提供完整的客服解决方案。
 ** 前端见 https://github.com/421843175/AICHATVUE **
## 技术栈
### 后端技术
- **Spring Boot 2.6.13** - 主框架
- **Netty 4.1.39** - WebSocket通信
- **MyBatis Plus 3.3.1** - 数据持久化
- **MySQL 8.0.30** - 数据库
- **Redis** - 缓存和会话管理
- **Apache Shiro 1.6.0** - 权限管理
- **JWT** - 身份认证

### AI集成
- **DeepSeek API** - AI对话服务
- **HanLP** - 中文分词处理

### 其他组件
- **Swagger 2.9.2** - API文档
- **Lombok** - 代码简化
- **FastJSON** - JSON处理
- **Apache HttpClient** - HTTP客户端

## 核心功能

### 1. 用户管理系统
- 用户注册、登录、认证
- 用户信息管理
- 头像上传功能
- 基于Shiro的权限控制

### 2. 智能聊天系统
- 基于Netty的WebSocket实时通信
- 支持用户与客服的实时对话
- AI智能回复（集成DeepSeek）
- 聊天记录持久化存储
- 关键词匹配和智能推荐

### 3. 商品管理
- 商品信息管理（增删改查）
- 商品图片上传
- 商品上架/下架管理
- 商品分页查询

### 4. 客服系统
- 人工客服管理
- 机器人客服自动回复
- 客服工作台
- 消息路由和分发

### 5. 评价系统
- 用户评价管理
- 评价数据统计

## 项目结构
src/main/java/com/jupiter/chatweb/
├── ChatwebApplication.java          # 主启动类
├── chat/                           # 聊天模块
│   ├── message/                    # 消息实体
│   ├── protocol/                   # 通信协议
│   └── server/                     # 服务器实现
├── config/                         # 配置类
│   ├── DeepSeekConfig.java         # AI配置
│   ├── ShiroConfig.java            # 权限配置
│   ├── UserRealm.java              # 用户认证
│   └── WebConfig.java              # Web配置
├── controller/                     # 控制器层
├── entity/                         # 实体类
├── mapper/                         # 数据访问层
├── pojo/                          # 数据传输对象
├── service/                       # 业务逻辑层
└── util/                          # 工具类


## 环境要求

- **JDK 1.8+**
- **Maven 3.6+**
- **MySQL 8.0+**
- **Redis 6.0+**

## 快速开始

### 1. 克隆项目
```bash
git clone <repository-url>
cd CHATWEB
```

### 2. 数据库配置
1. 创建MySQL数据库 `chatweb`
2. 执行SQL脚本初始化数据表
3. 修改 `application-dev.yaml` 中的数据库连接信息

### 3. Redis配置
确保Redis服务运行，并修改配置文件中的Redis连接信息：
```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    password: 123456
    database: 0
```

### 4. AI服务配置
在 `application-dev.yaml` 中配置DeepSeek API：
```yaml
deepseek:
  api-key: your-api-key
  base-url: https://api.deepseek.com
  model: deepseek-chat
```

### 5. 编译运行
```bash
mvn clean compile
mvn spring-boot:run
```

### 6. 访问应用
- **Web服务**: http://localhost:8090
- **WebSocket服务**: ws://localhost:8091/socket.io
- **API文档**: http://localhost:8090/swagger-ui.html

## 配置说明

### 应用配置 (application-dev.yaml)
```yaml
server:
  port: 8090                        # Web服务端口

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/chatweb?serverTimezone=UTC&zeroDateTimeBehavior=CONVERT_TO_NULL
    username: root
    password: "0000"
    driver-class-name: com.mysql.jdbc.Driver

  servlet:
    multipart:
      max-file-size: 10MB           # 文件上传大小限制
      max-request-size: 20MB

reply:
  min-match-score: 0.3              # 智能回复最低匹配分数

file:
  upload-dir: ./uploads             # 文件上传目录
```

## API接口

### 用户相关
- `POST /login` - 用户登录
- `POST /register` - 用户注册
- `GET /customer/list` - 获取用户列表

### 商品相关
- `GET /goods/all` - 获取所有商品
- `POST /goods/chat` - 发起商品咨询
- `POST /goods/update` - 更新商品信息

### 聊天相关
- `GET /chat/history` - 获取聊天记录
- `POST /chat/send` - 发送消息

### 文件上传
- `POST /upload/avatar` - 上传头像
- `POST /upload/goods` - 上传商品图片

## WebSocket消息格式

### 连接认证
```json
{
  "type": "auth",
  "token": "jwt-token"
}
```

### 发送消息
```json
{
  "type": "message",
  "to": "username",
  "body": "消息内容"
}
```

### 接收消息
```json
{
  "type": "message",
  "sender": "username",
  "body": "消息内容",
  "timestamp": "2024-01-01 12:00:00"
}
```

## 部署说明

### 1. 生产环境配置
- 修改数据库连接为生产环境
- 配置Redis集群
- 设置文件上传路径
- 配置日志级别

### 2. 打包部署
```bash
mvn clean package -Dmaven.test.skip=true
java -jar target/CHATWEB-0.0.1-SNAPSHOT.jar
```

### 3. Docker部署
```dockerfile
FROM openjdk:8-jre-alpine
COPY target/CHATWEB-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8090 8091
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 开发指南

### 添加新的消息类型
1. 在 `chat.message` 包下创建消息类
2. 继承 `Message` 基类
3. 在 `MessagesHandler` 中添加处理逻辑

### 添加新的业务模块
1. 创建实体类 (entity)
2. 创建数据访问层 (mapper)
3. 创建业务逻辑层 (service)
4. 创建控制器 (controller)

## 注意事项

1. **安全性**: 生产环境请修改默认密码和密钥
2. **性能**: 大量并发时建议使用Redis集群
3. **监控**: 建议集成监控系统监控应用状态
4. **日志**: 生产环境建议使用ELK进行日志管理

## 许可证

本项目采用 MIT 许可证，详情请参阅 LICENSE 文件。

## 联系方式

如有问题或建议，请联系开发团队。



---

<img width="2560" height="1520" alt="image" src="https://github.com/user-attachments/assets/87c6bbda-6a49-4816-831c-4ea6e067f40a" />


**版本**: 0.0.1-SNAPSHOT  
**最后更新**: 2024年1月
