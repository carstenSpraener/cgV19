package de.spraener.nxtgen.aicb;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.chat.ChatModel;

import java.io.IOException;
import java.util.List;

public class LangChain4jClient implements LlmClient {

    private final ChatModel chatModel;
    private final String modelName;

    public LangChain4jClient(Config cfg) {
        this(cfg, System.getenv("CGV19_LLM_API_KEY"));
    }

    public LangChain4jClient(Config cfg, String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Environment variable CGV19_LLM_API_KEY is not set");
        }

        this.modelName = cfg.getModel() != null ? cfg.getModel() : "gpt-4o-mini";
        this.chatModel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(cfg.getUrl())
                .modelName(this.modelName)
                .temperature(0.0)
                .build();
    }

    @Override
    public String complete(String prompt) {
        return chatModel.chat(prompt);
    }

    @Override
    public String chat(List<ChatMessage> messages) throws IOException {
        ChatRequest request = ChatRequest.builder()
                .messages(messages)
                .build();
        ChatResponse response = chatModel.chat(request);
        return response.aiMessage().text();
    }
}
