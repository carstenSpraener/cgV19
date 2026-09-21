package de.spraener.nxtgen.aicb;

import dev.langchain4j.data.message.ChatMessage;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MOperation;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LlmIntegrationTest {

    private static final String LM_STUDIO_URL = "http://localhost:1234/v1";
    private static final String LM_STUDIO_MODEL = "qwen3.6-35b-a3b";

    private static boolean isLmStudioAvailable = false;

    @BeforeAll
    static void setUp() {
        ClientFactory.reset();
        Cache.clear();

        String apiKey = System.getenv("CGV19_LLM_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("CGV19_LLM_API_KEY not set — skipping integration tests");
            return;
        }

        try {
            Config cfg = new Config();
            cfg.setUrl(LM_STUDIO_URL);
            cfg.setModel(LM_STUDIO_MODEL);
            Config.loadTestConfig(cfg);

            LlmClient client = ClientFactory.get();
            if (client.isMock()) {
                System.out.println("ClientFactory returned a MockLlmClient — skipping integration tests");
                return;
            }

            String probe = client.complete("Say hello in one word.");
            if (probe != null && !probe.isBlank()) {
                isLmStudioAvailable = true;
            }
        } catch (Exception e) {
            System.out.println("LM-Studio not reachable at " + LM_STUDIO_URL + " — skipping integration tests: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        ClientFactory.reset();
        Cache.clear();
    }

    @Test
    void complete_returnsNonEmptyResponse() throws IOException {
        assumeLmStudioAvailable();

        LlmClient client = ClientFactory.get();
        String result = client.complete("What is 2+2? Answer with just the number.");
        assertThat(result).isNotBlank();
    }

    @Test
    void chat_returnsResponseForConversation() throws IOException {
        assumeLmStudioAvailable();

        LlmClient client = ClientFactory.get();
        List<ChatMessage> messages = List.of(
                dev.langchain4j.data.message.UserMessage.from("What is the capital of France?")
        );

        String result = client.chat(messages);
        assertThat(result).isNotBlank();
    }

    @Test
    void aiCodeBlock_resolveWithLmStudio_returnsGeneratedContent() {
        assumeLmStudioAvailable();

        AiCodeBlock block = AiCodeBlock.resolve("Write a one-line Java comment that says hello.");
        assertThat(block.toCode()).isNotBlank();
    }

    @Test
    void aiCodeBlock_cacheWorksWithLmStudio() {
        assumeLmStudioAvailable();

        String prompt = "integration test unique prompt " + System.currentTimeMillis();
        AiCodeBlock block1 = AiCodeBlock.resolve(prompt);
        String result1 = block1.toCode();

        AiCodeBlock block2 = AiCodeBlock.resolve(prompt);
        String result2 = block2.toCode();

        assertThat(result1).isEqualTo(result2);
    }

    @Test
    void groovyTemplateWithAiCodeBlock_generatesPoJo() {
        assumeLmStudioAvailable();

        OOModel model = createPoJoModel();
        MClass pojo = model.findClassByName("com.example.GreetingService");

        URL templateUrl = LlmIntegrationTest.class.getResource("/aipojo_template.groovy");
        GroovyCodeBlockImpl block = new GroovyCodeBlockImpl("ai-pojo", pojo, templateUrl.toExternalForm());

        String generated = block.toCode();
        assertThat(generated).isNotBlank();
        assertThat(generated).contains("class GreetingService");
    }

    private static void assumeLmStudioAvailable() {
        Assumptions.assumeTrue(isLmStudioAvailable, "LM-Studio not available — integration test skipped");
    }

    private static OOModel createPoJoModel() {
        OOModel model = new OOModel();

        MPackage pkg = model.createPackage("com");
        MPackage subPkg = pkg.findOrCreatePackage("example");

        MClass mc = subPkg.createMClass("GreetingService");

        mc.createAttribute("name", "String");
        mc.createAttribute("age", "int");

        MOperation op = mc.createOperation("greetings");
        op.setType("String");
        return model;
    }
}
