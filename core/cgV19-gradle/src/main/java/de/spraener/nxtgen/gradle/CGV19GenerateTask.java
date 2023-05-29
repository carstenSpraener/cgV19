package de.spraener.nxtgen.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.util.List;

abstract class CGV19GenerateTask extends DefaultTask {

    @Input
    abstract public Property<String> getModel();

    @Inject
    public CGV19GenerateTask() {
    }

    @TaskAction
    public void resolveLatestVersion() {
        final Project p = getProject();
        final String nextGen = "de.spraener.nxtgen.NextGen";
        final String model = getModel().get();
        Configuration cpConfig = p.getConfigurations().getByName("cartridge");

        p.javaexec( (execSpec) -> {
            execSpec.getMainClass().set(nextGen);
            execSpec.setClasspath(p.getConfigurations().getByName("cartridge"));
            execSpec.setArgs( List.of(model) );
        });
    }
}
