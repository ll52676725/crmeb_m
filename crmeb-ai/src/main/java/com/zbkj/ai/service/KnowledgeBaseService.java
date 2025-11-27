package com.zbkj.ai.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 知识库服务接口
 * 定义文档入库、查询等核心功能
 */
public interface KnowledgeBaseService {

    /**
     * 上传文档到知识库
     * @param file 文档文件
     * @param metadata 文档元数据
     * @return 文档ID
     * @throws IOException 读取文件异常
     */
    String uploadDocument(MultipartFile file, Map<String, String> metadata) throws IOException;

    /**
     * 批量上传文档到知识库
     * @param files 文档文件列表
     * @param metadata 文档元数据
     * @return 文档ID列表
     * @throws IOException 读取文件异常
     */
    List<String> batchUploadDocuments(List<MultipartFile> files, Map<String, String> metadata) throws IOException;

    /**
     * 从知识库中查询相关文档
     * @param query 查询内容
     * @param topK 返回结果数量
     * @return 相关文档列表
     */
    List<Map<String, Object>> queryDocuments(String query, int topK);

    /**
     * 删除知识库中的文档
     * @param documentId 文档ID
     * @return 删除结果
     */
    boolean deleteDocument(String documentId);

    /**
     * 创建新的知识库
     * @param knowledgeBaseName 知识库名称
     * @param description 知识库描述
     * @return 知识库ID
     */
    String createKnowledgeBase(String knowledgeBaseName, String description);

    /**
     * 删除知识库
     * @param knowledgeBaseId 知识库ID
     * @return 删除结果
     */
    boolean deleteKnowledgeBase(String knowledgeBaseId);

    /**
     * 获取知识库列表
     * @return 知识库列表
     */
    List<Map<String, Object>> listKnowledgeBases();

}