package com.zbkj.ai.controller;

import com.zbkj.ai.service.KnowledgeBaseService;
import com.zbkj.ai.service.QaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
@Api(tags = "智能问答机器人")
public class AiController {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    private QaService qaService;

    @ApiOperation("上传文档到知识库")
    @PostMapping("/knowledge-base/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("knowledgeBaseName") String knowledgeBaseName) throws IOException {
        String result = knowledgeBaseService.uploadDocument(file, knowledgeBaseName);
        return ResponseEntity.ok(result);
    }

    @ApiOperation("批量上传文档到知识库")
    @PostMapping("/knowledge-base/upload/batch")
    public ResponseEntity<List<String>> uploadDocuments(@RequestParam("files") List<MultipartFile> files,
                                                       @RequestParam("knowledgeBaseName") String knowledgeBaseName) throws IOException {
        List<String> results = knowledgeBaseService.uploadDocuments(files, knowledgeBaseName);
        return ResponseEntity.ok(results);
    }

    @ApiOperation("从知识库中查询相关文档")
    @GetMapping("/knowledge-base/query")
    public ResponseEntity<List<String>> queryDocuments(@RequestParam("query") String query,
                                                       @RequestParam("knowledgeBaseName") String knowledgeBaseName,
                                                       @RequestParam(defaultValue = "5") int topK) {
        List<String> results = knowledgeBaseService.queryDocuments(query, knowledgeBaseName, topK);
        return ResponseEntity.ok(results);
    }

    @ApiOperation("创建知识库")
    @PostMapping("/knowledge-base/create")
    public ResponseEntity<String> createKnowledgeBase(@RequestParam("knowledgeBaseName") String knowledgeBaseName) {
        String result = knowledgeBaseService.createKnowledgeBase(knowledgeBaseName);
        return ResponseEntity.ok(result);
    }

    @ApiOperation("删除知识库")
    @DeleteMapping("/knowledge-base/delete")
    public ResponseEntity<String> deleteKnowledgeBase(@RequestParam("knowledgeBaseName") String knowledgeBaseName) {
        String result = knowledgeBaseService.deleteKnowledgeBase(knowledgeBaseName);
        return ResponseEntity.ok(result);
    }

    @ApiOperation("获取所有知识库")
    @GetMapping("/knowledge-base/list")
    public ResponseEntity<List<String>> getAllKnowledgeBases() {
        List<String> knowledgeBases = knowledgeBaseService.getAllKnowledgeBases();
        return ResponseEntity.ok(knowledgeBases);
    }

    @ApiOperation("智能问答")
    @PostMapping("/qa/answer")
    public ResponseEntity<String> answerQuestion(@RequestParam("question") String question,
                                                 @RequestParam("knowledgeBaseName") String knowledgeBaseName) {
        String answer = qaService.answerQuestion(question, knowledgeBaseName);
        return ResponseEntity.ok(answer);
    }

    @ApiOperation("智能问答（结合业务逻辑）")
    @PostMapping("/qa/answer/business")
    public ResponseEntity<String> answerQuestionWithBusinessLogic(@RequestParam("question") String question,
                                                                 @RequestParam("knowledgeBaseName") String knowledgeBaseName,
                                                                 @RequestParam(required = false) Integer userId) {
        String answer = qaService.answerQuestionWithBusinessLogic(question, knowledgeBaseName, userId);
        return ResponseEntity.ok(answer);
    }

}