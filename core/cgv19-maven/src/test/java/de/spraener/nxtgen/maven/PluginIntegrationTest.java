package de.spraener.nxtgen.maven;

import io.takari.maven.testing.TestResources5;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenPluginTest;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.File;

@MavenVersions({"3.2.3", "3.2.5"})
class PluginIntegrationTest {
    @RegisterExtension
    final TestResources5 resources = new TestResources5();

    private final MavenRuntime maven;

    PluginIntegrationTest(MavenRuntime.MavenRuntimeBuilder mavenBuilder) throws Exception {
        this.maven = mavenBuilder.withCliOptions("-B", "-U").build();
    }

    //@MavenPluginTest
    void test() throws Exception {
        File basedir = resources.getBasedir("it-basic");
        maven.forProject(basedir)
                .withCliOption("-Dproperty=value")
                .withCliOption("-X")
                .execute("deploy")
                .assertErrorFreeLog()
                .assertLogText("some build message");
    }
}