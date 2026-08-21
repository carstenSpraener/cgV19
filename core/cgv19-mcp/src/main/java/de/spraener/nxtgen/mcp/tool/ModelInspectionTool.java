package de.spraener.nxtgen.mcp.tool;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.mcp.api.McpTool;
import de.spraener.nxtgen.mcp.api.McpToolContext;
import de.spraener.nxtgen.mcp.api.McpToolResult;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.oom.model.OOMModelLoader;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MCP tools for inspecting cgV19 models and the available cartridges.
 */
public class ModelInspectionTool {

    @McpTool(
            name = "list_cartridges",
            description = "Lists all available cartridges on the classpath"
    )
    public McpToolResult listCartridges(McpToolContext ctx) {
        List<Cartridge> cartridges = NextGen.loadCartridges();
        String result = cartridges.stream()
                .map(Cartridge::getName)
                .collect(Collectors.joining("\n"));
        return McpToolResult.ok(result);
    }

    @McpTool(
            name = "describe_model",
            description = "Describes a model: its packages, classes and stereotypes",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "modelUri": {"type": "string", "description": "Path or URL of the .oom model"}
                  },
                  "required": ["modelUri"]
                }
                """
    )
    public McpToolResult describeModel(McpToolContext ctx) {
        String modelUri = ctx.getRequired("modelUri");
        try {
            OOModel model = loadModel(modelUri);
            StringBuilder sb = new StringBuilder();
            for (ModelElement element : model.getChilds()) {
                if (element instanceof MPackage pkg) {
                    describePackage(sb, pkg, 0);
                }
            }
            return McpToolResult.ok(sb.toString());
        } catch (Exception e) {
            return McpToolResult.error("Failed to describe model: " + e.getMessage());
        }
    }

    @McpTool(
            name = "describe_class",
            description = "Describes a class: its attributes, operations, references and stereotypes",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "modelUri": {"type": "string", "description": "Path or URL of the .oom model"},
                    "className": {"type": "string", "description": "Fully qualified class name"}
                  },
                  "required": ["modelUri", "className"]
                }
                """
    )
    public McpToolResult describeClass(McpToolContext ctx) {
        String modelUri = ctx.getRequired("modelUri");
        String className = ctx.getRequired("className");
        try {
            OOModel model = loadModel(modelUri);
            MClass clz = model.findClassByName(className);
            if (clz == null) {
                return McpToolResult.error("Class not found: " + className);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Class: ").append(clz.getFQName()).append("\n");
            appendStereotypes(sb, clz);
            sb.append("Attributes:\n");
            for (var attr : clz.getAttributes()) {
                sb.append("  - ").append(attr.getName()).append(": ").append(attr.getType()).append("\n");
            }
            sb.append("Operations:\n");
            for (var op : clz.getOperations()) {
                sb.append("  - ").append(op.getName()).append(": ").append(op.getType()).append("\n");
            }
            sb.append("References:\n");
            for (var ref : clz.getReferences()) {
                sb.append("  - ").append(ref.getName()).append("\n");
            }
            return McpToolResult.ok(sb.toString());
        } catch (Exception e) {
            return McpToolResult.error("Failed to describe class: " + e.getMessage());
        }
    }

    @McpTool(
            name = "list_classes_by_stereotype",
            description = "Lists the names of all classes carrying the given stereotype",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "modelUri": {"type": "string", "description": "Path or URL of the .oom model"},
                    "stereotype": {"type": "string", "description": "Stereotype name to filter by"}
                  },
                  "required": ["modelUri", "stereotype"]
                }
                """
    )
    public McpToolResult listClassesByStereotype(McpToolContext ctx) {
        String modelUri = ctx.getRequired("modelUri");
        String stereotype = ctx.getRequired("stereotype");
        try {
            OOModel model = loadModel(modelUri);
            List<MClass> classes = model.getClassesByStereotype(stereotype);
            String result = classes.stream()
                    .map(MClass::getName)
                    .collect(Collectors.joining("\n"));
            return McpToolResult.ok(result);
        } catch (Exception e) {
            return McpToolResult.error("Failed to list classes: " + e.getMessage());
        }
    }

    private void describePackage(StringBuilder sb, MPackage pkg, int level) {
        indent(sb, level);
        sb.append("Package: ").append(pkg.getFQName()).append("\n");
        for (MClass clz : pkg.getClasses()) {
            indent(sb, level + 1);
            sb.append("Class: ").append(clz.getName());
            if (!clz.getStereotypes().isEmpty()) {
                sb.append(" [").append(stereotypeNames(clz)).append("]");
            }
            sb.append("\n");
        }
        for (MPackage sub : pkg.getPackages()) {
            describePackage(sb, sub, level + 1);
        }
    }

    private void appendStereotypes(StringBuilder sb, MClass clz) {
        if (!clz.getStereotypes().isEmpty()) {
            sb.append("Stereotypes: ").append(stereotypeNames(clz)).append("\n");
        }
    }

    private String stereotypeNames(MClass clz) {
        return clz.getStereotypes().stream()
                .map(Stereotype::getName)
                .collect(Collectors.joining(", "));
    }

    private OOModel loadModel(String modelUri) {
        OOMModelLoader loader = new OOMModelLoader();
        NextGen.setActiveLoader(loader);
        try {
            Model model = loader.loadModel(modelUri);
            return (OOModel) model;
        } finally {
            NextGen.setActiveLoader(null);
        }
    }

    private void indent(StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
    }
}
