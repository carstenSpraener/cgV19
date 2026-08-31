package de.spraener.nxtgen.mcp.tool.vp;

import com.google.gson.Gson;
import de.spraener.nxtgen.mcp.api.McpTool;
import de.spraener.nxtgen.mcp.api.McpToolContext;
import de.spraener.nxtgen.mcp.api.McpToolResult;

import java.util.Map;

public class VpGroovyTool {

    private static final Gson gson = new Gson();

    @McpTool(
        name = "execute_vp_groovy",
        description = "Executes a Groovy script in Visual Paradigm to create or modify model elements. The script has access to: modelFactory (IModelElementFactory), appManager (ApplicationManager), project (IProject), rootModel (IModelElement).",
        schema = "{\"type\":\"object\",\"properties\":{\"script\":{\"type\":\"string\",\"description\":\"The Groovy script to execute in Visual Paradigm\"},\"vpUrl\":{\"type\":\"string\",\"description\":\"Visual Paradigm plugin URL (default: http://localhost:7001)\"}},\"required\":[\"script\"]}"
    )
    public McpToolResult executeGroovy(McpToolContext ctx) {
        String script = ctx.getRequired("script");
        String vpUrl = ctx.getOptional("vpUrl", "http://localhost:7001");

        try {
            String jsonBody = "{\"script\":" + gson.toJson(escapeJson(script)) + "}";
            String response = HttpHelper.postJson(vpUrl + "/groovy", jsonBody);

            Map<String, String> result = gson.fromJson(response, Map.class);
            if (result.containsKey("error")) {
                return McpToolResult.error(result.get("error"));
            }
            return McpToolResult.ok(result.get("result") != null ? result.get("result") : "Script executed.");
        } catch (Exception e) {
            return McpToolResult.error("Failed to execute Groovy script: " + e.getMessage());
        }
    }

    private String escapeJson(String input) {
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}
