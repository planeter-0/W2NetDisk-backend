# W2NetDisk-backend

> Copyright Planeter 2021 All Rights Reserved.
>
> Only used for the cooperative assessment of W2OL
>
> A Java backend project 

> Frontend project [W2NetDisk](https://github.com/MarkPoloChina/W2NetDisk) [@MarkPoloChina](https://github.com/MarkPoloChina)

## 项目结构
### [接口文档](http://planeter.icu:8088/swagger-ui.html)

![项目结构](http://planeter.icu:8088/view/130)


## 技术栈说明

* JDK 11
* Maven构建
* 基于Spring Boot
* MySQL8.0+Spring Data JPA 实现数据持久化
* Spring Security + JWT 实现安全认证与授权

## 功能说明
#### 一、网盘系统
* 上传：多文件上传
* 下载：多文件下载(前端多个请求才实现)
* 删除：支持文件夹整体删除，多文件删除
* 目录系统：可以建立多层目录来管理图片，支持在线预览图片
* 网盘大小：限制用户网盘大小为20GiB

#### 二、后台系统
* 未审核图片的查看
* 图片审核

#### 三、用户系统

- 用户的注册，登录与注销
- 管理员的登陆与注销，管理员只能由数据库管理员手动插入
- 共用一个登陆页面，登陆后跳转至不同页面(前端路由)

## 部署说明

> 数据库和JDK版本见 [项目结构](#技术栈说明)

> Shcema名为netdisk，SQL建表文件见 src/main/resources/sql

> 端口设置和文件存储路径见 src/main/resources/application-dev.yml
### 本地部署

略

### 服务器部署

暂无

## 不足之处

- Docker部署
- 缓存
- 分布式文件系统
- 大文件切块上传
- 其他文件格式
- 回收站和收藏家
- 采集用户请求日志和管理员操作日志
- 文件分享
