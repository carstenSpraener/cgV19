# cgV19 — Agent Instructions

## Project at a Glance

cgV19 is a model-driven code generation framework. It reads UML-style models (from Visual Paradigm via port 7001, or `.oom` files) and runs **cartridges** (generation plugins) to produce code, docs, and config artifacts.

- **Language**: Java 17+ (Temurin), Groovy templates, Mustache templates. Code uses pattern matching for `instanceof` (Java 16+) — Java 11 will fail to compile.
- **Build**: Gradle wrapper — core uses 8.10, cartridges use 8.0
- **Group/version**: `de.spraener.nxtgen` / `24.1.1` (core), `23.1.1` (cartridges)

## Repo Structure — Two Gradle Roots + Standalone Plugins Dir

| Root | Purpose | Build independently? |
|---|---|---|
| `core/` | Framework: core engine, OOM model loader, CLI, Gradle plugin, JavaPoet support, metacartridge, pojo example, MCP server | Yes — `./gradlew` inside `core/` |
| `cartridges/` | Generation cartridges (restcartridge, angular, javalin, cloud, symfony, laravel) | Yes — `./gradlew` inside `cartridges/` |
| `plugins/` | Visual Paradigm plugin + its cartridge — **not a Gradle root** (no gradlew, no root build.gradle/settings.gradle) | No — build individual subprojects only |

**`core/settings.gradle` does NOT cross-include cartridges.** Each Gradle root is independent.

## Core Modules (inside `core/`)

| Module | Role |
|---|---|
| `cgv19-core` | Base framework, main class `de.spraener.nxtgen.NextGen`. Uses Java ServiceLoader for ModelLoaders and cartridges. |
| `cgv19-oom` | Object-oriented model implementation (packages, classes, attributes, relations). Reads `.oom` files or VP REST API. |
| `cgv19-cli` | Standalone CLI tool. Main class `de.spraener.nxtgen.cli.CGV19`. startScripts patches CLASSPATH to include `$APP_HOME/cartridges/*` and sets `-Dapp.path`. Also bundles cartridge JARs (oom, javapoet, metacartridge, pojo) into the dist. |
| `cgv19-gradle` | Gradle plugin (`de.spraener.nxtgen.gradle.CGV19`). Uses `java-gradle-plugin`. |
| `cgv19-javapoet` | JavaPoet code generator integration (squareup/javapoet 1.13.0). |
| `cgv19-metacartridge` | Meta-cartridge for model-driven cartridge development. **Has generated source**: `src/main/java-gen` is a `srcDir`. |
| `cgv19-pojo` | Simple example cartridge. |
| `cgv19-annotationprocessor` | ALPHA — incomplete, has **no** `build.gradle`. |
| `cgv19-mcp` | MCP server (`de.spraener.nxtgen.mcp.CGV19McpServer`). Uses MCP SDK 2.0.1. Also bundled into CLI dist via `createMcpStartScripts`. Includes VP model-modification tools (`de.spraener.nxtgen.mcp.tool.vp`) that communicate with the VP plugin via HTTP on port 7001 — no classpath dependency on `cgv19-vpplugin.jar`. If VP is not running, tools return a clear error message. |

## Key Build Commands

All Gradle commands run from the respective root directory. Use `./gradlew`, not bare `gradle`.

```bash
# Build core (all modules)
cd core && ./gradlew build

# Build a single module
cd core && ./gradlew :cgv19-core:build

# Run tests for a module
cd core && ./gradlew :cgv19-core:test

# Build cartridges (auto-discovers all cgv19-* dirs)
cd cartridges && ./gradlew build

# Build the standalone CLI distribution
cd core && ./gradlew :cgv19-cli:installDist
# Output: core/cgv19-cli/build/install/cgv19-cli/

# Build plugins subproject (not a Gradle root)
cd plugins/cgv19-vpplugin && ../../gradlew build  # or use core's gradlew
```

**`buildCompleteDist.sh` is broken** — it calls `gradle :cgv19-cli:installDis` (missing trailing `t`). Use the manual commands above instead.

## Docker Build

```bash
docker build -t cgv19 .
# Uses eclipse-temurin:17, builds CLI dist, entrypoint is /opt/cgv19/bin/cgv19
```

## CLI Usage

After building the dist:
```bash
core/cgv19-cli/build/install/cgv19-cli/bin/cgv19 -m <model> -c <cartridge>
```

Required flag: `-m` (model path, URL, or directory). Optional: `-c` (cartridge name, colon-separated), `-b` (blueprints dir), `-w` (work directory).

Cartridges are discovered via JARs in the `cartridges/` subfolder of the install directory. The startScripts task patches CLASSPATH at build time to include `$APP_HOME/cartridges/*`.

## Cartridge Development

Cartridges can be implemented three ways:
1. **Classic Java** — implement the cartridge interfaces directly
2. **Model-driven** — use `cgv19-metacartridge` with UML models (`.vpp` files), apply `de.spraener.nxtgen.cgV19` Gradle plugin
3. **Annotation-based** — use `@CGV19Cartridge`, `@CGV19Transformation`, `@CGV19Generator` annotations

Model-driven cartridges have generated code in `src/main/java-gen`. The `cgv19-restcartridge` and `cgv19-metacartridge` both use this pattern. Cartridge `build.gradle` files reference core version `23.1.0` on the classpath (mismatch with current core `24.1.1`).

Cartridge settings.gradle auto-discovers modules by listing directories starting with `cgv19-`.

## Testing

- Most modules use **JUnit 5** (`useJUnitPlatform()`) with Mockito + AssertJ
- `cgv19-oom` uses **JUnit 4** (`junit:junit:4.13.1`) — an outlier
- Run single-module tests: `./gradlew :<module>:test` (from the appropriate root)

## CI / Release

- **No build/test CI** on main branch. Only Jekyll docs deploy on master push.
- **Release workflow**: pushes to `release-*` branches trigger `core/./gradlew build` then `publish` to GitHub Packages, plus git tag + GitHub release creation. Requires `GITHUB_USERNAME` and `GITHUB_TOKEN` env vars.

## Publishing

- **Core**: publishes to GitHub Packages (`maven.pkg.github.com/carstenSpraener/cgV19`). Publication name: `gpr`.
- **Cartridges**: publish to local Maven repo at `../repo` (relative to cartridges root). Publication name: `maven`.

## Model Files

`.vpp` = Visual Paradigm project files. `.oom` = cgV19's text-based model format. The VP plugin serves models over HTTP at `http://localhost:7001/<package>`.

## Gotchas

- **No linter/formatter config**: No Checkstyle, Spotless, or similar tooling is configured.
- **`cgv19-annotationprocessor`** has no `build.gradle` — it's an incomplete alpha module.
- **Version mismatch**: core is `24.1.1`, cartridges are `23.1.1`. Cartridge buildscripts reference `23.1.0` core on the classpath.
- **`.gitignore`** excludes `repo/`, `build/`, `.gradle/`, and `**/*-gen` (all generated source dirs, not just metacartridge).
- **VP plugin** (`plugins/cgv19-vpplugin`) targets Java 11 and depends on Visual Paradigm's internal libs via `flatDir`. It has no MCP dependencies — VP tools live in `cgv19-mcp` and communicate via HTTP.
- **Gradle version mismatch**: core uses Gradle 8.10, cartridges use Gradle 8.0.
