# 01-api-interfaces.md

## Ziel
API-Package für MCP-Server etablieren. Provider-Implementierung kennt nur Interfaces.

## Bereits vorhanden
- `McpTool` Annotation
- `McpToolContext` 
- `McpServerProvider` Interface
- `McpToolDescriptor`
- `McpToolResult`
- `McpToolRegistry`

## Tests (Red-Green)
Keine neuen Tests nötig – API ist bereits implementiert.

## Verifikation
```bash
./gradlew :cgv19-mcp:test
```
