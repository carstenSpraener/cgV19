package de.spraener.nxtgen.mcp;

import de.spraener.nxtgen.mcp.api.McpTool;
import de.spraener.nxtgen.mcp.api.McpToolContext;
import de.spraener.nxtgen.mcp.api.McpToolDescriptor;
import de.spraener.nxtgen.mcp.api.McpToolResult;
import de.spraener.nxtgen.mcp.tool.DummyTool;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class McpToolRegistryTest {

    @Test
    void shouldDiscoverDummyToolViaReflection() throws Exception {
        DummyTool dummyTool = new DummyTool();
        Method pingMethod = DummyTool.class.getDeclaredMethod("ping", McpToolContext.class);
        McpTool annotation = pingMethod.getAnnotation(McpTool.class);

        assertThat(annotation).isNotNull();
        assertThat(annotation.name()).isEqualTo("ping");
    }

    @Test
    void shouldExecuteDummyTool() throws Exception {
        DummyTool dummyTool = new DummyTool();
        Method pingMethod = DummyTool.class.getDeclaredMethod("ping", McpToolContext.class);
        pingMethod.setAccessible(true);

        McpToolContext ctx = new McpToolContext(Map.of(), System.getProperty("user.dir"));
        McpToolResult result = (McpToolResult) pingMethod.invoke(dummyTool, ctx);

        assertThat(result.isError()).isFalse();
        assertThat(result.getContents()).contains("pong");
    }

    @Test
    void shouldCreateToolDescriptor() {
        McpToolDescriptor descriptor = new McpToolDescriptor(
                "test",
                "test description",
                "{}",
                ctx -> McpToolResult.ok("result")
        );

        assertThat(descriptor.getName()).isEqualTo("test");
        assertThat(descriptor.getDescription()).isEqualTo("test description");
        assertThat(descriptor.getHandler().apply(null).getContents()).contains("result");
    }
}
