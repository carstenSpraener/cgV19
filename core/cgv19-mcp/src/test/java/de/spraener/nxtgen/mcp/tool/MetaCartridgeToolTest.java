package de.spraener.nxtgen.mcp.tool;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.mcp.api.McpToolContext;
import de.spraener.nxtgen.mcp.api.McpToolResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MetaCartridgeToolTest {

    @TempDir
    Path tempDir;

    private final MetaCartridgeTool tool = new MetaCartridgeTool();

    @BeforeEach
    void setUp() {
        NextGen.clearCartridgeNames();
    }

    @AfterEach
    void tearDown() throws IOException {
        NextGen.clearCartridgeNames();
        deleteRecursively(tempDir);
    }

    private McpToolContext ctx(Map<String, Object> args) {
        return new McpToolContext(args, tempDir.toString());
    }

    private Path copyDslToTemp() throws IOException {
        Path target = tempDir.resolve("dsl.oom");
        Files.copy(Path.of("src/test/resources/dsl-test.oom"), target);
        return target;
    }

    @Test
    void shouldCreateNewCartridgeProject() throws IOException {
        Path out = tempDir.resolve("mycartridge");
        McpToolResult result = tool.newCartridge(ctx(Map.of(
                "name", "MyCartridge",
                "package", "com.example",
                "outputDir", out.toString()
        )));
        assertThat(result.isError()).isFalse();
        assertThat(out.resolve("DSL.oom")).exists();
        assertThat(out.resolve("build.gradle")).exists();
        String dsl = Files.readString(out.resolve("DSL.oom"));
        assertThat(dsl).contains("MyCartridge").contains("com.example");
    }

    @Test
    void shouldLoadDSL() throws IOException {
        Path dsl = copyDslToTemp();
        McpToolResult result = tool.loadDsl(ctx(Map.of("dslPath", dsl.toString())));
        assertThat(result.isError()).isFalse();
        String text = String.join("\n", result.getContents());
        assertThat(text).contains("Stereotypes:");
        assertThat(text).contains("Transformations:");
        assertThat(text).contains("Generators:");
    }

    @Test
    void shouldAddStereotype() throws IOException {
        Path dsl = copyDslToTemp();
        McpToolResult result = tool.addStereotype(ctx(Map.of(
                "dslPath", dsl.toString(),
                "name", "MyStereotype"
        )));
        assertThat(result.isError()).isFalse();
        McpToolResult list = tool.listStereotypes(ctx(Map.of("dslPath", dsl.toString())));
        assertThat(String.join("\n", list.getContents())).contains("MyStereotype");
    }

    @Test
    void shouldAddStereotypeWithTaggedValues() throws IOException {
        Path dsl = copyDslToTemp();
        McpToolResult result = tool.addStereotype(ctx(Map.of(
                "dslPath", dsl.toString(),
                "name", "MyStereotype",
                "baseClass", "MClass",
                "taggedValues", List.of("key=value")
        )));
        assertThat(result.isError()).isFalse();
        String oom = Files.readString(dsl);
        assertThat(oom).contains("taggedValue");
        assertThat(oom).contains("key");
        assertThat(oom).contains("value");
    }

    @Test
    void shouldAddTransformation() throws IOException {
        Path dsl = copyDslToTemp();
        McpToolResult result = tool.addTransformation(ctx(Map.of(
                "dslPath", dsl.toString(),
                "name", "MyTransformation",
                "package", "com.example",
                "metaType", "MClass",
                "requiredStereotype", "MyStereotype",
                "priority", 0
        )));
        assertThat(result.isError()).isFalse();
        McpToolResult list = tool.listTransformations(ctx(Map.of("dslPath", dsl.toString())));
        assertThat(String.join("\n", list.getContents())).contains("MyTransformation");
    }

    @Test
    void shouldAddGenerator() throws IOException {
        Path dsl = copyDslToTemp();
        McpToolResult result = tool.addGenerator(ctx(Map.of(
                "dslPath", dsl.toString(),
                "name", "MyGenerator",
                "package", "com.example",
                "requiredStereotype", "MyStereotype",
                "outputType", "Java",
                "generatesOn", "MClass",
                "outputTo", "src"
        )));
        assertThat(result.isError()).isFalse();
        McpToolResult list = tool.listGenerators(ctx(Map.of("dslPath", dsl.toString())));
        assertThat(String.join("\n", list.getContents())).contains("MyGenerator");
    }

    @Test
    void shouldAddTemplate() throws IOException {
        McpToolResult result = tool.addTemplate(ctx(Map.of(
                "generatorName", "MyGenerator",
                "templateContent", "println 'hello'"
        )));
        assertThat(result.isError()).isFalse();
        Path template = tempDir.resolve("MyGenerator.groovy");
        assertThat(template).exists();
        assertThat(Files.readString(template)).contains("hello");
    }

    @Test
    void shouldListStereotypes() throws IOException {
        Path dsl = copyDslToTemp();
        tool.addStereotype(ctx(Map.of("dslPath", dsl.toString(), "name", "StereotypeA")));
        McpToolResult result = tool.listStereotypes(ctx(Map.of("dslPath", dsl.toString())));
        assertThat(result.isError()).isFalse();
        assertThat(String.join("\n", result.getContents())).contains("StereotypeA");
    }

    @Test
    void shouldListTransformations() throws IOException {
        Path dsl = copyDslToTemp();
        tool.addTransformation(ctx(Map.of(
                "dslPath", dsl.toString(), "name", "TransformationA", "package", "com.example",
                "metaType", "MClass", "requiredStereotype", "X", "priority", 1
        )));
        McpToolResult result = tool.listTransformations(ctx(Map.of("dslPath", dsl.toString())));
        assertThat(result.isError()).isFalse();
        assertThat(String.join("\n", result.getContents())).contains("TransformationA");
    }

    @Test
    void shouldListGenerators() throws IOException {
        Path dsl = copyDslToTemp();
        tool.addGenerator(ctx(Map.of(
                "dslPath", dsl.toString(), "name", "GeneratorA", "package", "com.example",
                "requiredStereotype", "X", "outputType", "Java", "generatesOn", "MClass", "outputTo", "src"
        )));
        McpToolResult result = tool.listGenerators(ctx(Map.of("dslPath", dsl.toString())));
        assertThat(result.isError()).isFalse();
        assertThat(String.join("\n", result.getContents())).contains("GeneratorA");
    }

    @Test
    void shouldRunMetaGeneration() throws IOException {
        Path dsl = copyDslToTemp();
        Path workDir = tempDir.resolve("gen");
        Files.createDirectories(workDir);
        McpToolResult result = tool.runGeneration(ctx(Map.of(
                "dslPath", dsl.toString(),
                "workDir", workDir.toString()
        )));
        assertThat(result.isError()).isFalse();
        try (Stream<Path> walk = Files.walk(workDir)) {
            long javaFiles = walk.filter(p -> p.toString().endsWith(".java")).count();
            assertThat(javaFiles).isGreaterThan(0);
        }
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
