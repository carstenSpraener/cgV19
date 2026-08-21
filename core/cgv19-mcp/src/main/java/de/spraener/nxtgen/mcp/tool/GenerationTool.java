package de.spraener.nxtgen.mcp.tool;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.mcp.api.McpTool;
import de.spraener.nxtgen.mcp.api.McpToolContext;
import de.spraener.nxtgen.mcp.api.McpToolResult;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * MCP tool that runs cgV19 code generation from a model.
 */
public class GenerationTool {

    @McpTool(
            name = "generate_code",
            description = "Generates code from a model using cgV19 cartridges",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "modelUri": {"type": "string", "description": "Path or URL of the .oom model"},
                    "cartridge": {"type": "string", "description": "Optional cartridge name to run"},
                    "workDir": {"type": "string", "description": "Optional working directory for the output"},
                    "runBuild": {"type": "boolean", "description": "Whether to run ./gradlew build after generation"}
                  },
                  "required": ["modelUri"]
                }
                """
    )
    public McpToolResult generateCode(McpToolContext ctx) {
        String modelUri = ctx.getRequired("modelUri");
        String cartridge = ctx.getOptional("cartridge", null);
        String workDir = ctx.getOptional("workDir", null);
        boolean runBuild = ctx.getOptional("runBuild", false);

        List<String> output = new ArrayList<>();
        try {
            if (workDir != null) {
                NextGen.setWorkingDir(workDir);
            }
            NextGen.clearCartridgeNames();
            if (cartridge != null) {
                NextGen.runCartridgeWithName(cartridge);
            }
            NextGen nextGen = new NextGen(modelUri);
            nextGen.run();
            output.add("Code generation completed successfully.");
        } catch (Exception e) {
            return McpToolResult.error("Code generation failed: " + e.getMessage());
        }

        if (runBuild) {
            String buildDir = workDir != null ? workDir : NextGen.getWorkingDir();
            output.add(runGradleBuild(buildDir));
        }

        return McpToolResult.ok(output.toArray(new String[0]));
    }

    private String runGradleBuild(String dir) {
        try {
            ProcessBuilder pb = new ProcessBuilder("./gradlew", "build");
            pb.directory(new File(dir));
            pb.redirectErrorStream(true);
            Process process = pb.start();
            String processOutput = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();
            return "Gradle build (exit code " + exitCode + "):\n" + processOutput;
        } catch (IOException e) {
            return "Gradle build could not be started: " + e.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Gradle build was interrupted.";
        }
    }
}
