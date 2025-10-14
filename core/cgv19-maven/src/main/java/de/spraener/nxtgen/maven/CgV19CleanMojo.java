package de.spraener.nxtgen.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

@Mojo(name = "clean", defaultPhase = LifecyclePhase.CLEAN, requiresProject = true)
public class CgV19CleanMojo extends AbstractMojo {

    @Parameter(property = "workDirectory")
    private String workDirectory;

    @Parameter(property = "logLevel")
    private String logLevel;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<String> args = new ArrayList<>();
        if (workDirectory != null && !workDirectory.isEmpty()) {
            args.add("--work-directory");
            args.add(workDirectory);
        }
        if (logLevel != null && !logLevel.isEmpty()) {
            args.add("--log-level");
            args.add(logLevel);
        }
        args.add("--delete-generated");
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

}
