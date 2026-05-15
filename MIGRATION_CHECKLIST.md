# Spring Boot 2.x → 3.x 迁移检查清单

## ✅ 已完成的核心迁移项

### 1. Spring Boot & Java 版本升级
- [x] Spring Boot: 2.x → 3.3.0
- [x] Java: 8 → 17
- [x] Maven Compiler Plugin: 3.6.1 → 3.12.1
- [x] Spring Boot Maven Plugin: 更新到 3.3.0

### 2. javax → jakarta 包迁移（关键文件）
- [x] StringContainsValidator.java: javax.validation → jakarta.validation
- [x] XmlUtil.java: javax.servlet, javax.xml.* → jakarta.*
- [x] ResponseFilter.java: javax.servlet → jakarta.servlet
- [x] 注意：剩余 ~97 个文件还需要批量替换（详见下方【后续步骤】）

### 3. Spring Security 6 重构
- [x] 移除 WebSecurityConfigurerAdapter 继承
- [x] 使用 @EnableMethodSecurity 替代 @EnableGlobalMethodSecurity
- [x] 改用 SecurityFilterChain Bean 配置方式
- [x] Lambda DSL 风格配置
- [x] antMatchers() → requestMatchers()
- [x] AuthenticationManager 改为独立 Bean 注册

### 4. Swagger 迁移
- [x] SpringFox → SpringDoc OpenAPI 2.5.0
- [x] 移除 @EnableSwagger2 注解
- [x] SwaggerConfig 重写为 SpringDoc 风格
- [x] 文档路径更新为 /swagger-ui.html, /v3/api-docs/**

### 5. @ConfigurationProperties 配置更新
- [x] 主启动类添加 @ConfigurationPropertiesScan
- [x] CrmebConfig、SwaggerConfig 配置保持兼容

### 6. 第三方依赖版本升级
- [x] MyBatis Plus: 3.x → 3.5.5
- [x] Druid: 支持 Spring Boot 3 Starter
- [x] Jedis: 4.4.6
- [x] Hutool: 5.8.24
- [x] Fastjson: 1.x → 2.0.43 (fastjson2)
- [x] 分页插件: 支持 Spring Boot 3
- [x] JAXB API: 显式引入（Java 17 默认不包含）

### 7. Redis 配置
- [x] Spring Data Redis API 兼容
- [x] Jedis 客户端配置保持兼容

---

## 📋 后续步骤（需要执行）

### 步骤 1: 全局 javax → jakarta 替换（重要！）
```bash
# 在项目根目录执行以下替换（IDE 全局替换也行）：

# 1. validation 包
import javax.validation. → import jakarta.validation.
import javax.validation.constraints. → import jakarta.validation.constraints.

# 2. servlet 包
import javax.servlet. → import jakarta.servlet.
import javax.servlet.http. → import jakarta.servlet.http.

# 3. annotation 包
import javax.annotation. → import jakarta.annotation.

# 4. xml bind 包
import javax.xml.bind. → import jakarta.xml.bind.
import javax.xml.parsers. → import jakarta.xml.parsers.
import javax.xml.transform. → import jakarta.xml.transform.

# 5. persistence 包（如果有 JPA）
import javax.persistence. → import jakarta.persistence.
```

**注意坑点**:
- ⚠️ 如果有 `javax.xml.ws.*` 相关代码，需要单独引入 JAX-WS 依赖
- ⚠️ 第三方库可能需要升级到支持 jakarta 的版本

### 步骤 2: 检查并更新 crmeb-front 模块
- [ ] 更新 crmeb-front/pom.xml 中的 SpringDoc 依赖
- [ ] 更新 crmeb-front 的 SwaggerConfig（如果有）
- [ ] 更新 crmeb-front 的启动类

### 步骤 3: 编译验证
```bash
mvn clean compile -DskipTests
```

**常见编译错误及修复**:
1. 找不到 `javax.*` 包 → 执行上面的全局替换
2. `WebSecurityConfigurerAdapter` 找不到 → 已经处理
3. 循环依赖 → Spring Boot 3 默认禁止，可配置 `spring.main.allow-circular-references=true`
4. Jackson 日期序列化问题 → 检查 ObjectMapper 配置
5. 日期时间 API (JSR310) 注册问题 → 确保 jackson-datatype-jsr310 在 classpath

### 步骤 4: 运行测试
```bash
mvn test
```

### 步骤 5: 启动验证
- [ ] 应用能够正常启动
- [ ] Swagger UI 可访问: http://localhost:{port}/swagger-ui.html
- [ ] Redis 连接正常
- [ ] 数据库 CRUD 操作正常
- [ ] 登录认证功能正常
- [ ] 核心业务流程测试通过

---

## 🔧 依赖兼容性清单

| 依赖 | 原版本 | 目标版本 | 兼容性 | 备注 |
|------|--------|----------|--------|------|
| spring-boot-starter-* | 2.x | 3.3.0 | ✅ | 完全兼容 |
| mybatis-plus-boot-starter | 3.3.x | 3.5.5 | ✅ | 支持 jakarta |
| druid | 1.1.20 | druid-spring-boot-3-starter | ✅ | 专用 Spring Boot 3 版本 |
| spring-data-redis | 2.x | 3.x | ✅ | 随 Spring Boot 升级 |
| jedis | 3.x | 4.4.6 | ✅ | 兼容 Spring Data Redis 3 |
| springfox-swagger2 | 2.9.2 | 移除 | ❌ | 改用 springdoc-openapi |
| pagehelper | 1.2.5 | 2.x | ✅ | 支持 Spring Boot 3 |
| fastjson | 1.2.83 | 2.0.43 | ✅ | fastjson2 |
| hutool-all | 4.5.7 | 5.8.24 | ✅ | 支持 Spring Boot 3 |
| jjwt | 0.9.1 | 0.12.3 | ✅ | 模块化，api + impl + jackson |

---

## 📝 关键配置文件变更

### application.yml 可能需要的配置
```yaml
# Spring Boot 3 循环依赖允许（如有需要）
spring:
  main:
    allow-circular-references: true
  
  # SpringDoc 配置（可选）
  springdoc:
    swagger-ui:
      enabled: true
    api-docs:
      enabled: true
```

---

## ⚠️ 迁移注意事项

### 1. 全局替换的坑点
- **不要** 盲目替换所有 "javax" 字符串
- **必须** 只替换 import 语句中的 `javax.` 前缀
- **检查** `javax.transaction.*`、`javax.mail.*` 等特殊包是否也需要替换

### 2. MyBatis Plus 注意点
- 确保所有 Mapper 接口使用 `@Mapper` 注解或 `@MapperScan`
- 检查 `TypeHandler`、`MetaObjectHandler` 等扩展点兼容性

### 3. Jackson 序列化
- Spring Boot 3 中 Jackson 配置略有不同
- 检查日期时间格式化是否正常
- 检查 NULL 值处理策略

### 4. 日志框架
- Spring Boot 3 默认使用 Logback
- 检查 logback-spring.xml 配置兼容性

### 5. 第三方 SDK
- 微信支付、阿里云 OSS 等 SDK 可能需要升级到支持 jakarta 的版本
- 检查所有外部 HTTP 调用库的兼容性

---

## ✅ 完成标准

1. `mvn clean compile` 无编译错误
2. `mvn test` 单元测试全部通过
3. 应用启动无异常堆栈
4. 核心业务功能正常工作
5. Swagger 文档可正常访问
6. 数据库连接正常
7. Redis 缓存正常
