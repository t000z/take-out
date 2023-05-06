# 工程简介
开发环境：Ubuntu-22.04.1-LTS, Docker
使用组件：MySQL, Nacos, Tomcat, Nginx
技术实现：SpringMVC, MyBatisPlus, SpringGateway, Log4J, Vue, AJAX, element-ui
测试工具：PostMan, Sentinel, Jmeter
开发工具：IDEA, Maven

项目分为管理端、用户端两大板块。
管理端有用户管理、订单管理、菜品管理模块，用户端有菜品获取、个人信息管理、购物车模块。
管理端与用户端登录方式不同，用户端可以支持邮箱验证码登录。
使用Vue, AJAX, element-ui完成了简单的前端页面。
后端根据前端的请求方式、请求URL使用MyBatisPlus完成CRUD操作。
