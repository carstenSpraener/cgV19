package de.spraener.nxtgen.cli;

import de.spraener.nxtgen.*;
import de.spraener.nxtgen.blueprint.BluePrintCartridgeCreator;
import de.spraener.nxtgen.blueprint.BlueprintDirectoryBasedCartridge;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class CGV19 {

    public static final Logger LOGGER = Logger.getLogger(CGV19.class.getName());

    public static void main(String[] args) {
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

        Option logLevel = new Option(null, "log-level", true, "set the default log level. Possible values: 'ALL', 'FINER', 'FINE', 'INFO', 'WARNING', 'SEVERE' and 'OFF'. Default: 'SEVERE'");
        logLevel.setRequired(false);
        options.addOption(logLevel);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        List<Cartridge> cartridgeList = new ArrayList<>();
        List<ModelLoader> modelLoaderList = new ArrayList<>();
        try {
            CommandLine cmd = parser.parse(options, args);
            Level l = Level.SEVERE;
            if (cmd.hasOption(logLevel)) {
                l = java.util.logging.Level.parse(cmd.getOptionValue(logLevel));
            }
            Logger rootLogger = LogManager.getLogManager().getLogger("");
            rootLogger.setLevel(l);
            LOGGER.setLevel(l);
            NextGen.LOGGER.setLevel(l);
            for (Handler h : rootLogger.getHandlers()) {
                h.setLevel(l);
            }

            LOGGER.info("Running in " + new File(".").getAbsolutePath());
            CGV19RuntimeDefaultImpl cgv19Runtime = new CGV19RuntimeDefaultImpl();
            String workDir = cmd.getOptionValue("work-directory");
            if (workDir != null) {
                cgv19Runtime = new CGV19RuntimeDefaultImpl(workDir);
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
                cartridgeList.add(c);
                if (cartridgeName == null || c.getName().equals(cartridgeName)) {
                    modelLoaderList.add(c);
                }
            }

            if (cmd.hasOption(optList)) {
                listCartridges();
            }

            if (!cmd.hasOption(optModel)) {
                NextGen.LOGGER.severe("No model was defined. Nothing to be done.");
                System.exit(0);
            }

            String model = cmd.getOptionValue("model");
            NextGen ng = NextGen.getInstance(model);
            ng.setCartridgeSupllier(()->cartridgeList);
            ng.setModelloaderSupplier(()->modelLoaderList);
            ng.setCgv19Runtime(cgv19Runtime);
            ng.run();
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }
    }

    private static void listCartridges() {
        List<Cartridge> cartridgeList = NextGen.getInstance("").loadCartridges();
        System.out.println("The current cgv19 installation contains the following cartridges:\n");
        for (Cartridge l : cartridgeList) {
            String isModelLoader = l instanceof ModelLoader ? " (ModelLoader)" : "";
            System.out.printf("    * '%s'%s%n", l.getName(), isModelLoader);
        }
        System.out.println("\nYou can choose one of these with the -c <CartridgeName> option.");
    }

    public static String getInstallationDir() {
        String fqAppPath = System.getProperty("app.path");
        String binDir = fqAppPath.substring(0, fqAppPath.lastIndexOf('/') - 1);
        return binDir.substring(0, binDir.lastIndexOf('/'));
    }
}
