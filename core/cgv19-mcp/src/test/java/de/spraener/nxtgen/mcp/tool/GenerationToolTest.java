package de.spraener.nxtgen.mcp.tool;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.mcp.api.McpToolContext;
import de.spraener.nxtgen.mcp.api.McpToolResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GenerationToolTest {

    private static final String GEN_MODEL_URI = "src/test/resources/genmodel.oom";
    private static final Path OOM_OUTPUT = Path.of("src/main/java-gen/de/spraener/nxtgen/model/oom/MyMetaClass.java");

    @TempDir
    Path workDir;

    private final GenerationTool tool = new GenerationTool();

    @BeforeEach
    void setUp() {
        NextGen.setWorkingDir(workDir.toString());
        NextGen.clearCartridgeNames();
    }

    @AfterEach
    void tearDown() throws IOException {
        NextGen.clearCartridgeNames();
        deleteRecursively(workDir);
    }

    private McpToolContext ctx(Map<String, Object> args) {
        return new McpToolContext(args, workDir.toString());
    }

    @Test
    void shouldGenerateCodeFromOomFile() {
        McpToolResult result = tool.generateCode(ctx(Map.of("modelUri", GEN_MODEL_URI)));
        assertThat(result.isError()).isFalse();
        String text = String.join("\n", result.getContents());
        assertThat(text).containsIgnoringCase("success");
    }

    @Test
    void shouldGenerateCodeWithCartridge() {
        // Filter to MetaCartridge, which produces no output for this model.
        // The OOMCartridge must be skipped, so no OOM output may appear.
        McpToolResult result = tool.generateCode(ctx(Map.of(
                "modelUri", GEN_MODEL_URI,
                "cartridge", "MetaCartridge"
        )));
        assertThat(result.isError()).isFalse();
        assertThat(workDir.resolve(OOM_OUTPUT)).doesNotExist();
    }

    @Test
    void shouldGenerateCodeWithWorkDir() {
        McpToolResult result = tool.generateCode(ctx(Map.of(
                "modelUri", GEN_MODEL_URI,
                "workDir", workDir.toString()
        )));
        assertThat(result.isError()).isFalse();
        assertThat(workDir.resolve(OOM_OUTPUT)).exists();
    }

    @Test
    void shouldReturnErrorForInvalidModel() {
        McpToolResult result = tool.generateCode(ctx(Map.of(
                "modelUri", "nonexistent-model-xyz.oom",
                "workDir", workDir.toString()
        )));
        assertThat(result.isError()).isTrue();
    }

    @Test
    void shouldRunGradleBuild() {
        McpToolResult result = tool.generateCode(ctx(Map.of(
                "modelUri", GEN_MODEL_URI,
                "workDir", workDir.toString(),
                "runBuild", true
        )));
        assertThat(result.isError()).isFalse();
        String text = String.join("\n", result.getContents());
        assertThat(text).containsIgnoringCase("gradle");
    }

    private void deleteRecursively(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted((a, b) -> b.getNameCount() - a.getNameCount())
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }
}
