package de.spraener.nxtgen.cli;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.ModelLoader;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.blueprint.BluePrintCartridgeCreator;
import de.spraener.nxtgen.blueprint.BlueprintDirectoryBasedCartridge;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class CGV19 {

    public static final Logger LOGGER = Logger.getLogger(CGV19.class.getName());

    public static void main(String[] args) {
        configLogging();

        Options options = new Options();

        Option workdir = new Option("w", "work-directory", true, "the directory in which cgv19 should run. Default is current directory");
        workdir.setRequired(false);
        options.addOption(workdir);

        Option optList = new Option("l", "list", false, "list all known cartridges of the installation");
        optList.setRequired(false);
        options.addOption(optList);

        Option optModel = new Option("m", "model", true, "the model to operate on. Could be a file, a directory or a URL");
        optModel.setRequired(false);
        options.addOption(optModel);

        Option optDelete = new Option("d", "delete-generated", false, "delete all files that are generated. Means: the files are not protected according to the current protection strategie.");
        optDelete.setRequired(false);
        options.addOption(optDelete);

        Option optCartridge = new Option("c", "cartridge", true, "the names of the cartridges to run seperated by a colon. If not specified cgv19 will run all cartridges.");
        optCartridge.setRequired(false);
        options.addOption(optCartridge);

        Option optBPDir = new Option("b", "blueprints-dir", true, "the directory to search for blueprints. Default ist installdir/cartridges/blueprints");
        optBPDir.setRequired(false);
        options.addOption(optBPDir);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            LOGGER.info("Running in " + new File(".").getAbsolutePath());
            CommandLine cmd = parser.parse(options, args);
            String workDir = cmd.getOptionValue("work-directory");
            if (workDir != null) {
                NextGen.setWorkingDir(workDir);
            }

            if (cmd.hasOption(optDelete)) {
                if (workDir == null) {
                    workDir = ".";
                }
                new DirectoryTreeDeletion(workDir).run();
            }

            String cartridgeName = cmd.getOptionValue("cartridge");
            if (cartridgeName != null) {
                NextGen.runCartridgeWithName(cartridgeName);
            }

            String blueprintDir = cmd.getOptionValue("blueprints-dir");
            if (blueprintDir == null) {
                blueprintDir = getInstallationDir() + "/cartridges/blueprints";
            }

            for (BlueprintDirectoryBasedCartridge c : BluePrintCartridgeCreator.createBlueprintCartridges(blueprintDir)) {
                NextGen.addCartridge(c);
                if (cartridgeName == null || c.getName().equals(cartridgeName)) {
                    NextGen.addModelLoader(c);
                }
            }

            if (cmd.hasOption(optList)) {
                listCartridges();
            }

            if (!cmd.hasOption(optModel)) {
                NextGen.LOGGER.info("No model was defined. Nothing to be done.");
                System.exit(0);
            }

            String model = cmd.getOptionValue("model");
            String[] ngArgs = new String[]{model};
            NextGen.main(ngArgs);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }
    }

    private static void configLogging() {
        System.out.println("Configuring logging");
        String userHome = System.getProperty("user.home");
        System.out.println("    Home directory is "+userHome);
        boolean loggingPropsFound = false;
        for (String path : new String[]{"logging.properties", ".cgv19/logging.properties", userHome + "/.cgv19/logging.properties"}) {
            System.out.println("Trying "+path);
            try {
                File f = new File(path);
                if (f.exists() && f.isFile()) {
                    System.out.println("Reading Logging configuration from "+path);
                    LogManager.getLogManager().readConfiguration(new FileInputStream(f));
                    loggingPropsFound = true;
                    break;
                }
            } catch (IOException xc) {
                xc.printStackTrace();
            }
        }

        if( !loggingPropsFound ) {
            try {
                System.out.println("No logging configuration found. Reading default.");
                LogManager.getLogManager().readConfiguration(CGV19.class.getResourceAsStream("/cgv19-logging.properties"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Configuring logging system finished.");
    }

    private static void listCartridges() {
        List<Cartridge> cartridgeList = NextGen.loadCartridges();
        System.out.println("The current cgv19 installation contains the following cartridges:\n");
        for (Cartridge l : cartridgeList) {
            String isModelLoader = l instanceof ModelLoader ? " (ModelLoader)" : "";
            System.out.printf("    * '%s'%s%n", l.getName(), isModelLoader);
        }
        System.out.println("\nYou can choose one of these with the -d <CartridgeName> option.");
    }

    public static String getInstallationDir() {
        String fqAppPath = System.getProperty("app.path");
        String binDir = fqAppPath.substring(0, fqAppPath.lastIndexOf('/') - 1);
        return binDir.substring(0, binDir.lastIndexOf('/'));
    }
}
