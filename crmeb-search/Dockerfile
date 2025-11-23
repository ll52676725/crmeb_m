# 使用Java 8作为基础镜像
FROM openjdk:8-jdk-alpine

# 维护者信息
MAINTAINER CRMEB Team <admin@crmeb.com>

# 设置工作目录
WORKDIR /app

# 复制jar包到工作目录
COPY target/crmeb-search-0.0.1-SNAPSHOT.jar /app/crmeb-search.jar

# 复制HanLP配置文件和模型文件
COPY src/main/resources/hanlp /app/hanlp

# 暴露端口
EXPOSE 8085

# 启动命令
ENTRYPOINT ["java", "-jar", "crmeb-search.jar"]
