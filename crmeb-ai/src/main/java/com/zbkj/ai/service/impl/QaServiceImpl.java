package com.zbkj.ai.service.impl;

import com.zbkj.ai.service.KnowledgeBaseService;
import com.zbkj.ai.service.QaService;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QaServiceImpl implements QaService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Override
    public String answerQuestion(String question, String knowledgeBaseName) {
        // 从知识库中查询相关文档
        List<String> relevantDocuments = knowledgeBaseService.queryDocuments(question, knowledgeBaseName, 5);
        // 构建提示词
        String promptTemplate = "你是一个智能问答机器人，请根据以下文档内容回答用户的问题。\n\n文档内容：\n{documents}\n\n用户问题：{question}\n\n回答：";
        Map<String, Object> promptParams = new HashMap<>();
        promptParams.put("documents", String.join("\n\n", relevantDocuments));
        promptParams.put("question", question);
        Prompt prompt = new PromptTemplate(promptTemplate, promptParams).create();
        // 调用大模型生成回答
        ChatResponse response = chatClient.call(prompt);
        // 提取回答内容
        return response.getResult().getOutput().getContent();
    }

    @Override
    public String answerQuestionWithBusinessLogic(String question, String knowledgeBaseName, Integer userId) {
        // 先从知识库中查询相关文档
        List<String> relevantDocuments = knowledgeBaseService.queryDocuments(question, knowledgeBaseName, 5);
        // 构建提示词，包含业务逻辑相关的信息
        String promptTemplate = "你是一个智能问答机器人，同时也是一个电商平台的客服，请根据以下文档内容和业务逻辑回答用户的问题。\n\n文档内容：\n{documents}\n\n业务逻辑提示：\n1. 如果用户询问订单相关问题，需要用户提供订单号。\n2. 如果用户询问商品相关问题，需要用户提供商品ID或商品名称。\n3. 如果用户询问物流相关问题，需要用户提供物流单号。\n4. 如果用户询问退换货相关问题，需要用户提供订单号和退换货原因。\n5. 如果用户询问优惠活动相关问题，需要告知用户当前的优惠活动信息。\n6. 如果用户询问会员相关问题，需要告知用户会员等级和权益。\n\n用户问题：{question}\n\n用户ID（可选）：{userId}\n\n回答：";
        Map<String, Object> promptParams = new HashMap<>();
        promptParams.put("documents", String.join("\n\n", relevantDocuments));
        promptParams.put("question", question);
        promptParams.put("userId", userId != null ? userId : "未提供");
        Prompt prompt = new PromptTemplate(promptTemplate, promptParams).create();
        // 调用大模型生成回答
        ChatResponse response = chatClient.call(prompt);
        // 提取回答内容
        return response.getResult().getOutput().getContent();
    }

}