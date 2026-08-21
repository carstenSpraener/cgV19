package de.spraener.nxtgen.mcp.tool;

import de.spraener.nxtgen.mcp.api.McpToolContext;
import de.spraener.nxtgen.mcp.api.McpToolResult;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ModelInspectionToolTest {

    private static final String MODEL_URI = "src/test/resources/testmodel.oom";

    private final ModelInspectionTool tool = new ModelInspectionTool();

    private McpToolContext ctx(Map<String, Object> args) {
        return new McpToolContext(args, System.getProperty("user.dir"));
    }

    @Test
    void shouldListCartridges() {
        McpToolResult result = tool.listCartridges(ctx(Map.of()));
        assertThat(result.isError()).isFalse();
        String text = String.join("\n", result.getContents());
        assertThat(text).contains("MetaCartridge");
    }

    @Test
    void shouldDescribeModel() {
        McpToolResult result = tool.describeModel(ctx(Map.of("modelUri", MODEL_URI)));
        assertThat(result.isError()).isFalse();
        String text = String.join("\n", result.getContents());
        assertThat(text).contains("de.test");
        assertThat(text).contains("Person");
        assertThat(text).contains("Address");
    }

    @Test
    void shouldDescribeClass() {
        McpToolResult result = tool.describeClass(ctx(Map.of(
                "modelUri", MODEL_URI,
                "className", "de.test.Person"
        )));
        assertThat(result.isError()).isFalse();
        String text = String.join("\n", result.getContents());
        assertThat(text).contains("name");
        assertThat(text).contains("getName");
    }

    @Test
    void shouldListClassesByStereotype() {
        McpToolResult result = tool.listClassesByStereotype(ctx(Map.of(
                "modelUri", MODEL_URI,
                "stereotype", "Ressource"
        )));
        assertThat(result.isError()).isFalse();
        String text = String.join("\n", result.getContents());
        assertThat(text).contains("Person");
        assertThat(text).doesNotContain("Address");
    }
}
