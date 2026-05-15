# Spring Boot 2.2.6 到 3.3.x 迁移指南

## 一、迁移概述

### 1.1 迁移目标
- **Spring Boot**: 2.2.6.RELEASE → 3.3.x
- **Java**: 8 → 17
- **javax**: 全部迁移到 jakarta
- **Spring Security**: 迁移到 Lambda DSL
- **Swagger**: SpringFox → SpringDoc OpenAPI

### 1.2 迁移步骤分解（分步执行，每步可编译运行）

| 步骤 | 内容 | 预计风险 |
|------|------|----------|
| 1 | 升级 Java 版本到 17，升级 Maven 插件 | 低 |
| 2 | 升级 Spring Boot 到 3.3.x，调整依赖版本 | 中 |
| 3 | 全局替换 javax → jakarta 包 | 高 |
| 4 | 重构 Spring Security 配置 | 中 |
| 5 | 替换 Swagger SpringFox 为 SpringDoc | 中 |
| 6 | 升级 MyBatis Plus 及相关依赖 | 中 |
| 7 | 调整 Redis 配置，处理 Jedis 迁移 | 低 |
| 8 | 更新 @ConfigurationProperties 配置 | 低 |
| 9 | 编译验证并修复问题 | 高 |

---

## 二、依赖兼容性清单

### 2.1 核心依赖升级

| 依赖 | 当前版本 | 目标版本 | 兼容性说明 |
|------|----------|----------|------------|
| spring-boot-starter-parent | 2.2.6.RELEASE | 3.3.x | ✅ 直接升级 |
| mybatis-plus-boot-starter | 3.3.1 | 3.5.5 | ✅ 兼容，需处理 jakarta |
| mybatis-plus-generator | 3.3.1 | 3.5.5 | ✅ 兼容 |
| pagehelper-spring-boot-starter | 1.2.5 | 2.1.0 | ✅ 支持 Spring Boot 3 |
| springfox-swagger2 | 2.9.2 | **移除** | ❌ SpringFox 不支持 Spring Boot 3，改用 SpringDoc |
| swagger-bootstrap-ui | 1.9.3 | **移除** | ❌ 改用 springdoc-openapi-starter-webmvc-ui |
| spring-boot-starter-data-redis | 2.2.0 | 3.3.x | ✅ 跟随 Spring Boot |
| jedis | 3.1.0 | 4.4.6 | ✅ 兼容 Spring Data Redis 3.x |
| validation-api | 1.1.0.Final | **移除** | ✅ 使用 Spring Boot 3 自带的 jakarta.validation |
| druid | 1.1.20 | 1.2.20 | ✅ 支持 Spring Boot 3 |
| mysql-connector-java | 8.0.33 | 8.0.33 | ✅ 兼容 |
| fastjson | 1.2.83 | 2.0.43 | ⚠️ 建议升级，支持 jdk17 |
| hutool-all | 4.5.7 | 5.8.24 | ✅ 支持 Spring Boot 3 |
| jjwt | 0.9.1 | 0.12.3 | ✅ 新版本支持 jakarta |
| xstream | 1.4.18 | 1.4.20 | ✅ 兼容 |

### 2.2 新增依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| springdoc-openapi-starter-webmvc-ui | 2.5.0 | 替代 SpringFox Swagger |

### 2.3 移除依赖

- springfox-swagger2
- swagger-models
- swagger-bootstrap-ui
- validation-api (javax)

---

## 三、javax → jakarta 包映射清单

### 3.1 核心包替换

| 旧包 (javax) | 新包 (jakarta) | 说明 |
|--------------|----------------|------|
| javax.servlet | jakarta.servlet | Servlet API |
| javax.servlet.http | jakarta.servlet.http | HTTP Servlet |
| javax.validation | jakarta.validation | 校验 API |
| javax.validation.constraints | jakarta.validation.constraints | 校验注解 |
| javax.annotation | jakarta.annotation | 通用注解 |
| javax.annotation.PostConstruct | jakarta.annotation.PostConstruct | 生命周期注解 |
| javax.annotation.PreDestroy | jakarta.annotation.PreDestroy | 生命周期注解 |
| javax.annotation.Resource | jakarta.annotation.Resource | 资源注入 |
| javax.persistence | jakarta.persistence | JPA（本项目可能不用） |
| javax.xml.bind | jakarta.xml.bind | JAXB（如需要单独引入） |

### 3.2 注意陷阱

1. **WXPayXmlUtil 和 XmlUtil 中的 JAXB**:
   - Spring Boot 3 不再包含 JAXB，需要显式引入 `jakarta.xml.bind-api` 和 `jaxb-runtime`
   - 或者重构使用其他 XML 序列化方式

2. **Validation 注解位置变化**:
   - `@Valid` 从 `javax.validation.Valid` → `jakarta.validation.Valid`
   - `@NotEmpty`, `@NotBlank`, `@NotNull` 等都在 jakarta 包下

3. **Druid 配置**:
   - 新版 Druid 已支持 jakarta 包

---

## 四、Spring Security 迁移

### 4.1 主要变化

| 旧写法 | 新写法 |
|--------|--------|
| `extends WebSecurityConfigurerAdapter` | 移除继承，使用 `@Bean` 配置 |
| `@EnableGlobalMethodSecurity` | `@EnableMethodSecurity` |
| `configure(HttpSecurity http)` | `SecurityFilterChain filterChain(HttpSecurity http)` |
| `configure(AuthenticationManagerBuilder auth)` | 注册为独立 Bean |

### 4.2 迁移要点

1. 移除 `WebSecurityConfigurerAdapter` 继承
2. 使用 Lambda DSL 配置 `HttpSecurity`
3. `AuthenticationManager` 需要显式暴露
4. `antMatchers()` 替换为 `requestMatchers()`

---

## 五、SpringDoc OpenAPI 迁移

### 5.1 注解映射

| SpringFox 注解 | SpringDoc 注解 |
|----------------|----------------|
| `@Api` | `@Tag` |
| `@ApiOperation` | `@Operation` |
| `@ApiParam` | `@Parameter` |
| `@ApiModel` | `@Schema` |
| `@ApiModelProperty` | `@Schema` |
| `@ApiIgnore` | `@Parameter(hidden = true)` 或 `@Hidden` |
| `@ApiImplicitParam` | `@Parameter` |
| `@ApiImplicitParams` | `@Parameters` |

### 5.2 配置变化

- 移除 `@EnableSwagger2`
- 文档路径从 `/doc.html`, `/swagger-ui.html` 变为 `/swagger-ui.html`
- API 文档从 `/v2/api-docs` 变为 `/v3/api-docs`

---

## 六、配置文件变化

### 6.1 @ConfigurationProperties 变化

Spring Boot 3 推荐使用 `@ConfigurationPropertiesScan` 替代 `@EnableConfigurationProperties`

推荐写法：
```java
@ConfigurationProperties(prefix = "crmeb")
public class CrmebConfig {
    // fields...
}
```

主启动类添加：
```java
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.zbkj")
public class CrmebAdminApplication {
    // ...
}
```

---

## 七、常见问题与解决方案

### 7.1 编译错误

1. **找不到 javax.servlet 包**
   - 确保已替换为 jakarta.servlet
   - 检查所有 filter、servlet 相关类

2. **NoClassDefFoundError: javax/xml/bind/JAXBException**
   - 方案一：添加 JAXB 依赖
   - 方案二：重构 XML 处理代码使用 Jackson XML

3. **Spring Security 方法找不到**
   - 使用新的 Lambda DSL 语法
   - `antMatchers()` → `requestMatchers()`

### 7.2 运行时问题

1. **循环依赖问题**
   - Spring Boot 3 默认禁止循环依赖
   - 重构代码消除循环依赖，或配置 `spring.main.allow-circular-references=true`

2. **Redis 连接问题**
   - 检查 Jedis 版本兼容性
   - Spring Data Redis 3.x API 变化

3. **日期序列化问题**
   - Jackson 在 Spring Boot 3 中默认配置可能变化
   - 检查 `LocalDateTime` 等日期类型序列化

---

## 八、验证清单

每步迁移完成后需验证：

- [ ] Maven 编译成功：`mvn clean compile`
- [ ] 无 javax 包残留：`grep -r "javax\." src/`
- [ ] 应用能正常启动
- [ ] Swagger 文档可访问
- [ ] Redis 连接正常
- [ ] 数据库 CRUD 操作正常
- [ ] 登录认证功能正常
- [ ] 核心业务流程测试通过
