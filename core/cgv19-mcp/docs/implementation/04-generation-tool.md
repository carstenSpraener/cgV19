# 04-generation-tool.md

## Ziel
`generate_code` Tool implementieren – Code-Generierung aus Modell.

## Tool
| Tool | Parameter | Rückgabe |
|---|---|---|
| `generate_code` | `modelUri`, `cartridge` (optional), `workDir` (optional) | Generierungs-Log + Build-Ergebnis |

## Klasse
`de.spraener.nxtgen.mcp.tool.GenerationTool`

## Tests (Red-Green)
1. **RED**: `GenerationToolTest.shouldGenerateCodeFromOomFile()`
   - `.oom`-Datei → `generate_code()` → Erfolgsmeldung

2. **RED**: `GenerationToolTest.shouldGenerateCodeWithCartridge()`
   - `.oom` + Cartridge-Name → nur diese Cartridge wird ausgeführt

3. **RED**: `GenerationToolTest.shouldGenerateCodeWithWorkDir()`
   - `.oom` + workDir → Ausgabe im指定的 Verzeichnis

4. **RED**: `GenerationToolTest.shouldReturnErrorForInvalidModel()`
   - Ungültige URI → Fehlermeldung

5. **RED**: `GenerationToolTest.shouldRunGradleBuild()`
   - Nach Generierung → `./gradlew build` ausführen → Ergebnis zurückgeben

## Implementierungshinweise
- `NextGen` direkt aufrufen (nicht `CGV19.main()`!)
- `NextGen.setWorkingDir()` für workDir
- `NextGen.runCartridgeWithName()` für Cartridge-Filter
- `new NextGen(modelUri).run()` für Generierung
- `ProcessBuilder` für Gradle-Build: `./gradlew build`
- Ausgabe von stdout/stderr einfangen und zurückgeben
- `System.exit()` vermeiden – `NextGen.run()` werft Exception statt exit

## Verifikation
```bash
./gradlew :cgv19-mcp:test --tests GenerationToolTest
```
