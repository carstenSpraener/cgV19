# 03-model-inspection-tools.md

## Ziel
MCP-Tools für Modell-Inspektion implementieren.

## Tools
| Tool | Parameter | Rückgabe |
|---|---|---|
| `list_cartridges` | keine | Liste aller verfügbaren Cartridges |
| `describe_model` | `modelUri` | Packages, Classes, Stereotypes als Text |
| `describe_class` | `modelUri`, `className` | Attribute, Operationen, References, Stereotypes |
| `list_classes_by_stereotype` | `modelUri`, `stereotype` | Klassen-Namen mit gegebenem Stereotype |

## Klasse
`de.spraener.nxtgen.mcp.tool.ModelInspectionTool`

## Tests (Red-Green)
1. **RED**: `ModelInspectionToolTest.shouldListCartridges()`
   - `list_cartridges()` → nicht-leere Liste mit "MetaCartridge"

2. **RED**: `ModelInspectionToolTest.shouldDescribeModel()`
   - Test-Modell laden → `describe_model()` → Text enthält Package-Namen

3. **RED**: `ModelInspectionToolTest.shouldDescribeClass()`
   - Test-Modell mit Klasse → `describe_class()` → Text enthält Attribute

4. **RED**: `ModelInspectionToolTest.shouldListClassesByStereotype()`
   - Test-Modell mit Stereotypes → gefilterte Klassen-Liste

## Implementierungshinweise
- `OOMModelLoader` kann `.oom`-Dateien und URLs laden
- `NextGen.loadCartridges()` liefert alle Cartridges
- `OOModel.getClassesByStereotype()` filtert Klassen
- `MClass.getAttributes()`, `getOperations()`, `getReferences()` für Klassendetails
- `StereotypeHelper.hasStereotype()` für Stereotype-Checks
- Alle Tools als `@McpTool` annotierte Methoden

## Verifikation
```bash
./gradlew :cgv19-mcp:test --tests ModelInspectionToolTest
```
