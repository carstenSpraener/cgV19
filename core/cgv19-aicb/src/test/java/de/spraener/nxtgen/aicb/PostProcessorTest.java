package de.spraener.nxtgen.aicb;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostProcessorTest {

    @Test
    void defaultProcessor_returnsInputUnchanged() {
        PostProcessor pp = PostProcessor.defaultProcessor();
        assertThat(pp.apply("test input")).isEqualTo("test input");
    }

    @Test
    void fromConfig_withNullConfig_returnsDefaultProcessor() {
        PostProcessor pp = PostProcessor.fromConfig(null);
        assertThat(pp.apply("test")).isEqualTo("test");
    }

    @Test
    void fromConfig_withCustomClass_appliesTransformation() {
        Config cfg = new Config();
        cfg.setPostProcessorClass(PostProcessorTest.UpperCaseProcessor.class.getName());
        PostProcessor pp = PostProcessor.fromConfig(cfg);
        assertThat(pp.apply("hello")).isEqualTo("HELLO");
    }

    @Test
    void fromConfig_withInvalidClass_throwsException() {
        Config cfg = new Config();
        cfg.setPostProcessorClass("non.existent.Class");
        assertThatThrownBy(() -> PostProcessor.fromConfig(cfg))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to load PostProcessor");
    }

    // Test implementation for custom processor
    public static class UpperCaseProcessor implements PostProcessor {
        @Override
        public String apply(String s) {
            return s.toUpperCase();
        }
    }
}
