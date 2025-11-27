package com.zbkj.ai.service;

public interface QaService {

    /**
     * 处理用户问答请求
     * @param question 用户问题
     * @param knowledgeBaseName 知识库名称
     * @return 回答内容
     */
    String answerQuestion(String question, String knowledgeBaseName);

    /**
     * 处理用户问答请求，结合业务逻辑
     * @param question 用户问题
     * @param knowledgeBaseName 知识库名称
     * @param userId 用户ID（可选）
     * @return 回答内容
     */
    String answerQuestionWithBusinessLogic(String question, String knowledgeBaseName, Integer userId);

}