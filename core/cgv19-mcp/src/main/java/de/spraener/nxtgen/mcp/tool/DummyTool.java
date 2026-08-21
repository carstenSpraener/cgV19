package de.spraener.nxtgen.mcp.tool;

import de.spraener.nxtgen.mcp.api.McpTool;
import de.spraener.nxtgen.mcp.api.McpToolContext;
import de.spraener.nxtgen.mcp.api.McpToolResult;

/**
 * Dummy tool for initial testing.
 * Will be replaced by real tools during implementation.
 */
public class DummyTool {

    @McpTool(
            name = "ping",
            description = "Test tool - returns pong to verify MCP connectivity"
    )
    public McpToolResult ping(McpToolContext ctx) {
        return McpToolResult.ok("pong");
    }
}
