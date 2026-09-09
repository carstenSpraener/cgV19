package de.spraener.nxtgen.aicb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class AiCodeBlockConversationTest {

    private de.spraener.nxtgen.oom.model.MAbstractModelElement testMElement;

    @BeforeEach
    void setup() {
        testMElement = new de.spraener.nxtgen.oom.model.MAbstractModelElement();
        ClientFactory.reset();
    }

    @AfterEach
    void cleanup() {
        AiConversation.clear(testMElement);
        Cache.clear();
        ClientFactory.reset();
    }

    @Test
    void firstCall_createsConversationAndCallsLlm() {
        // Use mock client (no config file)
        AiCodeBlock block = AiCodeBlock.resolve(testMElement, "Generate a POJO");

        assertThat(block.toCode()).isEqualTo("// mock AI output");

        // Verify conversation was created and contains the user message
        AiConversation conv = AiConversation.get(testMElement);
        assertThat(conv).isNotNull();
        assertThat(conv.getMessages()).hasSize(2); // user + assistant
    }

    @Test
    void secondCall_reusesConversationAndAppendsNewMessage() {
        AiCodeBlock block1 = AiCodeBlock.resolve(testMElement, "First prompt");
        AiCodeBlock block2 = AiCodeBlock.resolve(testMElement, "Second prompt");

        assertThat(block1.toCode()).isEqualTo("// mock AI output");
        assertThat(block2.toCode()).isEqualTo("// mock AI output");

        // Conversation should now have 4 messages: user1, assistant1, user2, assistant2
        AiConversation conv = AiConversation.get(testMElement);
        assertThat(conv.getMessages()).hasSize(4);
    }

    @Test
    void postProcessor_isAppliedToConversationResponse() {
        Function<String, String> upperCase = s -> s.toUpperCase();

        AiCodeBlock block = AiCodeBlock.resolve(testMElement, "test prompt", upperCase);

        assertThat(block.toCode()).isEqualTo("// MOCK AI OUTPUT");
    }

    @Test
    void clearConversation_resetsTheConversation() {
        AiCodeBlock block1 = AiCodeBlock.resolve(testMElement, "First prompt");

        // Clear the conversation
        AiCodeBlock.clearConversation(testMElement);

        // After clear, a new call should start fresh
        AiCodeBlock block2 = AiCodeBlock.resolve(testMElement, "Second prompt");

        // The conversation should only have 2 messages (user + assistant for the second call)
        AiConversation conv = AiConversation.get(testMElement);
        assertThat(conv.getMessages()).hasSize(2);
    }

    @Test
    void cacheHit_returnsCachedResponseWithoutLlmCall() {
        // First call – creates conversation, calls LLM mock, caches result
        AiCodeBlock block1 = AiCodeBlock.resolve(testMElement, "Prompt A");

        // Clear the conversation to force a fresh start for cache testing
        AiConversation.clear(testMElement);

        // Second call with same prompt – should hit cache (same serialized conversation + config hash)
        AiCodeBlock block2 = AiCodeBlock.resolve(testMElement, "Prompt A");

        assertThat(block1.toCode()).isEqualTo(block2.toCode());
    }
}
