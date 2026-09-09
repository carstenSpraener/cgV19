package de.spraener.nxtgen.aicb;

import dev.langchain4j.data.message.ChatMessage;

import java.io.IOException;
import java.util.List;

public class MockLlmClient implements LlmClient {
    @Override
    public String complete(String prompt) {
        return "// mock AI output";
    }

    @Override
    public String chat(List<ChatMessage> messages) throws IOException {
        return "// mock AI output";
    }

    @Override
    public boolean isMock() {
        return true;
    }
}
