# CRMEB 智能搜索模块

## 功能介绍

CRMEB智能搜索模块是一个基于自然语言处理的智能搜索服务，它可以理解用户的自然语言查询，并返回相应的结果。该模块支持以下功能：

1. **订单状态查询**：用户可以查询自己的订单状态，例如"我的订单状态"、"订单123456的状态"等。
2. **物流信息查询**：用户可以查询订单的物流信息，例如"我的订单物流信息"、"订单123456的物流"等。
3. **退款状态查询**：用户可以查询订单的退款状态，例如"我的订单退款状态"、"订单123456的退款状态"等。
4. **待支付订单查询**：用户可以查询自己的待支付订单，例如"我的待支付订单"、"我有哪些待支付的订单"等。
5. **待发货订单查询**：用户可以查询自己的待发货订单，例如"我的待发货订单"、"我有哪些待发货的订单"等。
6. **待收货订单查询**：用户可以查询自己的待收货订单，例如"我的待收货订单"、"我有哪些待收货的订单"等。
7. **已完成订单查询**：用户可以查询自己的已完成订单，例如"我的已完成订单"、"我有哪些已完成的订单"等。
8. **已取消订单查询**：用户可以查询自己的已取消订单，例如"我的已取消订单"、"我有哪些已取消的订单"等。

## 技术栈

- **Spring Boot**：框架基础
- **MyBatis-Plus**：数据库访问
- **HanLP**：自然语言处理
- **FastJSON**：JSON处理
- **Lombok**：简化代码

## 模块结构

```
crmeb-search/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── zbkj/
│   │   │           └── search/
│   │   │               ├── config/          # 配置类
│   │   │               ├── controller/      # 控制器
│   │   │               ├── enums/           # 枚举类
│   │   │               ├── model/           # 模型类
│   │   │               ├── service/         # 服务接口
│   │   │               ├── service/impl/    # 服务实现
│   │   │               └── utils/           # 工具类
│   │   └── resources/
│   │       ├── hanlp/                     # HanLP配置
│   │       └── application.yml            # 应用配置
│   └── test/                              # 测试类
└── pom.xml                                 # Maven配置
```

## 核心类说明

### 1. IntentTypeEnum

意图类型枚举，定义了所有支持的用户意图类型。

### 2. IntentRecognitionResult

意图识别结果模型，包含意图类型、置信度、实体信息和原始查询文本。

### 3. IntentRecognitionService

意图识别服务接口，定义了识别用户意图的方法。

### 4. IntentRecognitionServiceImpl

意图识别服务实现，使用HanLP进行自然语言处理，识别用户的意图。

### 5. SearchService

搜索服务接口，定义了处理用户搜索请求的方法。

### 6. SearchServiceImpl

搜索服务实现，根据用户的意图调用相应的业务逻辑，返回搜索结果。

### 7. SearchController

搜索控制器，提供RESTful API接口，接收用户的搜索请求并返回结果。

### 8. SearchResponse

搜索响应模型，包含意图识别结果和搜索结果详情。

### 9. NLPUtils

自然语言处理工具类，封装了HanLP的使用方法。

## 使用方法

### 1. 启动模块

```bash
cd crmeb-search
mvn spring-boot:run
```

模块将在端口8085启动，上下文路径为/search。

### 2. 调用API

#### 智能搜索接口

**接口地址**：POST /api/search/smart

**请求参数**：
- userId：用户ID
- query：搜索查询

**响应示例**：
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "intentRecognitionResult": {
      "intentType": "ORDER_STATUS",
      "confidence": 0.95,
      "entities": {},
      "originalQuery": "我的订单状态"
    },
    "searchResult": {
      "total": 5,
      "unpaid": 1,
      "unshipped": 2,
      "unreceived": 1,
      "completed": 1,
      "cancelled": 0
    }
  }
}
```

#### 意图识别测试接口

**接口地址**：POST /api/search/intent/test

**请求参数**：
- query：搜索查询

**响应示例**：
```json
{
  "code": 200,
  "message": "识别成功",
  "data": {
    "intentType": "ORDER_STATUS",
    "confidence": 0.95,
    "entities": {},
    "originalQuery": "我的订单状态"
  }
}
```

## 扩展方法

### 1. 添加新的意图类型

在IntentTypeEnum中添加新的意图类型：

```java
public enum IntentTypeEnum {
    // 现有意图类型
    NEW_INTENT("NEW_INTENT", "新意图");

    // 构造方法和现有方法
}
```

### 2. 实现新意图的处理逻辑

在SearchServiceImpl中添加新意图的处理方法：

```java
private CommonResult<Object> handleNewIntent(Integer userId, Map<String, Object> entities) {
    // 实现新意图的处理逻辑
}
```

然后在executeSearchByIntent方法中添加新意图的分支：

```java
switch (intentType) {
    // 现有分支
    case NEW_INTENT:
        return handleNewIntent(userId, entities);
    default:
        return CommonResult.failed("不支持的意图类型");
}
```

### 3. 训练自定义模型

如果需要提高意图识别的准确率，可以训练自定义的模型。具体方法请参考HanLP的官方文档。

## 注意事项

1. 确保HanLP的配置文件和模型文件正确配置。
2. 确保数据库连接信息正确配置。
3. 确保Redis连接信息正确配置。
4. 确保crmeb-service模块已经启动，并且可以正常访问。

## 联系方式

如果您有任何问题或建议，请联系CRMEB团队：admin@crmeb.com。
