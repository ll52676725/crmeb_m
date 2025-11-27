package com.zbkj.ai.service;

import java.util.Map;

/**
 * 问答服务接口
 * 定义智能问答的核心功能
 */
public interface QaService {

    /**
     * 处理用户问答请求
     * @param question 用户问题
     * @param knowledgeBaseId 知识库ID
     * @param userId 用户ID（可选）
     * @return 回答结果
     */
    Map<String, Object> answerQuestion(String question, String knowledgeBaseId, String userId);

    /**
     * 处理用户问答请求（结合业务逻辑）
     * @param question 用户问题
     * @param knowledgeBaseId 知识库ID
     * @param userId 用户ID（可选）
     * @param businessContext 业务上下文
     * @return 回答结果
     */
    Map<String, Object> answerQuestionWithBusinessLogic(String question, String knowledgeBaseId, String userId, Map<String, Object> businessContext);

    /**
     * 获取问答历史
     * @param userId 用户ID
     * @param limit 历史记录数量
     * @return 问答历史列表
     */
    Map<String, Object> getQuestionHistory(String userId, int limit);

    /**
     * 清空问答历史
     * @param userId 用户ID
     * @return 清空结果
     */
    boolean clearQuestionHistory(String userId);

}