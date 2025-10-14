# cgv19-maven

This module provides a Maven plugin for integrating cgV19 into Maven projects. The plugin allows you to run cgV19 code generation directly as part of your Maven build process.

## Installation & Usage

Add the plugin to your `pom.xml`:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>de.spraener.nxtgen</groupId>
      <artifactId>cgv19-maven</artifactId>
      <version>${cgv19.version}</version>
      <executions>
        <execution>
          <goals>
            <goal>cgv19</goal>
          </goals>
        </execution>
      </executions>
      <configuration>
        <model>src/main/resources/model.oom</model>
        <outputDir>${project.build.directory}/generated-sources/cgv19</outputDir>
        <cartridges>myCartridge</cartridges>
        <!-- See below for more parameters -->
      </configuration>
    </plugin>
  </plugins>
</build>
```

## Available Parameters

| Parameter         | Description                                                      | Default/Example                                   |
|------------------|------------------------------------------------------------------|---------------------------------------------------|
| model            | Path to the model file or directory (required)                   | src/main/resources/model.oom                      |
| outputDir        | Output directory for generated code                              | ${project.build.directory}/generated-sources/cgv19|
| cartridges       | Names of cartridges to use (colon-separated)                     | myCartridge:otherCartridge                        |
| workDirectory    | Working directory for execution                                  | .                                                 |
| list             | List all known cartridges                                        | false                                             |
| deleteGenerated  | Delete all generated files                                       | false                                             |
| blueprintsDir    | Directory for blueprints                                         | ./cartridges/blueprints                           |
| logLevel         | Log level (ALL, FINER, FINE, INFO, WARNING, SEVERE, OFF)         | SEVERE                                            |
| additionalArgs   | Additional CLI parameters as a string                            |                                                   |

## Phase

The plugin is bound to the `generate-sources` phase by default.

## Example Usage

```sh
mvn cgv19:cgv19 -Dmodel=src/main/resources/model.oom -Dcartridges=myCartridge
```

## Notes
- CLI parameters are identical to those of `cgv19-cli`.

## Development

Build the plugin using:

```sh
./gradlew :cgv19-maven:build
```

The resulting plugin can then be installed locally or deployed to a Maven repository.
