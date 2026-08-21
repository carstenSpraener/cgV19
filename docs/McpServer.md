# cgV19 MCP Server

The `cgv19-mcp` module exposes cgV19 as an [MCP (Model Context Protocol)](https://modelcontextprotocol.io)
server. This lets AI assistants (Claude Desktop, opencode, or any other MCP client)
inspect models, run code generation, and build new cartridges directly through
natural language — no manual CLI calls required.

The server speaks MCP over **stdio**, which is the standard transport for
locally launched MCP servers. It is bundled with the [cgv19-cli](../core/cgv19-cli/README.md)
distribution as the `cgv19-mcp` start script.

## Installation

Build the CLI distribution (this also builds the MCP server):

```bash
cd core
./gradlew :cgv19-cli:installDist
```

The MCP start script is created under
`core/cgv19-cli/build/install/cgv19-cli/bin/cgv19-mcp`.

Cartridges are discovered the same way as for the CLI: drop their jars into the
`cartridges/` folder of the installation directory.

## Connecting an MCP client

Point your MCP client at the `cgv19-mcp` binary. Example configuration
(Claude Desktop / generic `mcpServers` block):

```json
{
  "mcpServers": {
    "cgv19": {
      "command": "/path/to/cgv19-cli/build/install/cgv19-cli/bin/cgv19-mcp",
      "env": {}
    }
  }
}
```

Once connected, the client lists the tools below and can call them.

## Available tools

### Model inspection

| Tool | Parameters | Returns |
|---|---|---|
| `list_cartridges` | — | All cartridges available on the classpath |
| `describe_model` | `modelUri` | Packages, classes and stereotypes of a model |
| `describe_class` | `modelUri`, `className` | Attributes, operations, references and stereotypes of a class |
| `list_classes_by_stereotype` | `modelUri`, `stereotype` | Names of all classes carrying the given stereotype |

`modelUri` can be a path to a `.oom` file, a directory, or a URL (e.g. the
Visual Paradigm plugin at `http://localhost:7001/<package>`).

### Code generation

| Tool | Parameters | Returns |
|---|---|---|
| `generate_code` | `modelUri`, `cartridge` (optional), `workDir` (optional), `runBuild` (optional) | Generation log, optionally followed by a `./gradlew build` result |

If `cartridge` is omitted, all discovered cartridges run. Set `runBuild` to
`true` to compile the generated project right after generation.

### Metacartridge (cartridge development)

These tools let an assistant build a new model-driven cartridge step by step.

| Tool | Parameters | Returns |
|---|---|---|
| `meta_new_cartridge` | `name`, `package`, `outputDir` | A new cartridge project skeleton (`DSL.oom`, `build.gradle`) |
| `meta_load_dsl` | `dslPath` | The DSL structure: stereotypes, transformations, generators |
| `meta_add_stereotype` | `dslPath`, `name`, `baseClass` (optional), `taggedValues` (optional) | Updated DSL |
| `meta_add_transformation` | `dslPath`, `name`, `package`, `metaType`, `requiredStereotype`, `priority` (optional) | Updated DSL |
| `meta_add_generator` | `dslPath`, `name`, `package`, `requiredStereotype`, `outputType`, `generatesOn`, `outputTo` | Updated DSL |
| `meta_add_template` | `generatorName`, `templateContent` | The created Groovy template file |
| `meta_list_stereotypes` | `dslPath` | All stereotypes in the DSL |
| `meta_list_transformations` | `dslPath` | All transformations in the DSL |
| `meta_list_generators` | `dslPath` | All code generators in the DSL |
| `meta_run_generation` | `dslPath`, `workDir` | Runs the metacartridge to generate the cartridge Java code |

## Example workflow

A typical assistant-driven session looks like this:

1. `describe_model` on your `.oom` file to see what is in the model.
2. `list_classes_by_stereotype` with `Resource` to find the REST resources.
3. `generate_code` with the `rest` cartridge to produce a Spring Boot app.
4. `meta_new_cartridge` … `meta_run_generation` to scaffold a brand-new
   cartridge for a different target language.

## Implementation notes

The server discovers tools by scanning the `de.spraener.nxtgen.mcp.tool`
package for methods annotated with `@McpTool`. Each tool is a plain method
taking an `McpToolContext` and returning an `McpToolResult`. The transport and
SDK wiring live behind the `McpServerProvider` interface, so the underlying MCP
SDK can be swapped without touching the tools.

A detailed, step-by-step design of each tool group is available in
[core/cgv19-mcp/docs/implementation](../core/cgv19-mcp/docs/implementation/).
