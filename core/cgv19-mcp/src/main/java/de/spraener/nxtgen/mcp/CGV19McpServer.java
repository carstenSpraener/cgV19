package de.spraener.nxtgen.mcp;

import de.spraener.nxtgen.mcp.api.McpServerProvider;
import de.spraener.nxtgen.mcp.api.McpToolDescriptor;
import de.spraener.nxtgen.mcp.api.McpToolRegistry;
import de.spraener.nxtgen.mcp.sdk.McpSdkServerProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

        McpToolRegistry registry = new McpToolRegistry();

        // All tools (core + VP — always on classpath)
        List<McpToolDescriptor> allTools = new ArrayList<>();
        McpToolDescriptor[] tools = registry.scan("de.spraener.nxtgen.mcp.tool");
        allTools.addAll(Arrays.asList(tools));
        LOGGER.info("Discovered " + tools.length + " MCP tools");

        // Create provider and start server
        McpServerProvider provider = new McpSdkServerProvider();
        LOGGER.info("Using provider: " + provider.getId());
        provider.start(SERVER_NAME, SERVER_VERSION, allTools.toArray(new McpToolDescriptor[0]));

        LOGGER.info("cgV19 MCP Server started with " + allTools.size() + " tools (including VP model-modification tools)");
    }
}
