package com.zbkj.ai.controller;

import com.zbkj.ai.service.KnowledgeBaseService;
import com.zbkj.ai.service.QaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * AI模块控制器
 * 提供智能问答和知识库管理的REST API接口
 */
@RestController
@RequestMapping("/api")
@Api(tags = "智能问答机器人")
public class AiController {

    @Autowired
    private QaService qaService;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    /**
     * 智能问答接口
     */
    @PostMapping("/qa/answer")
    @ApiOperation(value = "智能问答", notes = "根据用户问题返回回答")
    public Map<String, Object> answerQuestion(
            @ApiParam(value = "用户问题", required = true) @RequestParam String question,
            @ApiParam(value = "知识库ID", required = false) @RequestParam(required = false) String knowledgeBaseId,
            @ApiParam(value = "用户ID", required = false) @RequestParam(required = false) String userId) {
        return qaService.answerQuestion(question, knowledgeBaseId, userId);
    }

    /**
     * 带业务逻辑的智能问答接口
     */
    @PostMapping("/qa/answer-with-business")
    @ApiOperation(value = "带业务逻辑的智能问答", notes = "结合业务上下文返回回答")
    public Map<String, Object> answerQuestionWithBusinessLogic(
            @ApiParam(value = "用户问题", required = true) @RequestParam String question,
            @ApiParam(value = "知识库ID", required = false) @RequestParam(required = false) String knowledgeBaseId,
            @ApiParam(value = "用户ID", required = false) @RequestParam(required = false) String userId,
            @ApiParam(value = "业务上下文", required = false) @RequestBody(required = false) Map<String, Object> businessContext) {
        return qaService.answerQuestionWithBusinessLogic(question, knowledgeBaseId, userId, businessContext);
    }

    /**
     * 获取问答历史
     */
    @GetMapping("/qa/history")
    @ApiOperation(value = "获取问答历史", notes = "获取用户的问答历史记录")
    public Map<String, Object> getQuestionHistory(
            @ApiParam(value = "用户ID", required = true) @RequestParam String userId,
            @ApiParam(value = "历史记录数量", required = false, defaultValue = "10") @RequestParam(required = false, defaultValue = "10") int limit) {
        return qaService.getQuestionHistory(userId, limit);
    }

    /**
     * 清空问答历史
     */
    @DeleteMapping("/qa/history")
    @ApiOperation(value = "清空问答历史", notes = "清空用户的问答历史记录")
    public boolean clearQuestionHistory(
            @ApiParam(value = "用户ID", required = true) @RequestParam String userId) {
        return qaService.clearQuestionHistory(userId);
    }

    /**
     * 上传文档到知识库
     */
    @PostMapping("/knowledge-base/upload")
    @ApiOperation(value = "上传文档到知识库", notes = "支持PDF、TXT、MD等格式")
    public String uploadDocument(
            @ApiParam(value = "文档文件", required = true) @RequestParam MultipartFile file,
            @ApiParam(value = "文档元数据", required = false) @RequestParam(required = false) Map<String, String> metadata) throws IOException {
        return knowledgeBaseService.uploadDocument(file, metadata);
    }

    /**
     * 批量上传文档到知识库
     */
    @PostMapping("/knowledge-base/batch-upload")
    @ApiOperation(value = "批量上传文档到知识库", notes = "支持PDF、TXT、MD等格式")
    public List<String> batchUploadDocuments(
            @ApiParam(value = "文档文件列表", required = true) @RequestParam List<MultipartFile> files,
            @ApiParam(value = "文档元数据", required = false) @RequestParam(required = false) Map<String, String> metadata) throws IOException {
        return knowledgeBaseService.batchUploadDocuments(files, metadata);
    }

    /**
     * 查询知识库文档
     */
    @GetMapping("/knowledge-base/query")
    @ApiOperation(value = "查询知识库文档", notes = "根据关键词查询相关文档")
    public List<Map<String, Object>> queryDocuments(
            @ApiParam(value = "查询关键词", required = true) @RequestParam String query,
            @ApiParam(value = "返回结果数量", required = false, defaultValue = "5") @RequestParam(required = false, defaultValue = "5") int topK) {
        return knowledgeBaseService.queryDocuments(query, topK);
    }

    /**
     * 删除知识库文档
     */
    @DeleteMapping("/knowledge-base/document/{documentId}")
    @ApiOperation(value = "删除知识库文档", notes = "根据文档ID删除文档")
    public boolean deleteDocument(
            @ApiParam(value = "文档ID", required = true) @PathVariable String documentId) {
        return knowledgeBaseService.deleteDocument(documentId);
    }

    /**
     * 创建知识库
     */
    @PostMapping("/knowledge-base")
    @ApiOperation(value = "创建知识库", notes = "创建新的知识库")
    public String createKnowledgeBase(
            @ApiParam(value = "知识库名称", required = true) @RequestParam String name,
            @ApiParam(value = "知识库描述", required = false) @RequestParam(required = false) String description) {
        return knowledgeBaseService.createKnowledgeBase(name, description);
    }

    /**
     * 删除知识库
     */
    @DeleteMapping("/knowledge-base/{knowledgeBaseId}")
    @ApiOperation(value = "删除知识库", notes = "根据知识库ID删除知识库")
    public boolean deleteKnowledgeBase(
            @ApiParam(value = "知识库ID", required = true) @PathVariable String knowledgeBaseId) {
        return knowledgeBaseService.deleteKnowledgeBase(knowledgeBaseId);
    }

    /**
     * 获取知识库列表
     */
    @GetMapping("/knowledge-base/list")
    @ApiOperation(value = "获取知识库列表", notes = "获取所有知识库列表")
    public List<Map<String, Object>> listKnowledgeBases() {
        return knowledgeBaseService.listKnowledgeBases();
    }

}