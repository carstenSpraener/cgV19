package de.spraener.nxtgen.mcp.tool.vp;

import com.google.gson.Gson;
import de.spraener.nxtgen.mcp.api.McpTool;
import de.spraener.nxtgen.mcp.api.McpToolContext;
import de.spraener.nxtgen.mcp.api.McpToolResult;

import java.util.Map;

public class VpSaveBaselineTool {

    private static final Gson gson = new Gson();

    @McpTool(
        name = "save_baseline",
        description = "Saves the current Visual Paradigm model state as a baseline for future diff comparisons.",
        schema = "{\"type\":\"object\",\"properties\":{\"vpUrl\":{\"type\":\"string\",\"description\":\"Visual Paradigm plugin URL (default: http://localhost:7001)\"}}}"
    )
    public McpToolResult saveBaseline(McpToolContext ctx) {
        String vpUrl = ctx.getOptional("vpUrl", "http://localhost:7001");

        try {
            String response = HttpHelper.postJson(vpUrl + "/save-baseline", "{}");
            Map<String, String> result = gson.fromJson(response, Map.class);

            if (result.containsKey("error")) {
                return McpToolResult.error(result.get("error"));
            }
            return McpToolResult.ok(result.get("message") != null ? result.get("message") : "Baseline saved.");
        } catch (Exception e) {
            return McpToolResult.error("Failed to save baseline: " + e.getMessage());
        }
    }
}
