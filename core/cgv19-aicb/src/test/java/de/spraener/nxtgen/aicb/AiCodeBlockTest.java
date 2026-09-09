package de.spraener.nxtgen.aicb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class AiCodeBlockTest {

    @BeforeEach
    void setup() {
        ClientFactory.reset();
    }

    @AfterEach
    void cleanup() {
        Cache.clear();
        ClientFactory.reset();
    }

    @Test
    void cacheHit_returnsCachedValueWithoutCallingLlm() {
        String testKey = Config.sha256("test prompt" + "mock");
        Cache.put(testKey, "cached response value");

        AiCodeBlock block = AiCodeBlock.resolve("test prompt", (Function<String, String>) null);
        assertThat(block.toCode()).isEqualTo("cached response value");
    }

    @Test
    void cacheMissWithMockClient_returnsMockOutput() {
        AiCodeBlock block = AiCodeBlock.resolve("new prompt", (Function<String, String>) null);
        assertThat(block.toCode()).isEqualTo("// mock AI output");
    }

    @Test
    void postProcessor_isAppliedToGeneratedCode() {
        Function<String, String> upperCase = s -> s.toUpperCase();
        AiCodeBlock block = AiCodeBlock.resolve("test prompt", upperCase);
        assertThat(block.toCode()).isEqualTo("// MOCK AI OUTPUT");
    }

    @Test
    void secondCallWithSamePrompt_returnsCachedValue() {
        AiCodeBlock block1 = AiCodeBlock.resolve("unique prompt 123", (Function<String, String>) null);
        String firstResult = block1.toCode();

        AiCodeBlock block2 = AiCodeBlock.resolve("unique prompt 123", (Function<String, String>) null);
        String secondResult = block2.toCode();

        assertThat(firstResult).isEqualTo(secondResult);
    }

    @Test
    void nullPostProcessor_usesDefaultIdentity() {
        AiCodeBlock block = AiCodeBlock.resolve("test", (Function<String, String>) null);
        assertThat(block.toCode()).isEqualTo("// mock AI output");
    }

    @Test
    void differentPrompts_produceDifferentCacheKeys() {
        String key1 = Config.sha256("prompt A" + "mock");
        String key2 = Config.sha256("prompt B" + "mock");
        assertThat(key1).isNotEqualTo(key2);
    }
}
