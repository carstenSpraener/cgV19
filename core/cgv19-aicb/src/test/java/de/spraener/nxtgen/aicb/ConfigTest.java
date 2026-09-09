package de.spraener.nxtgen.aicb;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigTest {

    @Test
    void configPresent_loadsFieldsAndHash() throws Exception {
        Path tempDir = Files.createTempDirectory("config-test");
        File configFile = new File(tempDir.toFile(), "ai-config.json");

        String json = """
            {
              "type": "langchain4j",
              "url": "https://api.openai.com/v1/chat/completions",
              "model": "gpt-4o-mini"
            }
            """;
        Files.writeString(configFile.toPath(), json);

        Config config = loadFromPath(tempDir.toFile());
        assertThat(config.getType()).isEqualTo("langchain4j");
        assertThat(config.getUrl()).isEqualTo("https://api.openai.com/v1/chat/completions");
        assertThat(config.getModel()).isEqualTo("gpt-4o-mini");

        String hash = config.configHash();
        assertThat(hash).isNotBlank().hasSize(64);
    }

    @Test
    void configMissing_returnsNull() {
        Config config = Config.load();
        // When no ai-config.json exists in any of the three locations, load() returns null
        assertThat(config).isNull();
    }

    @Test
    void overrideWith_replacesNonNullFields() {
        Config base = new Config();
        base.setUrl("https://base.example.com");
        base.setModel("gpt-3.5-turbo");

        Config override = new Config();
        override.setUrl("https://override.example.com");
        // model is null -> should NOT replace base's model

        base.overrideWith(override);

        assertThat(base.getUrl()).isEqualTo("https://override.example.com");
        assertThat(base.getModel()).isEqualTo("gpt-3.5-turbo"); // unchanged
    }

    @Test
    void overrideWith_allFieldsNull_keepsOriginalValues() {
        Config base = new Config();
        base.setUrl("https://base.example.com");
        base.setModel("gpt-3.5-turbo");

        Config empty = new Config(); // all fields null or default

        base.overrideWith(empty);

        assertThat(base.getUrl()).isEqualTo("https://base.example.com");
        assertThat(base.getModel()).isEqualTo("gpt-3.5-turbo");
    }

    @Test
    void sha256_isDeterministic() {
        String hash1 = Config.sha256("test prompt");
        String hash2 = Config.sha256("test prompt");
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void sha256_differentInputProducesDifferentHash() {
        String hash1 = Config.sha256("prompt A");
        String hash2 = Config.sha256("prompt B");
        assertThat(hash1).isNotEqualTo(hash2);
    }

    private Config loadFromPath(File dir) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(dir, "ai-config.json"), Config.class);
    }
}
