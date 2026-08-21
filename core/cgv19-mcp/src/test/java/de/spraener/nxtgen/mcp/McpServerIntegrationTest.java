package de.spraener.nxtgen.mcp;

import de.spraener.nxtgen.mcp.api.McpToolContext;
import de.spraener.nxtgen.mcp.api.McpToolDescriptor;
import de.spraener.nxtgen.mcp.api.McpToolRegistry;
import de.spraener.nxtgen.mcp.api.McpToolResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test that verifies all MCP tools are discovered and executable.
 * The MCP server itself is not started (no stdio); the registry is tested directly.
 */
class McpServerIntegrationTest {

    private static McpToolDescriptor[] tools;

    @BeforeAll
    static void discoverTools() {
        McpToolRegistry registry = new McpToolRegistry();
        tools = registry.scan("de.spraener.nxtgen.mcp.tool");
    }

    @Test
    void shouldStartServerWithAllTools() {
        List<String> names = toolNames();
        assertThat(names).contains(
                "ping",
                "list_cartridges", "describe_model", "describe_class", "list_classes_by_stereotype",
                "generate_code",
                "meta_new_cartridge", "meta_load_dsl", "meta_add_stereotype", "meta_add_transformation",
                "meta_add_generator", "meta_add_template", "meta_list_stereotypes",
                "meta_list_transformations", "meta_list_generators", "meta_run_generation"
        );
    }

    @Test
    void shouldDiscoverAllToolsViaRegistry() {
        assertThat(tools.length).isGreaterThanOrEqualTo(16);
        for (McpToolDescriptor tool : tools) {
            assertThat(tool.getName()).isNotBlank();
            assertThat(tool.getDescription()).isNotBlank();
            assertThat(tool.getHandler()).isNotNull();
        }
    }

    @Test
    void shouldExecutePingTool() {
        McpToolDescriptor ping = Arrays.stream(tools)
                .filter(t -> t.getName().equals("ping"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("ping tool not discovered"));
        McpToolResult result = ping.getHandler()
                .apply(new McpToolContext(Map.of(), System.getProperty("user.dir")));
        assertThat(result.isError()).isFalse();
        assertThat(result.getContents()).contains("pong");
    }

    private List<String> toolNames() {
        return Arrays.stream(tools).map(McpToolDescriptor::getName).collect(Collectors.toList());
    }
}
