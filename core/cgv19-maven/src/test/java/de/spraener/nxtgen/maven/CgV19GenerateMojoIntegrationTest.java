package de.spraener.nxtgen.maven;

import java.io.File;

import io.takari.maven.testing.TestMavenRuntime5;
import io.takari.maven.testing.executor.junit.MavenPluginTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.takari.maven.testing.TestResources5;
import io.takari.maven.testing.executor.MavenExecutionResult;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;
import io.takari.maven.testing.executor.MavenVersions;

@MavenVersions({"3.6.3", "3.9.0"})
public class CgV19GenerateMojoIntegrationTest {

    // Die Extension verwendet nun automatisch den Standardpfad src/test/resources/projects
    @RegisterExtension
    final TestResources5 resources = new TestResources5();

    private final MavenRuntime maven;

    public CgV19GenerateMojoIntegrationTest(MavenRuntimeBuilder mavenBuilder) throws Exception {
        this.maven = mavenBuilder.withCliOptions("-B").build();
    }

    @MavenPluginTest
    void testBasicExecution() throws Exception {
        // 'it-basic' wird im Standardpfad gesucht: src/test/resources/projects/it-basic
        File basedir = resources.getBasedir("it-basic");

        MavenExecutionResult result = maven.forProject(basedir)
                .execute("generate-sources");

        result.assertErrorFreeLog();
    }


    @MavenPluginTest
    void testAllArgsExecution() throws Exception {
        // 'it-basic' wird im Standardpfad gesucht: src/test/resources/projects/it-basic
        File basedir = resources.getBasedir("it-allargs");

        MavenExecutionResult result = maven.forProject(basedir)
                .execute("generate-sources");

        result.assertErrorFreeLog();
    }

}