package de.spraener.nxtgen.cli;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.blueprint.BluePrintCartridgeCreator;
import de.spraener.nxtgen.blueprint.BlueprintDirectoryBasedCartridge;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.logging.Logger;

public class CGV19 {

    public static final Logger LOGGER = Logger.getLogger(CGV19.class.getName());

    public static void main(String[] args) {
        Options options = new Options();

        Option workdir = new Option("w", "work-directory", true, "the directory in which cgv19 should run. Default is current directory");
        workdir.setRequired(false);
        options.addOption(workdir);

        Option optModel = new Option("m", "model", true, "the model to operate on. Could be a file, a directory or a URL");
        optModel.setRequired(true);
        options.addOption(optModel);

        Option optCartridge = new Option("c", "cartridge", true, "the names of the cartridge to execute seperated by a colon. If not specified cgv19 will run all cartridges.");
        optCartridge.setRequired(false);
        options.addOption(optCartridge);

        Option optBPDir = new Option("b", "blueprints-dir", true, "the directory to search for blueprints. Default ist installdir/cartridges/blueprints");
        optBPDir.setRequired(false);
        options.addOption(optBPDir);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            LOGGER.info("Running in "+new File(".").getAbsolutePath());
            CommandLine cmd = parser.parse(options, args);
            String workDir = cmd.getOptionValue("work-directory");
            if( workDir!=null ) {
                NextGen.setWorkingDir(workDir);
            }
            String cartridgeName = cmd.getOptionValue("cartridge");
            if( cartridgeName != null ) {
                NextGen.runCartridgeWithName(cartridgeName);
            }
            String model = cmd.getOptionValue("model");
            String[] ngArgs = new String[]{model};
            String blueprintDir =cmd.getOptionValue("blueprints-dir");
            if( blueprintDir == null ) {
                blueprintDir=getInstallationDir()+"/cartridges/blueprints";
            }
            for(BlueprintDirectoryBasedCartridge c : BluePrintCartridgeCreator.createBlueprintCartridges(blueprintDir) ) {
                NextGen.addCartridge(c);
                NextGen.addModelLoader(c);
            }
            NextGen.main(ngArgs);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }
    }

    public static String getInstallationDir() {
        String fqAppPath = System.getProperty("app.path");
        String binDir = fqAppPath.substring(0, fqAppPath.lastIndexOf('/')-1);
        return binDir.substring(0, binDir.lastIndexOf('/'));
    }
}
