# AGENTS – cgV19 Repository Guide

- **Core installation**
  - Build the CLI distribution from the core root:
    ```bash
    cd cgV19/core
    ./gradlew :cgv19-cli:installDist
    ```
  - The built CLI lives in `cgv19-cli/build/install/cgv19-cli/bin/cgV19`.

- **Running a generation**
  - Basic command syntax: `cgV19 -m <model‑path-or‑url> -c <cartridgeName>`
  - Cartridges are discovered via the Java ServiceLoader in `$APP_HOME/cartridges/*`.
  - Use `-d <CartridgeName>` to select a specific cartridge (e.g., `cgv19Gradle`).

- **Model loading**
  - The framework uses the `ModelLoader` SPI (`canHandle(String)` / `loadModel(String)`) to read OOM files, URLs, or Visual‑Paradigm HTTP endpoints (port 7001).
  - The `cgv19-annotationprocessor` module adds a loader for the `java-ap://<dir>` protocol: it compiles Java sources in‑process (JDK required) and builds the OOM from `@Stereotype`‑marked annotations. See `core/cgv19-annotationprocessor/README.md` and the example in `examples/cgv19-annotationprocessor`.
  - Generated source files contain the marker line `THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS`; treat those as generated and avoid manual edits.

- **Build workflow**
  - Each core module is an independent Gradle sub‑project (see `core/settings.gradle`).
  - Build a single module: `./gradlew :cgv19-<module>:build`.
  - Run its tests: `./gradlew :cgv19-<module>:test`.
  - The repository’s top‑level `buildCompleteDist.sh` is **broken** (misses a trailing “t”). Use the manual CLI install steps above instead.

- **Version mismatch**
  - Core version is `24.1.1`; cartridges still reference core `23.1.x`. When developing a cartridge, ensure the classpath uses the current core version to avoid `NoSuchMethodError`s.

- **Generated code conventions**
  - All generated files start with the protection comment `THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS`.
  - The `-d` option deletes generated files before a fresh generation run.

- **Important scripts & docs**
  - Quick‑start guide: `docs/GettingStarted.md`.
  - Core architecture overview: `core/cgv19-core/doc/CoreArchitecture.md`.
  - Cartridge documentation: `cartridges/doc/Cartridges.md`.

- **Common pitfalls**
  - Forgetting to add the `cartridges/` folder to `$APP_HOME` when running the CLI → cartridges not found.
  - Running `gradle :cgv19-cli:installDist` from the repository root instead of `core/` → build fails because the CLI project lives under `core/cgv19-cli`.
  - Assuming `buildCompleteDist.sh` works; it does not.

- **Typical one‑off commands**
  - Install CLI and add to PATH (once):
    ```bash
    cp -r core/cgv19-cli/build/install/cgv19-cli ~/tools/
    echo 'export PATH=$PATH:~/tools/cgv19-cli/bin' >> ~/.zshrc
    ```
  - Generate a PoJo project:
    ```bash
    cgV19 -m my-app.oom -c cgv19PoJo
    ```

- **Service‑loader locations**
  - Core SPI files are under `src/main/resources/META-INF/services/` (e.g., `de.spraener.nxtgen.ModelLoader`). Adding a new cartridge only requires placing its JAR in the `cartridges/` directory and providing the appropriate service‑loader entry.
