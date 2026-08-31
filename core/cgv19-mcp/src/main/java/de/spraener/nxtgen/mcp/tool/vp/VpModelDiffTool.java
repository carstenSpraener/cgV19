package de.spraener.nxtgen.mcp.tool.vp;

import com.google.gson.Gson;
import de.spraener.nxtgen.mcp.api.McpTool;
import de.spraener.nxtgen.mcp.api.McpToolContext;
import de.spraener.nxtgen.mcp.api.McpToolResult;

import java.util.Map;

public class VpModelDiffTool {

    private static final Gson gson = new Gson();

    @McpTool(
        name = "diff_model",
        description = "Shows the diff between the current Visual Paradigm model and the last saved baseline for a given package. Returns added (+) and removed (-) lines.",
        schema = "{\"type\":\"object\",\"properties\":{\"packageName\":{\"type\":\"string\",\"description\":\"The package name to diff\"},\"vpUrl\":{\"type\":\"string\",\"description\":\"Visual Paradigm plugin URL (default: http://localhost:7001)\"}},\"required\":[\"packageName\"]}"
    )
    public McpToolResult diffModel(McpToolContext ctx) {
        String packageName = ctx.getRequired("packageName");
        String vpUrl = ctx.getOptional("vpUrl", "http://localhost:7001");

        try {
            String response = HttpHelper.get(vpUrl + "/diff/" + packageName);
            Map<String, String> result = gson.fromJson(response, Map.class);

            if (result.containsKey("error")) {
                return McpToolResult.error(result.get("error"));
            }

            String diff = result.get("diff");
            if (diff != null && !diff.isEmpty()) {
                return McpToolResult.ok("Diff for package '" + packageName + "':\n" + diff);
            }
            return McpToolResult.ok("No changes detected for package '" + packageName + "'.");
        } catch (Exception e) {
            return McpToolResult.error("Failed to compute diff: " + e.getMessage());
        }
    }
}
