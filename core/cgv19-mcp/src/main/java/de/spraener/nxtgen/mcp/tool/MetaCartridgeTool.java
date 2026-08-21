package de.spraener.nxtgen.mcp.tool;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.mcp.api.McpTool;
import de.spraener.nxtgen.mcp.api.McpToolContext;
import de.spraener.nxtgen.mcp.api.McpToolResult;
import de.spraener.nxtgen.mcp.helper.CartridgeDSLHelper;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.model.impl.TaggedValueImpl;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.oom.model.OOMExporter;
import de.spraener.nxtgen.oom.model.OOMModelLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MCP tools for creating and manipulating cgV19 cartridges via their DSL.
 */
public class MetaCartridgeTool {

    private final CartridgeDSLHelper helper = new CartridgeDSLHelper();

    @McpTool(
            name = "meta_new_cartridge",
            description = "Creates a new cartridge project skeleton (DSL.oom, build.gradle)",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "name": {"type": "string", "description": "Cartridge class name"},
                    "package": {"type": "string", "description": "Root package of the cartridge"},
                    "outputDir": {"type": "string", "description": "Directory to create the project in"}
                  },
                  "required": ["name", "package", "outputDir"]
                }
                """
    )
    public McpToolResult newCartridge(McpToolContext ctx) {
        String name = ctx.getRequired("name");
        String pkg = ctx.getRequired("package");
        String outputDir = ctx.getRequired("outputDir");
        try {
            Path out = Path.of(outputDir);
            Files.createDirectories(out);
            writeSkeleton(out, "DSL.oom", name, pkg);
            writeSkeleton(out, "build.gradle", name, pkg);
            return McpToolResult.ok("Created cartridge project '" + name + "' at " + out.toAbsolutePath());
        } catch (Exception e) {
            return McpToolResult.error("Failed to create cartridge: " + e.getMessage());
        }
    }

    @McpTool(
            name = "meta_load_dsl",
            description = "Loads a cartridge DSL and reports its stereotypes, transformations and generators",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "dslPath": {"type": "string", "description": "Path to the DSL.oom file"}
                  },
                  "required": ["dslPath"]
                }
                """
    )
    public McpToolResult loadDsl(McpToolContext ctx) {
        String dslPath = ctx.getRequired("dslPath");
        try {
            OOModel model = loadModel(dslPath);
            StringBuilder sb = new StringBuilder();
            appendList(sb, "Stereotypes", model.getClassesByStereotype(CartridgeDSLHelper.STEREOTYPE));
            appendList(sb, "Transformations", model.getClassesByStereotype(CartridgeDSLHelper.TRANSFORMATION));
            appendList(sb, "Generators", model.getClassesByStereotype(CartridgeDSLHelper.CODE_GENERATOR));
            return McpToolResult.ok(sb.toString());
        } catch (Exception e) {
            return McpToolResult.error("Failed to load DSL: " + e.getMessage());
        }
    }

    @McpTool(
            name = "meta_add_stereotype",
            description = "Adds a stereotype to a cartridge DSL",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "dslPath": {"type": "string"},
                    "name": {"type": "string"},
                    "baseClass": {"type": "string"},
                    "taggedValues": {"type": "array", "items": {"type": "string"}, "description": "key=value pairs"}
                  },
                  "required": ["dslPath", "name"]
                }
                """
    )
    public McpToolResult addStereotype(McpToolContext ctx) {
        String dslPath = ctx.getRequired("dslPath");
        String name = ctx.getRequired("name");
        String baseClass = ctx.getOptional("baseClass", null);
        List<String> specs = stringList(ctx, "taggedValues");
        try {
            OOModel model = loadModel(dslPath);
            helper.addStereotype(model, name, baseClass, toTaggedValues(specs));
            saveModel(model, dslPath);
            return McpToolResult.ok("Added stereotype '" + name + "' to " + dslPath);
        } catch (Exception e) {
            return McpToolResult.error("Failed to add stereotype: " + e.getMessage());
        }
    }

    @McpTool(
            name = "meta_add_transformation",
            description = "Adds a transformation to a cartridge DSL",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "dslPath": {"type": "string"},
                    "name": {"type": "string"},
                    "package": {"type": "string"},
                    "metaType": {"type": "string"},
                    "requiredStereotype": {"type": "string"},
                    "priority": {"type": "integer"}
                  },
                  "required": ["dslPath", "name", "package", "metaType", "requiredStereotype"]
                }
                """
    )
    public McpToolResult addTransformation(McpToolContext ctx) {
        String dslPath = ctx.getRequired("dslPath");
        String name = ctx.getRequired("name");
        String pkg = ctx.getRequired("package");
        String metaType = ctx.getRequired("metaType");
        String requiredStereotype = ctx.getRequired("requiredStereotype");
        int priority = ctx.getOptional("priority", 0);
        try {
            OOModel model = loadModel(dslPath);
            helper.addTransformation(model, name, pkg, metaType, requiredStereotype, priority);
            saveModel(model, dslPath);
            return McpToolResult.ok("Added transformation '" + name + "' to " + dslPath);
        } catch (Exception e) {
            return McpToolResult.error("Failed to add transformation: " + e.getMessage());
        }
    }

    @McpTool(
            name = "meta_add_generator",
            description = "Adds a code generator to a cartridge DSL",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "dslPath": {"type": "string"},
                    "name": {"type": "string"},
                    "package": {"type": "string"},
                    "requiredStereotype": {"type": "string"},
                    "outputType": {"type": "string"},
                    "generatesOn": {"type": "string"},
                    "outputTo": {"type": "string"}
                  },
                  "required": ["dslPath", "name", "package", "requiredStereotype", "outputType", "generatesOn", "outputTo"]
                }
                """
    )
    public McpToolResult addGenerator(McpToolContext ctx) {
        String dslPath = ctx.getRequired("dslPath");
        String name = ctx.getRequired("name");
        String pkg = ctx.getRequired("package");
        String requiredStereotype = ctx.getRequired("requiredStereotype");
        String outputType = ctx.getRequired("outputType");
        String generatesOn = ctx.getRequired("generatesOn");
        String outputTo = ctx.getRequired("outputTo");
        try {
            OOModel model = loadModel(dslPath);
            helper.addGenerator(model, name, pkg, requiredStereotype, outputType, generatesOn, outputTo);
            saveModel(model, dslPath);
            return McpToolResult.ok("Added generator '" + name + "' to " + dslPath);
        } catch (Exception e) {
            return McpToolResult.error("Failed to add generator: " + e.getMessage());
        }
    }

    @McpTool(
            name = "meta_add_template",
            description = "Creates a Groovy template file for a generator",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "generatorName": {"type": "string"},
                    "templateContent": {"type": "string"}
                  },
                  "required": ["generatorName", "templateContent"]
                }
                """
    )
    public McpToolResult addTemplate(McpToolContext ctx) {
        String generatorName = ctx.getRequired("generatorName");
        String templateContent = ctx.getRequired("templateContent");
        try {
            Path workDir = Path.of(ctx.getWorkingDir());
            Files.createDirectories(workDir);
            Path templateFile = workDir.resolve(generatorName + ".groovy");
            Files.writeString(templateFile, templateContent, StandardCharsets.UTF_8);
            return McpToolResult.ok("Created template " + templateFile.toAbsolutePath());
        } catch (Exception e) {
            return McpToolResult.error("Failed to add template: " + e.getMessage());
        }
    }

    @McpTool(
            name = "meta_list_stereotypes",
            description = "Lists all stereotypes in a cartridge DSL",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "dslPath": {"type": "string", "description": "Path to the DSL.oom file"}
                  },
                  "required": ["dslPath"]
                }
                """
    )
    public McpToolResult listStereotypes(McpToolContext ctx) {
        return listByStereotype(ctx, CartridgeDSLHelper.STEREOTYPE);
    }

    @McpTool(
            name = "meta_list_transformations",
            description = "Lists all transformations in a cartridge DSL",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "dslPath": {"type": "string", "description": "Path to the DSL.oom file"}
                  },
                  "required": ["dslPath"]
                }
                """
    )
    public McpToolResult listTransformations(McpToolContext ctx) {
        return listByStereotype(ctx, CartridgeDSLHelper.TRANSFORMATION);
    }

    @McpTool(
            name = "meta_list_generators",
            description = "Lists all code generators in a cartridge DSL",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "dslPath": {"type": "string", "description": "Path to the DSL.oom file"}
                  },
                  "required": ["dslPath"]
                }
                """
    )
    public McpToolResult listGenerators(McpToolContext ctx) {
        return listByStereotype(ctx, CartridgeDSLHelper.CODE_GENERATOR);
    }

    @McpTool(
            name = "meta_run_generation",
            description = "Runs the metacartridge on a DSL to generate the cartridge Java code",
            schema = """
                {
                  "type": "object",
                  "properties": {
                    "dslPath": {"type": "string"},
                    "workDir": {"type": "string"}
                  },
                  "required": ["dslPath", "workDir"]
                }
                """
    )
    public McpToolResult runGeneration(McpToolContext ctx) {
        String dslPath = ctx.getRequired("dslPath");
        String workDir = ctx.getRequired("workDir");
        try {
            NextGen.setWorkingDir(workDir);
            NextGen.clearCartridgeNames();
            NextGen.runCartridgeWithName("MetaCartridge");
            NextGen nextGen = new NextGen(dslPath);
            nextGen.run();
            return McpToolResult.ok("Meta generation completed in " + workDir);
        } catch (Exception e) {
            return McpToolResult.error("Meta generation failed: " + e.getMessage());
        }
    }

    private McpToolResult listByStereotype(McpToolContext ctx, String stereotype) {
        String dslPath = ctx.getRequired("dslPath");
        try {
            OOModel model = loadModel(dslPath);
            List<MClass> classes = model.getClassesByStereotype(stereotype);
            String result = classes.stream().map(MClass::getName).collect(Collectors.joining("\n"));
            return McpToolResult.ok(result);
        } catch (Exception e) {
            return McpToolResult.error("Failed to list: " + e.getMessage());
        }
    }

    private void appendList(StringBuilder sb, String title, List<MClass> classes) {
        sb.append(title).append(":\n");
        if (classes.isEmpty()) {
            sb.append("  (none)\n");
        } else {
            for (MClass c : classes) {
                sb.append("  - ").append(c.getName()).append("\n");
            }
        }
    }

    private void writeSkeleton(Path out, String fileName, String name, String pkg) throws IOException {
        String template = loadSkeleton(fileName);
        String content = template.replace("{{name}}", name).replace("{{package}}", pkg);
        Files.writeString(out.resolve(fileName), content, StandardCharsets.UTF_8);
    }

    private String loadSkeleton(String fileName) throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/cartridge-skeleton/" + fileName)) {
            if (is == null) {
                throw new IOException("Skeleton not found: " + fileName);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private List<TaggedValue> toTaggedValues(List<String> specs) {
        List<TaggedValue> result = new ArrayList<>();
        for (String spec : specs) {
            String[] kv = spec.split("=", 2);
            TaggedValueImpl tv = new TaggedValueImpl();
            tv.setName(kv[0]);
            tv.setValue(kv.length > 1 ? kv[1] : "");
            result.add(tv);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<String> stringList(McpToolContext ctx, String key) {
        Object value = ctx.getArguments().get(key);
        List<String> result = new ArrayList<>();
        if (value instanceof List<?> list) {
            for (Object o : list) {
                result.add(String.valueOf(o));
            }
        }
        return result;
    }

    private OOModel loadModel(String dslPath) {
        OOMModelLoader loader = new OOMModelLoader();
        NextGen.setActiveLoader(loader);
        try {
            Model model = loader.loadModel(dslPath);
            return (OOModel) model;
        } finally {
            NextGen.setActiveLoader(null);
        }
    }

    private void saveModel(OOModel model, String dslPath) throws IOException {
        new OOMExporter().export(model, Path.of(dslPath));
    }
}
