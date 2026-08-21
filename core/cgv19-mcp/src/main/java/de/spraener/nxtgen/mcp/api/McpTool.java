package de.spraener.nxtgen.mcp.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an MCP tool.
 * The method receives a McpToolContext with the tool arguments.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface McpTool {
    /**
     * Unique tool name (lowercase, underscores).
     */
    String name();

    /**
     * Human-readable description for the LLM.
     */
    String description();

    /**
     * JSON Schema for the tool's input arguments.
     */
    String schema() default """
        {
          "type": "object",
          "properties": {}
        }
        """;
}
