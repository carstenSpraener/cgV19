# Release Notes — cgV19 26.0.0

**Release date:** August 2026  
**Previous release:** 24.1.1

This is a major release focused on **AI-assisted development**, **fluent Groovy DSL for CodeTargets**, and **inter-cartridge evaluation**.

---

## Highlights

- **Chainable Groovy DSL** for CodeTargets — write generators as readable scripts
- **Inter-Cartridge Evaluation (ICE)** — cartridges can now delegate to other cartridges at runtime
- **Activity & FSM support (ALPHA)** — generate state machine methods from model activities (experimental, subject to breaking changes)
- **MCP server enhancements** — Visual Paradigm model modification tools via HTTP
- **Docker & CI/CD improvements** — streamlined builds and publishing

---

## New Features

### Chainable Groovy DSL for CodeTargets

The most significant usability improvement. You can now write generators as fluent Groovy scripts:

```groovy
def MClass mClass = (MClass) modelElement
def ct = new PoJoCodeTargetCreator(mClass).createPoJoTarget()

return ct.setDefaultModelElement(mClass)
    .forAspect('logging') {
        to JavaSections.IMPORTS, "import java.util.logging.Logger;"
        beforeSnippet JavaSections.CONSTRUCTORS, [aspect: 'clazz-default-constructor.close'], 
            "        LOGGER.trace(\"new Instance of ${mClass.name}.\");"
    }
    .evaluate('Logging.groovy')   // load external script
```

**New API methods:**

| Method | Description |
|---|---|
| `CodeTarget.setDefaultModelElement(ME)` | Chainable setter for default model element |
| `CodeTarget.forAspect(String, Closure)` | 2-arg version using defaultModelElement |
| `CodeTarget.evaluate(String scriptPath)` | Load & execute external Groovy scripts |
| `ForAspectDSL.mClass` property | Access model element from within closures |
| `ForAspectDSL.addSection(...)` | Add new sections to existing CodeTargets |

All methods return `this` for fluent chaining. Section IDs accept both `String` and enum values (e.g., `JavaSections.IMPORTS`).

**See:** `examples/cgv19-codetarget/` for the complete demo with `DemoApp.groovy`, `Logging.groovy`, and `Entity.groovy`.

### Inter-Cartridge Evaluation (ICE)

Cartridges can now delegate work to other cartridges at runtime using `EvaluationRequest`:

```java
EvaluationRequest request = new EvaluationRequest(
    activity,           // the model element
    "PoJo",            // required stereotype
    "activity-aspect", // aspect name
    codeTarget         // the target to enhance
);
NextGen.evaluateByAny(request);
```

This enables **composable generators** — a REST cartridge can ask the PoJo cartridge to generate entity classes, or a cloud cartridge can delegate Docker Compose generation.

**See:** `examples/cgv19-ice/` for a complete example with calling and called cartridges.

### ⚠️ Activity & Finite State Machine Support (ALPHA / EXPERIMENTAL)

**This feature is highly experimental and not production-ready.** The API, generated code quality, and model support are all subject to significant change. Use at your own risk and expect breaking changes in future releases.

New model elements for activity diagrams:
- `MActivity` — represents an activity in a class (basic support)
- `MActivityDecision` — decision nodes with guards and transitions (limited)

New generator:
- `PoJoFSMMethodCreator` — generates Java methods from activity diagrams (alpha quality, limited test coverage)

Activities can also be evaluated by other cartridges via ICE. This is a proof-of-concept and the integration path may change.

### MCP Server — Visual Paradigm Model Modification

The MCP server (`cgv19-mcp`) now includes tools for modifying Visual Paradigm models via HTTP:

| Tool | Description |
|---|---|
| `VpGroovyTool` | Execute Groovy scripts in the VP plugin to create/modify model elements |
| `VpModelDiffTool` | Show diff between current VP model and saved baseline |
| `VpSaveBaselineTool` | Save current VP model state as a baseline for future diffs |

These tools communicate with the VP plugin via HTTP on port 7001 — no classpath dependency required. If VP is not running, tools return a clear error message.

### Docker Enhancements

- `cgv19` Docker image uses Eclipse Temurin JDK 17
- Entry point is `/opt/cgv19/bin/cgv19`
- Docker Compose templates in `cgv19-restcartridge` for multi-service applications

---

## API Changes

### Core (`cgv19-core`)

**Added:**
- `EvaluationRequest` class for inter-cartridge evaluation
- `NextGen.evaluateByAny(EvaluationRequest)` method
- `CodeTarget.setDefaultModelElement()`, `getDefaultModelElement()`
- `CodeTarget.forAspect(String, Closure)` — 2-arg overload
- `CodeTarget.evaluate(String)` — external script execution
- `ForAspectDSL.mClass` property
- `ForAspectDSL.addSection(...)` overloads for dynamic section creation
- `GroovyCodeBlockImpl` now binds `mClass` as alias for `modelElement`
- All `ForAspectDSL` methods (`to`, `first`, `beforeSnippet`, `afterSnippet`) now accept `Object` for sectionId (supports enums)
- Groovy closures in `forAspect` use `Closure.DELEGATE_FIRST` resolve strategy

**Changed:**
- `CodeTarget.forAspect(Object, ModelElement, Closure)` now sets `Closure.DELEGATE_FIRST`
- `ForAspectDSL.to()`, `.first()`, `.beforeSnippet()`, `.afterSnippet()` parameter type changed from `String` to `Object` for sectionId
- `SimpleFileWriterCodeBlock` now writes files as UTF-8 (was already in 24.1.1, reinforced)

### OOM Model (`cgv19-oom`)

**Added:**
- `MActivity` class with `name`, `guard`, `transitions` **(ALPHA)**
- `MActivityDecision` class with `guards`, `outgoing transitions` **(ALPHA)**
- `MClass.getActivities()` method **(ALPHA)**
- `MAbstractModelElement` helper methods for parent/child/model connection during creation
- `OOModel.findClassByName(String)` utility method

### PoJo Cartridge (`cgv19-pojo`)

**Added:**
- `PoJoFSMMethodCreator` — generates methods from activity diagrams **(ALPHA)**
- `PoJoCodeTargetCreator.createPoJoTarget()` now evaluates activities via ICE
- `SerializableEnhancer` utility class

**Changed:**
- `ClassFrameTargetCreator.createConstructor()` uses new `forAspect` API with `Closure.DELEGATE_FIRST`
- `PoJoCodeTargetCreator` delegates activity generation to other cartridges via ICE

### MCP Server (`cgv19-mcp`)

**Added:**
- `HttpHelper` — HTTP communication utility for VP plugin
- `VpGroovyTool` — execute Groovy scripts in VP
- `VpModelDiffTool` — show model diffs
- `VpSaveBaselineTool` — save baselines

### Visual Paradigm Plugin (`cgv19-vpplugin`)

**Added:**
- OOM exporters for `MAttribute`, `MOperation`, `MParameter` that write Groovy DSL
- Groovy script execution endpoint for model modification

**Changed:**
- Build system simplified — removed `settings.gradle`, uses parent gradlew
- Plugin XML updated for new endpoints

---

## Build & CI/CD

**Changed:**
- Core Gradle version: 8.10 (unchanged)
- Cartridge Gradle version: 8.0 (unchanged)
- Version centralized in `core/gradle.properties`
- Publishing to GitHub Packages (`maven.pkg.github.com`)
- Automated release workflow on `release-*` branches
- `plugins/` directory is no longer a Gradle root — build individual subprojects only

**Removed:**
- `plugins/settings.gradle` (no longer needed)
- Symfon cartridge dependency from cartridges root (broken build)

---

## New Examples

| Example | Description |
|---|---|
| `cgv19-codetarget` (enhanced) | Chainable Groovy DSL demo with `DemoApp.groovy`, `Logging.groovy`, `Entity.groovy` |
| `cgv19-ice` | Inter-Cartridge Evaluation example with calling and called cartridges, Docker Compose generation |

---

## Bug Fixes

- **UTF-8 file writing** — all generated files are now written as UTF-8
- **Nullpointer safety** — various NPE fixes in model element creation and cartridge evaluation
- **Closure resolve strategy** — `forAspect` closures now use `DELEGATE_FIRST` so DSL methods are found correctly
- **Section ID types** — `ForAspectDSL` methods now accept both String and enum (JavaSections) values
- **Cartridge test paths** — fixed repo-relative model paths in restcartridge tests
- **Gradle plugin publishing** — skip GPR publication for java-gradle-plugin projects to avoid duplicate coordinates

---

## Deprecations & Removals

- `plugins/settings.gradle` — removed, plugins are no longer a Gradle root
- `InterCartridgeEvaluation.java` (old API) — replaced by `ICECallingCartridge` with new `EvaluationRequest` API
- Symfony cartridge dependency — removed from cartridges root due to broken build
- **Activity/FSM features are marked ALPHA** — expect breaking changes in future releases

---

## Migration Guide

### From 24.1.1 to 26.0.0

**If you use `forAspect` with Groovy closures:**
No changes needed — the new `DELEGATE_FIRST` resolve strategy is backward compatible. Your existing closures will work, and you can now use the shorter 2-arg version:

```groovy
// Old (still works):
ct.forAspect('logging', mClass) { to 'imports', "..." }

// New (recommended):
ct.setDefaultModelElement(mClass)
   .forAspect('logging') { to JavaSections.IMPORTS, "..." }
```

**If you use section IDs as strings:**
Still works. You can now also use enum values:
```groovy
// Both work:
to 'imports', "..."
to JavaSections.IMPORTS, "..."
```

**If you use ICE (Inter-Cartridge Evaluation):**
The old `InterCartridgeEvaluation` API is deprecated. Use the new `EvaluationRequest`:
```java
// Old:
InterCartridgeEvaluation.evaluate(activity, "PoJo", ct);

// New:
NextGen.evaluateByAny(new EvaluationRequest(activity, "PoJo", "aspect", ct));
```

**If you use Activity/FSM features (ALPHA):**
These are experimental. The API, model elements, and generated code may change significantly in future releases without notice. Do not use in production systems.

---

## Known Issues

- `cgv19-annotationprocessor` remains incomplete (alpha, no build.gradle)
- **Activity & FSM support is ALPHA** — API and generated code are subject to significant change; not recommended for production use
- Version mismatch between core (24.1.1 on classpath) and cartridges (23.1.1) — cartridge buildscripts still reference 23.1.0
- `buildCompleteDist.sh` is broken (missing trailing 't' in command) — use manual gradle commands instead

---

## Contributors

- Carsten Spräner (lead developer)
- AI-assisted development via MCP server and opencode agents

---

## Installation

### Via Gradle Plugin
```groovy
plugins {
    id 'de.spraener.nxtgen.cgV19' version '26.0.0'
}
```

### Via Docker
```bash
docker pull ghcr.io/carstenspraener/cgv19:26.0.0
docker run cgv19 -m /path/to/model.oom -c pojo
```

### Via CLI
Download from GitHub Releases and run:
```bash
./cgv19 -m /path/to/model.oom -c pojo,restcartridge
```

---

## Links

- [GitHub Repository](https://github.com/carstenSpraener/cgV19)
- [Docker Documentation](docs/Docker.md)
- [MCP Server Documentation](docs/McpServer.md)
- [CodeTarget Example](examples/cgv19-codetarget/Readme.md)
- [ICE Example](examples/cgv19-ice/Readme.md)
