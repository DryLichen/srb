# 尚融宝

涉及前后端的练习项目，实现了一个互联网金融借贷平台，包括平台管理系统和用户借贷系统。

# 技术栈

- **前端：** HTML, CSS, JavaScript, Vue2, axios, Nuxt.js, Thymeleaf
- **第三方组件：** Element-UI, vue-element-admin
- **后端：** Java, Spring Boot2, MyBatis-Plus, SpringCloud, Swagger, Logback, Postman
- **数据库和中间件：** MySQL8, Redis, RabbitMQ, Nginx
- **第三方接口：** 容联云短信, 阿里云OSS

# 模块

## 模块 1: [demo]

对部分项目涉及技术的练习，包括easy-excel, mybatis-plus, vue, es6以及Nuxt

## 模块 2: [汇付宝后端服务器]

运行第三方的汇付宝服务，模拟第三方资金托管平台

## 模块 3: [尚融宝后端服务器]

包括三个微服务，分别是系统核心服务，短信服务和对象存储服务。
开发前期，使用Nginx反向代理访问三个微服务。后期利用nacos注册管理微服务，gateway生成转发路由

### 系统核心服务

- 实现后台管理系统和客户端主要业务逻辑的服务
- 利用Redis实现数据词典的缓存
- 

### 短信服务

引入容联云的短信发送服务

- 调用
- RabbitMQ

### 对象存储服务


## 模块 4: [后台管理系统客户端]

尚融宝后台管理的客户端，利用Vue2，vue-element-admin搭建。

- 积分等级管理
- 数据字典的上传和导出
- 借款人资质的审核和额度管理
- 借款申请管理
- 标的的审核放款
- 用户账号管理


## 模块 5: [尚融宝客户端]

借款人和投资人的用户平台，利用Nuxt和Element-UI搭建。

- 用户注册和登录系统
- 

