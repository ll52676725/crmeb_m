# Spring Boot 3 迁移 - 编译问题排查和解决方案

---

## 🔧 已修复的关键问题

### 1. ✅ Spring Boot Parent 配置修复
- **问题**: 根 pom.xml 中的 `spring-boot-starter-parent` 被注释掉了
- **影响**: 依赖版本管理失效，插件配置缺失
- **解决方案**: 取消注释，升级到 3.3.0

### 2. ✅ Spring Security 6 配置重构
- **问题**: `WebSecurityConfigurerAdapter` 在 Spring Security 6 中已被移除
- **已修复文件**:
  - `crmeb-admin/src/main/java/com/zbkj/admin/config/WebSecurityConfig.java`
  - `crmeb-front/src/main/java/com/zbkj/front/config/CloseSecurityConfig.java`
- **解决方案**: 使用 `SecurityFilterChain` Bean + Lambda DSL

### 3. ✅ JJWT 0.12.x API 升级
- **问题**: JJWT 0.12.x 的解析器 API 有重大变更
- **已修复文件**: `crmeb-common/src/main/java/com/zbkj/common/utils/AppleUtil.java`
- **API 变更**:
  ```java
  // 旧版 (0.11.x)
  Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jwt)
  
  // 新版 (0.12.x)
  Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(jwt)
  ```

### 4. ✅ Spring Boot Validation 依赖添加
- **问题**: Spring Boot 3 不再默认包含 Validation Starter
- **已修复文件**: `crmeb-common/pom.xml`
- **添加依赖**:
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
  </dependency>
  ```

---

## 📋 可能遇到的其他问题和解决方案

### 问题 A: 循环依赖（Circular Dependency）

**错误信息**:
```
Bean with name 'xxx' has been injected into other beans [yyy]
in its raw version as part of a circular reference
```

**解决方案**:
在 `application.yml` 中添加：
```yaml
spring:
  main:
    allow-circular-references: true
```

---

### 问题 B: 日期序列化问题（JavaTimeModule）

**错误信息**:
```
Cannot construct instance of `java.time.LocalDateTime`
```

**解决方案**:
确保 Jackson 配置正确，添加 JavaTimeModule：
```java
@Bean
public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    return mapper;
}
```

---

### 问题 C: Fastjson 2 兼容性

**现象**: 序列化/反序列化行为变化

**解决方案**:
1. 检查是否使用了 Fastjson 1 的特定 API
2. 如需兼容旧版行为，考虑配置：
```java
// 在配置类中添加
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
```

---

### 问题 D: MyBatis Plus 分页插件

**现象**: 分页查询返回结果不正确

**解决方案**:
确保 MyBatis Plus 配置正确，使用新版分页插件：
```java
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    return interceptor;
}
```

---

### 问题 E: Redis 配置变更

**现象**: Redis 连接失败或序列化问题

**解决方案**:
检查 RedisTemplate 配置，确保使用正确的序列化器：
```java
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    // 配置序列化器...
    return template;
}
```

---

### 问题 F: IDE 中的 "类型转换错误" 提示

**现象**: IDE 中显示类型转换错误，但实际编译通过

**可能原因**:
1. IDE 缓存问题
2. Lombok 注解处理器未正确配置
3. Maven 依赖未完全下载

**解决方案**:
1. **清理 IDE 缓存**:
   - IntelliJ IDEA: `File` → `Invalidate Caches...` → `Invalidate and Restart`
   
2. **重新构建项目**:
   ```bash
   mvn clean install -DskipTests
   ```

3. **确保 IDE 使用 Java 17**:
   - `File` → `Project Structure` → `Project` → `SDK` = 17
   - `File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Java Compiler`
     - Project bytecode version: 17

---

## 🚀 编译步骤建议

### 步骤 1: 确保 Java 版本
```bash
java -version
# 应该显示 Java 17 或更高版本
```

### 步骤 2: 清理并重新构建
```bash
# 清理所有模块
mvn clean

# 安装依赖到本地仓库
mvn install -DskipTests

# 或者逐个模块构建
cd crmeb-common && mvn clean install -DskipTests && cd ..
cd crmeb-service && mvn clean install -DskipTests && cd ..
cd crmeb-admin && mvn clean compile -DskipTests && cd ..
cd crmeb-front && mvn clean compile -DskipTests && cd ..
```

### 步骤 3: 检查 IDE 配置
1. 确保 IDE 使用的 JDK 是 17
2. 确保 Maven 配置正确
3. 重新导入 Maven 项目

---

## 📝 关键配置检查清单

### ✅ pom.xml 检查项
- [ ] 根 pom.xml 有正确的 `spring-boot-starter-parent` (3.3.0)
- [ ] `java.version` 设置为 17
- [ ] `spring-boot-starter-validation` 依赖已添加
- [ ] MyBatis Plus 版本 >= 3.5.5
- [ ] JJWT 版本 >= 0.12.3

### ✅ Java 代码检查项
- [ ] 没有 `javax.annotation.*` 导入（应替换为 jakarta）
- [ ] 没有 `javax.servlet.*` 导入（应替换为 jakarta）
- [ ] 没有 `javax.validation.*` 导入（应替换为 jakarta）
- [ ] 没有继承 `WebSecurityConfigurerAdapter`
- [ ] JJWT 解析器使用新的 API

### ✅ IDE 配置检查项
- [ ] JDK 17 已安装并配置
- [ ] Maven 已配置使用 JDK 17
- [ ] Lombok 注解处理器已启用
- [ ] IDE 缓存已清理

---

## 💡 其他迁移注意事项

### 1. 配置文件变更
- Spring Boot 3 中部分配置属性名可能有变更
- 检查 `application.yml` / `application.properties` 中的废弃属性

### 2. 第三方 SDK 兼容性
- 微信支付、支付宝支付等 SDK 可能需要升级到支持 Spring Boot 3 的版本
- 阿里云 OSS、腾讯云 COS 等 SDK 同理

### 3. 日志框架
- Spring Boot 3 默认使用 Logback
- 确保日志配置文件兼容新版本

---

## 🆘 仍有问题？

如果仍然遇到编译问题，请：
1. 查看完整的 Maven 构建日志
2. 检查具体的错误信息和堆栈跟踪
3. 确认是否是特定模块的问题
4. 检查该模块的 pom.xml 和配置类

**常见问题定位**:
- 90% 的问题是: 依赖版本不匹配、IDE 缓存、Java 版本不对
- 9% 的问题是: 配置类未迁移、注解未更新
- 1% 的问题是: 第三方库不兼容

---

**祝迁移顺利！** 🎉
