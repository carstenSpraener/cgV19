package de.spraener.nxtgen.blueprint;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.cli.CGV19;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsibility:
 * <p>
 * Each subdirectory of the cgv19/cartridges/blueprints directory will be interpreted as a cartridge name
 * and the cartridge itself will have one blueprint generator with that content.
 * <p>
 * The blueprint-cartridge will also read the parameters referenced in the templates and read them from
 * a local property file. If a value is not present, it will be requested from the user during model reading
 * phase.
 */
public class BluePrintCartridgeCreator {

    public static List<BlueprintDirectoryBasedCartridge> createBlueprintCartridges(String blueprintsPath) {
        File blueprintDir = new File(blueprintsPath);
        List<BlueprintDirectoryBasedCartridge> cartridges = new ArrayList<>();
        if( !blueprintDir.exists() ) {
            CGV19.LOGGER.warning("The specified blueprint directory '"+blueprintsPath+"' does not exist!");
            return cartridges;
        }
        for( File subDir : blueprintDir.listFiles() ) {
            if( subDir.isDirectory() ) {
                cartridges.add(new BlueprintDirectoryBasedCartridge(subDir));
            }
        }
        return cartridges;
    }
}
