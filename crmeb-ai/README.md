# Crmeb AI 智能问答机器人模块

## 功能介绍

Crmeb AI模块是基于Spring AI构建的智能问答机器人，提供以下核心功能：

### 1. 智能问答
- 基于大模型的自然语言理解
- 支持上下文对话
- 结合知识库内容回答
- 支持业务逻辑集成

### 2. 知识库管理
- 文档上传（支持PDF、TXT、MD等格式）
- 批量文档处理
- 文档向量存储
- 语义搜索
- 知识库创建和删除

### 3. 问答历史
- 用户问答历史记录
- 历史记录查询
- 历史记录清空

## 技术栈

- **Spring Boot 2.2.6.RELEASE** - 基础框架
- **Spring AI 0.8.1** - AI功能集成
- **OpenAI API** - 大模型支持
- **PgVector** - 向量数据库
- **PDFBox** - PDF文档处理
- **Swagger** - API文档

## 快速开始

### 1. 配置环境变量

```bash
# OpenAI API配置
OPENAI_API_KEY=your-openai-api-key
OPENAI_BASE_URL=https://api.openai.com/v1

# PostgreSQL数据库配置
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/crmeb_ai
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
```

### 2. 启动服务

```bash
# 编译项目
mvn compile

# 启动AI模块
mvn spring-boot:run -pl crmeb-ai
```

### 3. 访问API文档

启动成功后，访问以下地址查看API文档：

```
http://localhost:8083/ai/swagger-ui.html
```

## API使用示例

### 智能问答

```bash
curl -X POST "http://localhost:8083/ai/api/qa/answer" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "question=如何购买商品&userId=user123"
```

### 上传文档到知识库

```bash
curl -X POST "http://localhost:8083/ai/api/knowledge-base/upload" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@product_manual.pdf" \
  -F "metadata[category]=product"
```

### 查询相关文档

```bash
curl -X GET "http://localhost:8083/ai/api/knowledge-base/query?query=商品购买流程&topK=3"
```

## 项目结构

```
crmeb-ai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/zbkj/ai/
│   │   │       ├── config/       # 配置类
│   │   │       ├── controller/   # 控制器
│   │   │       ├── service/      # 服务接口
│   │   │       └── service/impl/ # 服务实现
│   │   └── resources/
│   │       └── application.yml   # 配置文件
│   └── test/                     # 测试代码
├── pom.xml                       # Maven配置
└── README.md                     # 项目说明
```

## 配置说明

### Spring AI配置

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      base-url: ${OPENAI_BASE_URL}
      chat:
        model: gpt-3.5-turbo
        temperature: 0.7
      embedding:
        model: text-embedding-ada-002
    vectorstore:
      pgvector:
        enabled: true
        datasource:
          url: jdbc:postgresql://localhost:5432/crmeb_ai
          username: postgres
          password: password
        index-type: HNSW
        dimensions: 1536
```

### 知识库配置

```yaml
ai:
  knowledge-base:
    chunk-size: 1000        # 文档分块大小
    chunk-overlap: 200      # 块重叠大小
    document-path: /data/ai/documents # 文档存储路径
```

## 扩展功能

### 1. 支持更多文档格式

当前支持PDF、TXT、MD格式，可扩展支持：
- Word文档（.doc, .docx）
- Excel表格（.xls, .xlsx）
- PowerPoint演示文稿（.ppt, .pptx）

### 2. 多知识库支持

当前支持单个知识库，可扩展支持：
- 多个知识库管理
- 知识库权限控制
- 知识库之间的关联

### 3. 业务逻辑集成

可与现有业务系统集成：
- 用户信息查询
- 订单状态查询
- 商品信息查询
- 促销活动查询

### 4. 多语言支持

可扩展支持：
- 中文、英文等多语言
- 自动语言检测
- 多语言知识库

## 注意事项

1. **API密钥安全**：请妥善保管OpenAI API密钥，不要泄露到代码仓库中
2. **数据库配置**：需要配置PostgreSQL数据库并启用PgVector扩展
3. **文档处理**：大文件处理可能需要较长时间，建议使用异步处理
4. **性能优化**：对于大规模知识库，建议优化向量存储和查询性能

## 许可证

MIT License