# 06-integration-test.md

## Ziel
End-to-End-Test des MCP-Servers mit allen Tools.

## Tests (Red-Green)
1. **RED**: `McpServerIntegrationTest.shouldStartServerWithAllTools()`
   - `CGV19McpServer` starten → alle Tools registriert

2. **RED**: `McpServerIntegrationTest.shouldDiscoverAllToolsViaRegistry()`
   - `McpToolRegistry.scan()` → alle @McpTool-Methoden gefunden

3. **RED**: `McpServerIntegrationTest.shouldExecutePingTool()`
   - `ping` Tool aufrufen → "pong" zurück

## Implementierungshinweise
- Integrationstest startet den MCP-Server nicht wirklich (keine stdio)
- Stattdessen: `McpToolRegistry` direkt testen
- Alle Tool-Klassen instantiieren und Methoden aufrufen
- Mocks für `OOMModelLoader` und `NextGen` wo nötig

## Verifikation
```bash
./gradlew :cgv19-mcp:test --tests McpServerIntegrationTest
```
