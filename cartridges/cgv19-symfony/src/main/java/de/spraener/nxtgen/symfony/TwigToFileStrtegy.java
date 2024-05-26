package de.spraener.nxtgen.symfony;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.filestrategies.ToFileStrategy;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.MClass;

import java.io.File;

public class TwigToFileStrtegy implements ToFileStrategy {
    private MClass mc;
    private String[] subFolders;
    public TwigToFileStrtegy(MClass mc, String... subFolders) {
        this.mc = mc;
        this.subFolders = subFolders;
    }

    @Override
    public File open() {

        MClass orgClass = GeneratorGapTransformation.getOriginalClass(mc);
        String folder = NextGen.getWorkingDir()+"/templates/";
        if( subFolders!=null) {
            for( String subFolder : this.subFolders ) {
                folder += subFolder+"/";
            }
        }
        return new File(folder+orgClass.getName().toLowerCase()+"/"+mc.getName()+".twig.html");
    }
}
