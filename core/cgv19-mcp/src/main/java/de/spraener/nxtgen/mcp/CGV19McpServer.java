package de.spraener.nxtgen.mcp;

import de.spraener.nxtgen.mcp.api.McpServerProvider;
import de.spraener.nxtgen.mcp.api.McpToolDescriptor;
import de.spraener.nxtgen.mcp.api.McpToolRegistry;
import de.spraener.nxtgen.mcp.sdk.McpSdkServerProvider;

import java.util.logging.Logger;

/**
 * Main entry point for the cgV19 MCP server.
 * Discovers tools via annotation scanning and starts the MCP server.
 */
public class CGV19McpServer {

    private static final Logger LOGGER = Logger.getLogger(CGV19McpServer.class.getName());
    private static final String SERVER_NAME = "cgv19-mcp";
    private static final String SERVER_VERSION = "1.0.0";

    public static void main(String[] args) {
        LOGGER.info("Starting cgV19 MCP Server...");

        // Discover tools via annotation scanning
        McpToolRegistry registry = new McpToolRegistry();
        McpToolDescriptor[] tools = registry.scan("de.spraener.nxtgen.mcp.tool");
        LOGGER.info("Discovered " + tools.length + " MCP tools");

        // Create provider and start server
        McpServerProvider provider = new McpSdkServerProvider();
        LOGGER.info("Using provider: " + provider.getId());
        provider.start(SERVER_NAME, SERVER_VERSION, tools);

        LOGGER.info("cgV19 MCP Server started");
    }
}
