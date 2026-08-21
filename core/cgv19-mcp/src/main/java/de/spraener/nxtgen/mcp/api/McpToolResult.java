package de.spraener.nxtgen.mcp.api;

import java.util.List;

/**
 * Result returned by an MCP tool handler.
 */
public class McpToolResult {
    private final List<String> contents;
    private final boolean isError;

    public McpToolResult(List<String> contents, boolean isError) {
        this.contents = contents;
        this.isError = isError;
    }

    public static McpToolResult ok(String... contents) {
        return new McpToolResult(List.of(contents), false);
    }

    public static McpToolResult error(String... messages) {
        return new McpToolResult(List.of(messages), true);
    }

    public List<String> getContents() {
        return contents;
    }

    public boolean isError() {
        return isError;
    }
}
