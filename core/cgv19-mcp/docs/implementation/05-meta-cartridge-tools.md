# 05-meta-cartridge-tools.md

## Ziel
Metacartridge-Tools implementieren – Cartridge-Erstellung via MCP.

## Tools
| Tool | Parameter | Rückgabe |
|---|---|---|
| `meta_new_cartridge` | `name`, `package`, `outputDir` | Neues Cartridge-Projekt mit Skelett |
| `meta_load_dsl` | `dslPath` | DSL-Struktur (Stereotypes, Transformations, Generators) |
| `meta_add_stereotype` | `dslPath`, `name`, `baseClass`, `taggedValues[]` | Aktualisierte DSL |
| `meta_add_transformation` | `dslPath`, `name`, `package`, `metaType`, `requiredStereotype`, `priority` | Aktualisierte DSL |
| `meta_add_generator` | `dslPath`, `name`, `package`, `requiredStereotype`, `outputType`, `generatesOn`, `outputTo` | Aktualisierte DSL |
| `meta_add_template` | `generatorName`, `templateContent` | Erstellte Template-Datei |
| `meta_list_stereotypes` | `dslPath` | Alle Stereotypes |
| `meta_list_transformations` | `dslPath` | Alle Transformations |
| `meta_list_generators` | `dslPath` | Alle CodeGenerators |
| `meta_run_generation` | `dslPath`, `workDir` | Metacartridge-Generierung + Gradle Build |

## Klassen
- `de.spraener.nxtgen.mcp.tool.MetaCartridgeTool`
- `de.spraener.nxtgen.mcp.helper.CartridgeDSLHelper`

## Tests (Red-Green)

### meta_new_cartridge
1. **RED**: `MetaCartridgeToolTest.shouldCreateNewCartridgeProject()`
   - `meta_new_cartridge("MyCartridge", "com.example", "/tmp/test")` → Verzeichnis mit `DSL.oom`, `build.gradle`, Projektstruktur

### meta_load_dsl
2. **RED**: `MetaCartridgeToolTest.shouldLoadDSL()`
   - Existierende `DSL.oom` → Struktur mit Stereotypes, Transformations, Generators

### meta_add_stereotype
3. **RED**: `MetaCartridgeToolTest.shouldAddStereotype()`
   - `meta_add_stereotype(...)` → DSL enthält neuen Stereotype
4. **RED**: `MetaCartridgeToolTest.shouldAddStereotypeWithTaggedValues()`
   - Mit TaggedValueDefinitions → korrekt im `.oom`

### meta_add_transformation
5. **RED**: `MetaCartridgeToolTest.shouldAddTransformation()`
   - `meta_add_transformation(...)` → DSL enthält neue Transformation

### meta_add_generator
6. **RED**: `MetaCartridgeToolTest.shouldAddGenerator()`
   - `meta_add_generator(...)` → DSL enthält neuen CodeGenerator

### meta_add_template
7. **RED**: `MetaCartridgeToolTest.shouldAddTemplate()`
   - `meta_add_template(...)` → `.groovy`-Datei erstellt

### meta_list_*
8. **RED**: `MetaCartridgeToolTest.shouldListStereotypes()`
   - `meta_list_stereotypes(...)` → Liste der Stereotypes
9. **RED**: `MetaCartridgeToolTest.shouldListTransformations()`
   - `meta_list_transformations(...)` → Liste der Transformations
10. **RED**: `MetaCartridgeToolTest.shouldListGenerators()`
    - `meta_list_generators(...)` → Liste der Generators

### meta_run_generation
11. **RED**: `MetaCartridgeToolTest.shouldRunMetaGeneration()`
    - `meta_run_generation(...)` → Java-Code generiert + Gradle Build erfolgreich

## Implementierungshinweise
- `OOMModelLoader.loadFromString()` zum Laden von `.oom`
- `OOMExporter.export()` zum Speichern (aus Abschnitt 02)
- `CartridgeDSLHelper` manipuliert `OOModel` programmatisch:
  - `addStereotype(OOModel, String, String, List<TaggedValue>)` 
  - `addTransformation(OOModel, String, String, String, String, int)`
  - `addGenerator(OOModel, String, String, String, String, String, String)`
- `MPackage.findOrCreatePackage()` für Package-Erstellung
- `MPackage.createMClass()` für Klasse-Erstellung
- `StereotypeHelper` für Stereotype-Manipulation
- Template-Skelette in `src/main/resources/cartridge-skeleton/`
- `meta_run_generation`: Metacartridge via `NextGen` ausführen, dann `./gradlew build`

## Verifikation
```bash
./gradlew :cgv19-mcp:test --tests MetaCartridgeToolTest
```
