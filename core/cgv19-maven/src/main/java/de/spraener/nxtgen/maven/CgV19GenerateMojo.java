package de.spraener.nxtgen.maven;

import de.spraener.nxtgen.cli.CGV19;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

@Mojo(name = "cgv19", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class CgV19GenerateMojo extends AbstractMojo {
    // Beispiel-Parameter, weitere können analog ergänzt werden
    @Parameter(property = "model", required = true)
    private String model;

    @Parameter(property = "outputDir", defaultValue = "${project.build.directory}/generated-sources/cgv19")
    private String outputDir;

    @Parameter(property = "cartridges")
    private String cartridges;

    @Parameter(property = "additionalArgs")
    private String additionalArgs;

    @Parameter(property = "workDirectory")
    private String workDirectory;

    @Parameter(property = "list", defaultValue = "false")
    private boolean list;

    @Parameter(property = "blueprintsDir")
    private String blueprintsDir;

    @Parameter(property = "logLevel")
    private String logLevel;

    @Override
    public void execute() throws MojoExecutionException {
        List<String> args = new ArrayList<>();
        if (workDirectory != null && !workDirectory.isEmpty()) {
            args.add("--work-directory");
            args.add(workDirectory);
        }
        if (list) {
            args.add("--list");
        }
        if (model != null && !model.isEmpty()) {
            args.add("--model");
            args.add(model);
        }
        if (cartridges != null && !cartridges.isEmpty()) {
            args.add("--cartridge");
            args.add(cartridges);
        }
        if (blueprintsDir != null && !blueprintsDir.isEmpty()) {
            args.add("--blueprints-dir");
            args.add(blueprintsDir);
        }
        if (logLevel != null && !logLevel.isEmpty()) {
            args.add("--log-level");
            args.add(logLevel);
        }
        if (additionalArgs != null && !additionalArgs.isEmpty()) {
            for (String arg : additionalArgs.split(" ")) {
                args.add(arg);
            }
        }
        try {
            callCli(args.toArray(new String[0]));
        } catch (Exception e) {
            throw new MojoExecutionException("Fehler beim Ausführen von cgV19", e);
        }
    }

    /**
     * Kapselt den Aufruf von CgV19Cli.main(args) für Testbarkeit.
     */
    protected void callCli(String[] args) throws Exception {
        de.spraener.nxtgen.cli.CGV19.main(args);
    }

    // Getter für Testbarkeit
    public String getModel() { return model; }
    public String getOutputDir() { return outputDir; }
    public String getCartridges() { return cartridges; }
    public String getWorkDirectory() { return workDirectory; }
    public boolean isList() { return list; }
    public String getBlueprintsDir() { return blueprintsDir; }
    public String getLogLevel() { return logLevel; }
    public String getAdditionalArgs() { return additionalArgs; }
}
