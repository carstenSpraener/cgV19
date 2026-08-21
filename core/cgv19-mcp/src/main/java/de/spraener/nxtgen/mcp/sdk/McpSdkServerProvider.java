package de.spraener.nxtgen.mcp.sdk;

import de.spraener.nxtgen.mcp.api.McpServerProvider;
import de.spraener.nxtgen.mcp.api.McpToolContext;
import de.spraener.nxtgen.mcp.api.McpToolDescriptor;
import de.spraener.nxtgen.mcp.api.McpToolResult;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MCP SDK implementation of McpServerProvider.
 * Uses the official MCP SDK (io.modelcontextprotocol.sdk) with stdio transport.
 */
public class McpSdkServerProvider implements McpServerProvider {

    private McpSyncServer server;

    @Override
    public String getId() {
        return "mcp-sdk";
    }

    @Override
    public void start(String serverName, String serverVersion, McpToolDescriptor... tools) {
        McpJsonMapper jsonMapper = McpJsonDefaults.getMapper();

        List<McpServerFeatures.SyncToolSpecification> specifications = new ArrayList<>();
        for (McpToolDescriptor tool : tools) {
            Tool mcpTool = Tool.builder(tool.getName(), jsonMapper, tool.getSchema())
                    .description(tool.getDescription())
                    .build();
            specifications.add(new McpServerFeatures.SyncToolSpecification(
                    mcpTool,
                    (exchange, request) -> handleTool(tool, request.arguments())
            ));
        }

        this.server = McpServer.sync(new StdioServerTransportProvider(jsonMapper))
                .serverInfo(serverName, serverVersion)
                .tools(specifications)
                .build();
    }

    private CallToolResult handleTool(McpToolDescriptor descriptor, Map<String, Object> args) {
        McpToolContext ctx = new McpToolContext(args, System.getProperty("user.dir"));
        McpToolResult result = descriptor.getHandler().apply(ctx);

        return CallToolResult.builder()
                .textContent(result.getContents())
                .isError(result.isError())
                .build();
    }

    @Override
    public void stop() {
        if (server != null) {
            server.closeGracefully();
            server = null;
        }
    }
}
