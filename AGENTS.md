# cgV19 — Agent Instructions

## Project at a Glance

cgV19 is a model-driven code generation framework. It reads UML-style models (from Visual Paradigm via port 7001, or `.oom` files) and runs **cartridges** (generation plugins) to produce code, docs, and config artifacts.

- **Language**: Java 17, Groovy templates
- **Build**: Gradle 8.0 wrapper
- **Group/version**: `de.spraener.nxtgen` / `24.1.0` (core), `23.1.1` (cartridges)

## Repo Structure — Three Independent Gradle Roots

| Root | Purpose | Build independently? |
|---|---|---|
| `core/` | Framework: core engine, OOM model loader, CLI, Gradle plugin, JavaPoet support, metacartridge, pojo example | Yes — `./gradlew` inside `core/` |
| `cartridges/` | Generation cartridges (restcartridge, angular, javalin, cloud, symfony, laravel) | Yes — `./gradlew` inside `cartridges/` |
| `plugins/` | Visual Paradigm plugin + its cartridge | Yes — `./gradlew` inside `plugins/` |

**Important**: `core/settings.gradle` cross-includes cartridges via `includeCartridge()` so building `core/` also builds cartridges. But `cartridges/` and `plugins/` are separate Gradle roots with their own `settings.gradle` and `build.gradle`.

## Core Modules (inside `core/`)

| Module | Role |
|---|---|
| `cgv19-core` | Base framework, main class `de.spraener.nxtgen.NextGen`. Uses Java ServiceLoader for ModelLoaders and cartridges. |
| `cgv19-oom` | Object-oriented model implementation (packages, classes, attributes, relations). Reads `.oom` files or VP REST API. |
| `cgv19-cli` | Standalone CLI tool. Main class `de.spraener.nxtgen.cli.CGV19`. Also has MCP server `CGV19Mcp`. |
| `cgv19-gradle` | Gradle plugin (`de.spraener.nxtgen.gradle.CGV19`). |
| `cgv19-javapoet` | JavaPoet code generator integration. |
| `cgv19-metacartridge` | Meta-cartridge for model-driven cartridge development. **Has generated source**: `src/main/java-gen` is a `srcDir`. |
| `cgv19-pojo` | Simple example cartridge. |
| `cgv19-annotationprocessor` | ALPHA — proof of concept for Java annotation processor integration. |

## Key Build Commands

All Gradle commands run from the `core/` directory unless noted.

```bash
# Build everything (core + cross-included cartridges)
cd core && ./gradlew build

# Build a single module
cd core && ./gradlew :cgv19-core:build

# Run tests for a module
cd core && ./gradlew :cgv19-core:test

# Build the standalone CLI distribution
cd core && ./gradlew :cgv19-cli:installDist
# Output: core/cgv19-cli/build/install/cgv19-cli/

# Full clean + CLI dist (root script)
./buildCompleteDist.sh

# Build cartridges standalone
cd cartridges && ./gradlew build

# Build plugins standalone
cd plugins && ./gradlew build
```

## CLI Usage

After building the dist:
```bash
core/cgv19-cli/build/install/cgv19-cli/bin/cgv19 -m <model> -c <cartridge>
```

Required flag: `-m` (model path, URL, or directory). Optional: `-c` (cartridge name, colon-separated), `-b` (blueprints dir), `-w` (work directory).

Cartridges are discovered via JARs in the `cartridges/` subfolder of the install directory. The CLASSPATH is patched at build time to include `$APP_HOME/cartridges/*`.

## Cartridge Development

Cartridges can be implemented three ways:
1. **Classic Java** — implement the cartridge interfaces directly
2. **Model-driven** — use `cgv19-metacartridge` with UML models (`.vpp` files)
3. **Annotation-based** — use `@CGV19Cartridge`, `@CGV19Transformation`, `@CGV19Generator` annotations

Model-driven cartridges have generated code in `src/main/java-gen`. The `cgv19-restcartridge` and `cgv19-metacartridge` both use this pattern.

## Testing

- Most modules use **JUnit 5** (`useJUnitPlatform()`) with Mockito + AssertJ
- `cgv19-oom` uses **JUnit 4** (`junit:junit:4.13.1`) — an outlier
- Run single-module tests: `./gradlew :<module>:test`

## Model Files

`.vpp` = Visual Paradigm project files. `.oom` = cgV19's text-based model format. The VP plugin serves models over HTTP at `http://localhost:7001/<package>`.

## Publishing

Both `core/` and `cartridges/` publish to a local Maven repo at `../repo` (relative to each root). The `core/` root uses publication name `gpr`, `cartridges/` uses `maven`.

## Gotchas

- **No CI for builds**: The only GitHub Actions workflow deploys Jekyll docs — there's no build/test CI pipeline.
- **No linter/formatter config**: No Checkstyle, Spotless, or similar tooling is configured.
- **`cgv19-annotationprocessor`** has no `build.gradle` — it's an incomplete alpha module.
- **Version mismatch**: core is `24.1.0`, cartridges are `23.1.1`. Cartridge `build.gradle` files reference `23.1.0` core on the classpath.
- **`buildCompleteDist.sh`** runs from the repo root, not from `core/`.
- **`.gitignore`** excludes `repo/` (local Maven repo), `build/`, `.gradle/`, and generated cartridge resources under `core/cgv19-metacartridge/src/main/resources/meta/**`.
