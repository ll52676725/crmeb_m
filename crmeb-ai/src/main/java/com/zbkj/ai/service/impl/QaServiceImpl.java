package com.zbkj.ai.service.impl;

import com.zbkj.ai.service.KnowledgeBaseService;
import com.zbkj.ai.service.QaService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 问答服务实现类
 * 实现智能问答的核心功能
 */
@Service
public class QaServiceImpl implements QaService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    // 存储用户问答历史
    private final Map<String, List<Map<String, Object>>> userQuestionHistory = new ConcurrentHashMap<>();

    /**
     * 处理用户问答请求
     */
    @Override
    public Map<String, Object> answerQuestion(String question, String knowledgeBaseId, String userId) {
        // 从知识库中查询相关文档
        List<Map<String, Object>> relevantDocuments = knowledgeBaseService.queryDocuments(question, 5);

        // 构建提示词
        String prompt = buildPrompt(question, relevantDocuments);

        // 调用大模型生成回答
        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // 保存问答历史
        saveQuestionHistory(userId, question, answer, relevantDocuments);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("question", question);
        result.put("answer", answer);
        result.put("relevantDocuments", relevantDocuments);
        result.put("timestamp", new Date());

        return result;
    }

    /**
     * 处理用户问答请求（结合业务逻辑）
     */
    @Override
    public Map<String, Object> answerQuestionWithBusinessLogic(String question, String knowledgeBaseId, String userId, Map<String, Object> businessContext) {
        // 从知识库中查询相关文档
        List<Map<String, Object>> relevantDocuments = knowledgeBaseService.queryDocuments(question, 5);

        // 构建包含业务逻辑的提示词
        String prompt = buildPromptWithBusinessLogic(question, relevantDocuments, businessContext);

        // 调用大模型生成回答
        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // 保存问答历史
        saveQuestionHistory(userId, question, answer, relevantDocuments);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("question", question);
        result.put("answer", answer);
        result.put("relevantDocuments", relevantDocuments);
        result.put("businessContext", businessContext);
        result.put("timestamp", new Date());

        return result;
    }

    /**
     * 获取问答历史
     */
    @Override
    public Map<String, Object> getQuestionHistory(String userId, int limit) {
        List<Map<String, Object>> history = userQuestionHistory.getOrDefault(userId, new ArrayList<>());
        // 按时间倒序排列并限制数量
        List<Map<String, Object>> limitedHistory = history.stream()
                .sorted((a, b) -> ((Date) b.get("timestamp")).compareTo((Date) a.get("timestamp")))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("history", limitedHistory);
        result.put("total", history.size());

        return result;
    }

    /**
     * 清空问答历史
     */
    @Override
    public boolean clearQuestionHistory(String userId) {
        userQuestionHistory.remove(userId);
        return true;
    }

    /**
     * 构建提示词
     */
    private String buildPrompt(String question, List<Map<String, Object>> relevantDocuments) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个智能客服机器人，负责回答用户的问题。\n");
        prompt.append("请根据以下提供的知识库内容来回答用户的问题：\n\n");

        // 添加知识库内容
        for (int i = 0; i < relevantDocuments.size(); i++) {
            Map<String, Object> doc = relevantDocuments.get(i);
            prompt.append("知识库内容 " + (i + 1) + ":\n");
            prompt.append(doc.get("content") + "\n\n");
        }

        prompt.append("用户问题：" + question + "\n\n");
        prompt.append("请根据以上内容，用简洁明了的语言回答用户的问题。如果知识库中没有相关内容，请说\"抱歉，我暂时无法回答您的问题\"。\n");
        prompt.append("回答格式要求：\n");
        prompt.append("1. 直接回答问题，不要添加额外的解释\n");
        prompt.append("2. 如果需要举例，请使用具体的例子\n");
        prompt.append("3. 保持回答的专业性和准确性\n");

        return prompt.toString();
    }

    /**
     * 构建包含业务逻辑的提示词
     */
    private String buildPromptWithBusinessLogic(String question, List<Map<String, Object>> relevantDocuments, Map<String, Object> businessContext) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个智能客服机器人，负责回答用户的问题。\n");
        prompt.append("请根据以下提供的知识库内容和业务逻辑来回答用户的问题：\n\n");

        // 添加业务逻辑
        prompt.append("业务逻辑：\n");
        prompt.append("当前用户信息：" + businessContext.getOrDefault("userInfo", "未知") + "\n");
        prompt.append("当前订单信息：" + businessContext.getOrDefault("orderInfo", "无") + "\n");
        prompt.append("当前商品信息：" + businessContext.getOrDefault("productInfo", "无") + "\n");
        prompt.append("当前活动信息：" + businessContext.getOrDefault("promotionInfo", "无") + "\n\n");

        // 添加知识库内容
        for (int i = 0; i < relevantDocuments.size(); i++) {
            Map<String, Object> doc = relevantDocuments.get(i);
            prompt.append("知识库内容 " + (i + 1) + ":\n");
            prompt.append(doc.get("content") + "\n\n");
        }

        prompt.append("用户问题：" + question + "\n\n");
        prompt.append("请根据以上内容，用简洁明了的语言回答用户的问题。如果知识库中没有相关内容，请说\"抱歉，我暂时无法回答您的问题\"。\n");
        prompt.append("回答格式要求：\n");
        prompt.append("1. 直接回答问题，不要添加额外的解释\n");
        prompt.append("2. 如果需要举例，请使用具体的例子\n");
        prompt.append("3. 保持回答的专业性和准确性\n");
        prompt.append("4. 结合用户的具体情况提供个性化回答\n");

        return prompt.toString();
    }

    /**
     * 保存问答历史
     */
    private void saveQuestionHistory(String userId, String question, String answer, List<Map<String, Object>> relevantDocuments) {
        if (userId == null) {
            return; // 匿名用户不保存历史
        }

        List<Map<String, Object>> history = userQuestionHistory.computeIfAbsent(userId, k -> new ArrayList<>());
        Map<String, Object> record = new HashMap<>();
        record.put("question", question);
        record.put("answer", answer);
        record.put("relevantDocuments", relevantDocuments);
        record.put("timestamp", new Date());

        history.add(record);

        // 限制历史记录数量，最多保存100条
        if (history.size() > 100) {
            history.remove(0);
        }
    }

}