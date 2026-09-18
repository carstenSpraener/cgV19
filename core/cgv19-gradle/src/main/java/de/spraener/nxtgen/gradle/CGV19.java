package de.spraener.nxtgen.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskProvider;

public class CGV19 implements Plugin<Project> {

    public void apply(Project p) {
        //p.getExtensions().create("cgV19", CGV19Extension.class);
        Configuration cartridge = p.getConfigurations().create("cartridge");
        TaskProvider<CGV19GenerateTask> generateTask = p.getTasks().register("cgV19", CGV19GenerateTask.class);
        generateTask.configure(task -> task.getCartridgeClasspath().from(cartridge));
        p.getTasksByName("compileJava", true).forEach(
                t -> t.dependsOn(generateTask)
        );
    }
}
