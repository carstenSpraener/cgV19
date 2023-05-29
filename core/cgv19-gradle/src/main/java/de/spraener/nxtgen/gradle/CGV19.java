package de.spraener.nxtgen.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.util.List;

public class CGV19 implements Plugin<Project> {

    public void apply(Project p) {
        //p.getExtensions().create("cgV19", CGV19Extension.class);
        p.getConfigurations().create("cartridge");
        Task generateTask = p.getTasks().register("cgV19", CGV19GenerateTask.class).get();
        p.getTasksByName("compileJava", true).forEach(
                t -> t.getDependsOn().add(generateTask)
        );
     }
}
