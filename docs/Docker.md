# Running cgV19 with Docker

cgV19 can be run from a Docker image, which removes the need to install a JDK
or Gradle on the host. The image ships the [cgv19-cli](../core/cgv19-cli/README.md)
distribution and starts the `cgv19` command.

## Using the published image

A prebuilt image is available on Docker Hub:

```bash
docker run --rm \
  -v $(pwd):/out \
  casigoreng/cgv19:24.1.1 \
  /opt/bin/cgv19 --help
```

The container's working directory is `/workdir`. Mount your project (or model)
wherever you like and pass absolute container paths to `cgv19`.

## Building the image yourself

From the repository root:

```bash
docker build -t cgv19:local .
```

The build is a two-stage build:

1. **build** — `eclipse-temurin:17` compiles the CLI with
   `./gradlew :cgv19-cli:installDist`.
2. **runtime** — `eclipse-temurin:17-jre-jammy` copies only the finished
   distribution to `/opt/cgv19`. No Gradle or build tools end up in the final
   image.

The `.dockerignore` keeps the build context small by excluding `.git`, all
`build/` directories, `plugins`, `demoProjects`, `docs`, `examples` and the
local `repo/` Maven repository.

## Using your own cartridges

The CLI discovers cartridges from the `cartridges/` folder inside the
installation. Mount your cartridge jars into that folder:

```bash
docker run --rm \
  -v $(pwd):/out \
  -v $(pwd)/my-cartridges:/opt/cgv19/cartridges \
  casigoreng/cgv19:24.1.1 \
  /opt/bin/cgv19 -m /out/model.oom -c myCartridge -w /out
```

## Typical invocation

```bash
docker run --rm \
  -v $(pwd):/out \
  casigoreng/cgv19:24.1.1 \
  /opt/bin/cgv19 -m /out/model.oom -w /out
```

| Flag | Meaning |
|---|---|
| `-m, --model` | Model to operate on: a file, a directory, or a URL |
| `-c, --cartridge` | Cartridge name(s), colon-separated. Omit to run all |
| `-w, --work-directory` | Directory in which cgV19 runs (default: current dir) |
| `-b, --blueprints-dir` | Directory to search for blueprints |

Run `/opt/bin/cgv19 --help` inside the image for the full option list.
