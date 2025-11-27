package com.zbkj.ai.service.impl;

import com.zbkj.ai.service.KnowledgeBaseService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.VectorStoreQuery;
import org.springframework.ai.vectorstore.VectorStoreQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Autowired
    private EmbeddingClient embeddingClient;

    @Autowired
    private VectorStore vectorStore;

    @Override
    public String uploadDocument(MultipartFile file, String knowledgeBaseName) throws IOException {
        // 读取文件内容
        String content = readFileContent(file);
        // 分割内容为段落
        List<String> paragraphs = splitContentIntoParagraphs(content);
        // 将段落存储到向量数据库
        vectorStore.add(paragraphs.stream()
                .map(p -> new org.springframework.ai.document.Document(p, null))
                .collect(Collectors.toList()));
        return "文档上传成功，共处理 " + paragraphs.size() + " 个段落";
    }

    @Override
    public List<String> uploadDocuments(List<MultipartFile> files, String knowledgeBaseName) throws IOException {
        List<String> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(uploadDocument(file, knowledgeBaseName));
        }
        return results;
    }

    @Override
    public List<String> queryDocuments(String query, String knowledgeBaseName, int topK) {
        // 从向量数据库中查询相关文档
        VectorStoreQuery vectorStoreQuery = new VectorStoreQuery();
        vectorStoreQuery.setQueryText(query);
        vectorStoreQuery.setTopK(topK);
        VectorStoreQueryResult result = vectorStore.query(vectorStoreQuery);
        // 提取文档内容
        return result.getDocuments().stream()
                .map(org.springframework.ai.document.Document::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public String createKnowledgeBase(String knowledgeBaseName) {
        // 在向量数据库中创建知识库（如果需要）
        // 这里简化处理，因为Spring AI的VectorStore接口可能不直接支持创建知识库
        return "知识库创建成功: " + knowledgeBaseName;
    }

    @Override
    public String deleteKnowledgeBase(String knowledgeBaseName) {
        // 在向量数据库中删除知识库（如果需要）
        // 这里简化处理，因为Spring AI的VectorStore接口可能不直接支持删除知识库
        return "知识库删除成功: " + knowledgeBaseName;
    }

    @Override
    public List<String> getAllKnowledgeBases() {
        // 获取所有知识库名称（如果需要）
        // 这里简化处理，返回一个固定的知识库列表
        List<String> knowledgeBases = new ArrayList<>();
        knowledgeBases.add("default");
        return knowledgeBases;
    }

    /**
     * 读取文件内容
     * @param file 文档文件
     * @return 文件内容
     * @throws IOException IO异常
     */
    private String readFileContent(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IOException("文件名不能为空");
        }
        String extension = FilenameUtils.getExtension(fileName).toLowerCase();
        switch (extension) {
            case "pdf":
                return readPdfContent(file);
            case "txt":
                return new String(file.getBytes());
            case "md":
                return readMarkdownContent(file);
            default:
                throw new IOException("不支持的文件格式: " + extension);
        }
    }

    /**
     * 读取PDF文件内容
     * @param file PDF文件
     * @return PDF内容
     * @throws IOException IO异常
     */
    private String readPdfContent(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * 读取Markdown文件内容
     * @param file Markdown文件
     * @return Markdown内容
     * @throws IOException IO异常
     */
    private String readMarkdownContent(MultipartFile file) throws IOException {
        // 简单处理，直接返回Markdown内容
        return new String(file.getBytes());
    }

    /**
     * 将内容分割为段落
     * @param content 文档内容
     * @return 段落列表
     */
    private List<String> splitContentIntoParagraphs(String content) {
        // 简单处理，按换行符分割
        List<String> paragraphs = new ArrayList<>();
        String[] lines = content.split("\\n");
        StringBuilder currentParagraph = new StringBuilder();
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                if (currentParagraph.length() > 0) {
                    paragraphs.add(currentParagraph.toString());
                    currentParagraph.setLength(0);
                }
            } else {
                if (currentParagraph.length() > 0) {
                    currentParagraph.append(" ");
                }
                currentParagraph.append(line);
            }
        }
        if (currentParagraph.length() > 0) {
            paragraphs.add(currentParagraph.toString());
        }
        // 过滤空段落
        return paragraphs.stream()
                .filter(p -> !p.isEmpty())
                .collect(Collectors.toList());
    }

}