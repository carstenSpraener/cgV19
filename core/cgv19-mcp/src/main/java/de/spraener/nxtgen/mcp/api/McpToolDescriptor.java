package de.spraener.nxtgen.mcp.api;

import java.util.function.Function;

/**
 * Describes an MCP tool and its handler.
 * Created by scanning @McpTool annotations.
 */
public class McpToolDescriptor {
    private final String name;
    private final String description;
    private final String schema;
    private final Function<McpToolContext, McpToolResult> handler;

    public McpToolDescriptor(String name, String description, String schema,
                             Function<McpToolContext, McpToolResult> handler) {
        this.name = name;
        this.description = description;
        this.schema = schema;
        this.handler = handler;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSchema() {
        return schema;
    }

    public Function<McpToolContext, McpToolResult> getHandler() {
        return handler;
    }
}
