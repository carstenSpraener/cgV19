package de.spraener.nxtgen.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.process.JavaExecSpec;

import javax.inject.Inject;
import java.util.List;

abstract class CGV19GenerateTask extends DefaultTask {

    @Input
    abstract public Property<String> getModel();

    /**
     * Resolved cartridge artifacts. Wired to the "cartridge" configuration by the plugin.
     * Using a @Classpath property (instead of Project#getProject() at execution time)
     * keeps this task compatible with the Gradle configuration cache.
     */
    @Classpath
    abstract public ConfigurableFileCollection getCartridgeClasspath();

    @Inject
    protected abstract ExecOperations getExecOperations();

    @Inject
    public CGV19GenerateTask() {
    }

    @TaskAction
    public void resolveLatestVersion() {
        final String nextGen = "de.spraener.nxtgen.NextGen";
        final String model = getModel().get();

        getExecOperations().javaexec((JavaExecSpec execSpec) -> {
            execSpec.getMainClass().set(nextGen);
            execSpec.setClasspath(getCartridgeClasspath());
            execSpec.setArgs(List.of(model));
        });
    }
}
