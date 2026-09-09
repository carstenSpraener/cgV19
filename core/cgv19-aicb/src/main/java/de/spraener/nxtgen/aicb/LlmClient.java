package de.spraener.nxtgen.aicb;

import dev.langchain4j.data.message.ChatMessage;

import java.io.IOException;
import java.util.List;

public interface LlmClient {
    String complete(String prompt) throws IOException;

    /**
     * Sends a full conversation history to the LLM and returns the assistant's reply.
     */
    String chat(List<ChatMessage> messages) throws IOException;

    /**
     * Returns true if this is a mock implementation.
     */
    default boolean isMock() {
        return false;
    }
}
