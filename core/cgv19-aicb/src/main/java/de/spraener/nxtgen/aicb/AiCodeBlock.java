package de.spraener.nxtgen.aicb;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.spraener.nxtgen.CodeBlockImpl;
import dev.langchain4j.data.message.ChatMessage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class AiCodeBlock extends CodeBlockImpl {

    private final String generatedCode;

    private AiCodeBlock(String name, String code) {
        super(name);
        this.generatedCode = code;
    }

    public static AiCodeBlock resolve(String prompt) {
        return resolve(prompt, s -> s);
    }

    /**
     * Factory method used from Groovy/Java templates.
     * Builds the prompt, checks cache, calls LLM if needed, applies post-processing, and returns a block.
     *
     * @param prompt the prompt string built by the template
     * @param postProcessor optional post-processing function (can be null)
     * @return an AiCodeBlock containing the generated code
     */
    public static AiCodeBlock resolve(String prompt, Function<String, String> postProcessor) {
        if (postProcessor == null) {
            postProcessor = PostProcessor.defaultProcessor();
        }

        Config cfg = Config.load();
        String configHash = (cfg != null) ? cfg.configHash() : "mock";

        String cacheKey = Config.sha256(prompt + configHash);

        Optional<String> cached = Cache.get(cacheKey);
        if (cached.isPresent()) {
            return new AiCodeBlock("ai-cached-" + cacheKey.substring(0, 8), cached.get());
        }

        try {
            LlmClient client = ClientFactory.get();
            String raw = client.complete(prompt);
            String processed = postProcessor.apply(raw);

            Cache.put(cacheKey, processed);
            return new AiCodeBlock("ai-generated-" + UUID.randomUUID().toString().substring(0, 8), processed);
        } catch (IOException e) {
            String error = "// [AI generation failed: " + e.getMessage() + "]";
            return new AiCodeBlock("ai-error-" + UUID.randomUUID().toString().substring(0, 8), error);
        }
    }

    /**
     * Conversation-based overload: binds a running conversation to the given model element.
     * The full message history is sent on each call, enabling KV-caching at the LLM provider.
     *
     * @param model the ModelElement to bind the conversation to
     * @param userPrompt the new user prompt to append
     * @return an AiCodeBlock containing only the latest assistant reply
     */
    public static AiCodeBlock resolve(Object model, String userPrompt) {
        return resolve(model, userPrompt, null);
    }

    /**
     * Conversation-based overload with post-processing.
     *
     * @param model the ModelElement to bind the conversation to
     * @param userPrompt the new user prompt to append
     * @param postProcessor optional post-processing function (can be null)
     * @return an AiCodeBlock containing only the latest assistant reply
     */
    public static AiCodeBlock resolve(Object model, String userPrompt, Function<String, String> postProcessor) {
        if (postProcessor == null) {
            postProcessor = PostProcessor.defaultProcessor();
        }

        // 1. Retrieve or create the conversation attached to this model element
        AiConversation conv = AiConversation.get(model);

        // 2. Append the new user prompt
        conv.addUserMessage(userPrompt);

        // 3. Serialize the entire conversation for caching
        String serialized = serializeConversation(conv.getMessages());

        // 4. Compute cache key from full conversation + config hash
        Config cfg = Config.load();
        String configHash = (cfg != null) ? cfg.configHash() : "mock";
        String cacheKey = Config.sha256(serialized + configHash);

        // 5. Cache lookup
        Optional<String> cached = Cache.get(cacheKey);
        if (cached.isPresent()) {
            return new AiCodeBlock("ai-cached-" + cacheKey.substring(0, 8), cached.get());
        }

        // 6. LLM call with full conversation history
        try {
            LlmClient client = ClientFactory.get();
            String assistantReply = client.chat(conv.getMessages());

            // 7. Store the reply in the conversation (so future calls see full history)
            conv.addAssistantMessage(assistantReply);

            // 8. Apply post-processor
            String processed = postProcessor.apply(assistantReply);

            // 9. Cache the processed reply
            Cache.put(cacheKey, processed);

            return new AiCodeBlock("ai-generated-" + UUID.randomUUID().toString().substring(0, 8), processed);
        } catch (IOException e) {
            String error = "// [AI generation failed: " + e.getMessage() + "]";
            return new AiCodeBlock("ai-error-" + UUID.randomUUID().toString().substring(0, 8), error);
        }
    }

    /**
     * Clears the conversation bound to the given model element.
     */
    public static void clearConversation(Object model) {
        AiConversation.clear(model);
    }

    /**
     * Serializes the conversation's message list to a deterministic JSON string.
     * Uses LangChain4j's built-in JSON codec to handle the ChatMessage interface properly.
     */
    private static String serializeConversation(List<ChatMessage> messages) {
        try {
            dev.langchain4j.data.message.ChatMessageJsonCodec codec =
                    new dev.langchain4j.data.message.JacksonChatMessageJsonCodec();
            return codec.messagesToJson(messages);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize conversation for caching", e);
        }
    }

    @Override
    public String toCode() {
        return generatedCode;
    }

    public static String removeJavaFence(String code) {
        if (code == null || code.trim().isEmpty()) {
            return code;
        }

        String trimmed = code.trim();

        // Remove opening fence (```java, ```typescript, ``` etc.)
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            if (firstNewline != -1) {
                trimmed = trimmed.substring(firstNewline + 1);
            }
        }

        int lastFence = trimmed.lastIndexOf("```");
        if (lastFence != -1) {
            trimmed = trimmed.substring(0, lastFence);
        }

        return trimmed.trim();
    }
}
