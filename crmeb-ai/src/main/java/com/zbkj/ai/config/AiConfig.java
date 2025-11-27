package com.zbkj.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI模块配置类
 * 配置Spring AI相关组件
 */
@Configuration
public class AiConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.chat.model}")
    private String chatModel;

    @Value("${spring.ai.openai.chat.temperature}")
    private double temperature;

    /**
     * 配置OpenAI API客户端
     */
    @Bean
    public OpenAiApi openAiApi() {
        return new OpenAiApi(apiKey, baseUrl);
    }

    /**
     * 配置OpenAI聊天客户端
     */
    @Bean
    public OpenAiChatClient openAiChatClient(OpenAiApi openAiApi) {
        OpenAiChatModel chatModel = new OpenAiChatModel(openAiApi);
        chatModel.setModel(this.chatModel);
        chatModel.setTemperature(temperature);
        return new OpenAiChatClient(chatModel);
    }

    /**
     * 配置OpenAI嵌入客户端
     */
    @Bean
    public OpenAiEmbeddingClient openAiEmbeddingClient(OpenAiApi openAiApi) {
        return new OpenAiEmbeddingClient(openAiApi);
    }

    /**
     * 配置聊天客户端（带有内存支持）
     */
    @Bean
    public ChatClient chatClient(OpenAiChatClient openAiChatClient) {
        return ChatClient.builder(openAiChatClient)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(
                                new InMemoryChatMemory()))
                .build();
    }

}