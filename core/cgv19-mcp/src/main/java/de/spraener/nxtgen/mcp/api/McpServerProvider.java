package de.spraener.nxtgen.mcp.api;

/**
 * Provider interface for MCP server implementations.
 * Different providers (MCP SDK, Spring AI, etc.) implement this interface
 * to expose tools to LLM clients.
 */
public interface McpServerProvider {

    /**
     * @return the provider identifier (e.g. "mcp-sdk", "spring-ai").
     */
    String getId();

    /**
     * Starts the MCP server with the given configuration.
     *
     * @param serverName the server name
     * @param serverVersion the server version
     * @param tools the tool descriptors to register
     */
    void start(String serverName, String serverVersion, McpToolDescriptor... tools);

    /**
     * Stops the MCP server gracefully.
     */
    void stop();
}
