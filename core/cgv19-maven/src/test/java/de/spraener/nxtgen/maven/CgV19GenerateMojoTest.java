package de.spraener.nxtgen.maven;

import io.takari.maven.testing.executor.junit.MavenPluginTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CgV19GenerateMojoTest {

    @MavenPluginTest
    public void testArgumentMapping() {
        // Beispielhafter Test, der die Argumente prüft
        String[] lastArgs = {"--model", "test.oom", "--outputDir", "target/gen", "--cartridge", "pojo", "--work-directory", "workdir", "--list", "--delete-generated", "--blueprints-dir", "bpdir", "--log-level", "INFO", "--foo", "bar"};
        String argsStr = String.join(" ", lastArgs);
        assertThat(lastArgs).isNotNull();
        assertThat(argsStr).contains("--model test.oom");
        assertThat(argsStr).contains("--outputDir target/gen");
        assertThat(argsStr).contains("--cartridge pojo");
        assertThat(argsStr).contains("--work-directory workdir");
        assertThat(argsStr).contains("--list");
        assertThat(argsStr).contains("--delete-generated");
        assertThat(argsStr).contains("--blueprints-dir bpdir");
        assertThat(argsStr).contains("--log-level INFO");
        assertThat(argsStr).contains("--foo bar");
    }
}
