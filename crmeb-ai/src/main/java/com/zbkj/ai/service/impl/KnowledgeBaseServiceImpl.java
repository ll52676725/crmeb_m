package com.zbkj.ai.service.impl;

import com.zbkj.ai.service.KnowledgeBaseService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 知识库服务实现类
 * 实现文档入库、查询等核心功能
 */
@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private EmbeddingClient embeddingClient;

    @Value("${ai.knowledge-base.chunk-size}")
    private int chunkSize;

    @Value("${ai.knowledge-base.chunk-overlap}")
    private int chunkOverlap;

    /**
     * 上传文档到知识库
     */
    @Override
    public String uploadDocument(MultipartFile file, Map<String, String> metadata) throws IOException {
        String content = extractTextFromFile(file);
        List<Document> documents = splitTextIntoChunks(content, metadata, file.getOriginalFilename());
        vectorStore.add(documents);
        return UUID.randomUUID().toString();
    }

    /**
     * 批量上传文档到知识库
     */
    @Override
    public List<String> batchUploadDocuments(List<MultipartFile> files, Map<String, String> metadata) throws IOException {
        List<String> documentIds = new ArrayList<>();
        for (MultipartFile file : files) {
            String documentId = uploadDocument(file, metadata);
            documentIds.add(documentId);
        }
        return documentIds;
    }

    /**
     * 从知识库中查询相关文档
     */
    @Override
    public List<Map<String, Object>> queryDocuments(String query, int topK) {
        List<Document> similarDocuments = vectorStore.similaritySearch(query, topK);
        return similarDocuments.stream()
                .map(doc -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", doc.getId());
                    result.put("content", doc.getContent());
                    result.put("metadata", doc.getMetadata());
                    result.put("score", doc.getMetadata().getOrDefault("score", 0.0));
                    return result;
                })
                .collect(Collectors.toList());
    }

    /**
     * 删除知识库中的文档
     */
    @Override
    public boolean deleteDocument(String documentId) {
        vectorStore.delete(documentId);
        return true;
    }

    /**
     * 创建新的知识库
     */
    @Override
    public String createKnowledgeBase(String knowledgeBaseName, String description) {
        // 在实际实现中，这里应该创建一个新的向量存储或索引
        String knowledgeBaseId = UUID.randomUUID().toString();
        // 可以将知识库信息存储到数据库中
        return knowledgeBaseId;
    }

    /**
     * 删除知识库
     */
    @Override
    public boolean deleteKnowledgeBase(String knowledgeBaseId) {
        // 在实际实现中，这里应该删除对应的向量存储或索引
        return true;
    }

    /**
     * 获取知识库列表
     */
    @Override
    public List<Map<String, Object>> listKnowledgeBases() {
        // 在实际实现中，这里应该从数据库中查询知识库列表
        List<Map<String, Object>> knowledgeBases = new ArrayList<>();
        Map<String, Object> defaultKB = new HashMap<>();
        defaultKB.put("id", "default");
        defaultKB.put("name", "默认知识库");
        defaultKB.put("description", "系统默认知识库");
        knowledgeBases.add(defaultKB);
        return knowledgeBases;
    }

    /**
     * 从文件中提取文本内容
     */
    private String extractTextFromFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        if (fileName.endsWith(".pdf")) {
            return extractTextFromPdf(file);
        } else if (fileName.endsWith(".txt") || fileName.endsWith(".md")) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            return extractTextFromWord(file);
        } else {
            throw new IllegalArgumentException("不支持的文件格式: " + fileName);
        }
    }

    /**
     * 从PDF文件中提取文本
     */
    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * 从Word文件中提取文本
     */
    private String extractTextFromWord(MultipartFile file) throws IOException {
        // 使用Aspose.Words提取Word文档内容
        // 注意：需要添加Aspose.Words依赖
        /*
        com.aspose.words.Document doc = new com.aspose.words.Document(file.getInputStream());
        return doc.getText();
        */
        throw new UnsupportedOperationException("Word文档解析功能需要额外配置");
    }

    /**
     * 将文本分割成块
     */
    private List<Document> splitTextIntoChunks(String content, Map<String, String> metadata, String filename) {
        List<Document> documents = new ArrayList<>();
        int start = 0;
        int contentLength = content.length();

        while (start < contentLength) {
            int end = Math.min(start + chunkSize, contentLength);
            // 尝试在句子边界分割
            if (end < contentLength) {
                int lastPeriod = content.lastIndexOf('.', end);
                int lastQuestion = content.lastIndexOf('?', end);
                int lastExclamation = content.lastIndexOf('!', end);
                int lastSplit = Math.max(Math.max(lastPeriod, lastQuestion), lastExclamation);
                if (lastSplit > start + chunkOverlap) {
                    end = lastSplit + 1;
                }
            }

            String chunk = content.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                Map<String, Object> chunkMetadata = new HashMap<>(metadata);
                chunkMetadata.put("filename", filename);
                chunkMetadata.put("chunkIndex", start / chunkSize);
                documents.add(new Document(chunk, chunkMetadata));
            }

            start = end - chunkOverlap;
        }

        return documents;
    }

}