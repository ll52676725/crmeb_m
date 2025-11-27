package com.zbkj.ai.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface KnowledgeBaseService {

    /**
     * 上传文档并入库
     * @param file 文档文件
     * @param knowledgeBaseName 知识库名称
     * @return 入库结果
     * @throws IOException  IO异常
     */
    String uploadDocument(MultipartFile file, String knowledgeBaseName) throws IOException;

    /**
     * 批量上传文档并入库
     * @param files 文档文件列表
     * @param knowledgeBaseName 知识库名称
     * @return 入库结果
     * @throws IOException IO异常
     */
    List<String> uploadDocuments(List<MultipartFile> files, String knowledgeBaseName) throws IOException;

    /**
     * 从知识库中查询相关文档
     * @param query 查询内容
     * @param knowledgeBaseName 知识库名称
     * @param topK 返回结果数量
     * @return 相关文档列表
     */
    List<String> queryDocuments(String query, String knowledgeBaseName, int topK);

    /**
     * 创建知识库
     * @param knowledgeBaseName 知识库名称
     * @return 创建结果
     */
    String createKnowledgeBase(String knowledgeBaseName);

    /**
     * 删除知识库
     * @param knowledgeBaseName 知识库名称
     * @return 删除结果
     */
    String deleteKnowledgeBase(String knowledgeBaseName);

    /**
     * 获取所有知识库名称
     * @return 知识库名称列表
     */
    List<String> getAllKnowledgeBases();

}