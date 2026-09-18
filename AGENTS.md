# AGENTS – cgV19 Repository Guide

Model-driven code generation: models (Groovy `.oom`, Visual Paradigm via port 7001, or URLs) are interpreted by pluggable *cartridges* that generate code. Java 17, Gradle 8.

## Build layout — five separate Gradle builds (no root build)

- `core/` — the only healthy build; wrapper works. Modules: cgv19-core, -oom, -gradle (Gradle plugin), -javapoet, -metacartridge, -pojo, -annotationprocessor, -cli, -mcp, -aicb. Version: `core/gradle.properties` (26.x).
- `examples/` — works; composite build (`includeBuild('../core')` + substitution) compiles against **local core source**. No wrapper — use system `gradle`.
- `cartridges/`, `demoProjects/` — **broken**: non-executable gradlew, wrapper jar committed empty, buildscripts reference `project(':cgv19-core')` which isn't in that build (removed from `core/settings.gradle` in 26.0.0). Last jars: 24.1.1.
- `plugins/` — not a Gradle root (settings.gradle removed in 26.0.0); build subprojects individually (`cgv19-vpplugin` has `buildPlugin.sh`).

## CLI

```bash
cd core && ./gradlew :cgv19-cli:installDist   # → bin/cgv19 (lowercase) + bin/cgv19-mcp
```

- Dist bundles core cartridges + blueprints into `cartridges/` (on classpath via `$APP_HOME/cartridges/*`) — drop new cartridge jars there, verify with `cgv19 -l`.
- Root `buildCompleteDist.sh` is **broken** (`installDis`) — don't use.
- `./gradlew build` in core **fails** at `:cgv19-aicb:test` unless LM-Studio runs (tests throw, don't assume) → use `-x :cgv19-aicb:test`.
- Options: `-m <model>` (file/dir/URL) · `-c <name[:...]>` colon-separated cartridges, omit → all · `-d` **flag** deletes generated files · `-l` list · `-w <dir>` · `--log-level` · `--mcp`. Docs claiming `-d <name>` selects a cartridge are wrong — that's `-c`.

## Model loading (`OOMModelLoader`)

Handles `.oom` files and `http*` URLs; chain: local file → classpath resource → URL (saves a copy) → **on URL failure, silently falls back to `<last-segment>.oom` in the working dir**. So `http://localhost:7001/<pkg>` builds fine without Visual Paradigm — examples ship a minimal `.oom` stub; check you got the real model.

## Generated code

- Marker `THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS` (top of file) → overwritten each run; `-d` deletes.
- `**/*-gen` is gitignored — never check in generated sources, **except** `core/cgv19-metacartridge/src/main/java-gen/**` (tracked on purpose: bootstrap).
- Editing a Groovy template in `resources/`: remove the protection line first or your edits are wiped.

## Version skew (expect `NoSuchMethodError`)

core = 26.x; local maven repo `repo/` holds **24.1.1** (tracked despite .gitignore); cartridge buildscripts pin **23.1.0** from Central; examples use 24.1.0 (substituted with local core). Resolve cartridge classpaths to local/26.x core when developing against current core.

## Cartridge development

1. Scaffold: `cgv19 -m my-cartridge.yml -c cgv19Cartridge` (blueprint; prompts for name/version/package) → Gradle project.
2. Implement MDD-style (VP model + metacartridge stereotypes) or annotation-based (`@CGV19Cartridge` + `AnnotatedCartridgeImpl`); cartridge class = generated Base + hand-written derived (GeneratorGap).
3. `gradle jar` → copy to `<install>/cartridges/`; verify with `cgv19 -l`.
4. Tests build models via `OOModelBuilder` (missing from the 23.1.x maven artifact — copy it into test sources if on that version).
- The `de.spraener.nxtgen.cgV19` Gradle plugin registers a `cgV19` task (NextGen on the configured model, `cartridge` config as classpath); `compileJava` depends on it.

## Releases & CI

Push to a `release-*` branch → Actions (JDK 17, in `core/`): build → publish to GitHub Packages (`maven.pkg.github.com/carstenSpraener/cgV19`) → tag `v<version>` from `core/gradle.properties` (bump it on the release branch) → GitHub release.

## 26.x modules & docs

- `cgv19-mcp` — MCP server (`cgv19 --mcp`): model inspection, generation, metacartridge tools; VP-modification tools via HTTP :7001. `cgv19-aicb` — LLM code blocks (langchain4j). Activity/FSM support is **ALPHA**.
- Docs: `docs/GettingStarted.md` · `core/cgv19-core/doc/CoreArchitecture.md` · `cartridges/doc/Cartridges.md` · `docs/CartridgeDevelopment.md` (tutorial) · `core/cgv19-annotationprocessor/README.md` (`java-ap://<dir>` loader, JDK required) · `docs/ReleaseNotes-26.0.0.md`
