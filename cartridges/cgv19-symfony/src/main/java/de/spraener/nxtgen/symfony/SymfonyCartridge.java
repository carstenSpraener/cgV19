package de.spraener.nxtgen.symfony;

import de.spraener.nxtgen.*;
import de.spraener.nxtgen.annotations.CGV19Cartridge;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.cartridge.rest.transformations.EnsureEntityDefinitionsTransformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.php.PhpFileStrategy;
import de.spraener.nxtgen.symfony.controller.PhpControllerBaseGenerator;
import de.spraener.nxtgen.symfony.entities.PhpRepositoryGenerator;

import java.io.File;
import java.util.List;

@CGV19Cartridge("Symfony")
public class SymfonyCartridge extends SymfonyCartridgeBase implements OnEmptyRootDir, AfterEmptyDir {
    public static final String NAME = "Symfony";

    public SymfonyCartridge() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected CodeGeneratorMapping createMapping(ModelElement me, String stereotypeName) {
        if( me instanceof MClass mc && RESTStereotypes.RESTCONTROLLERBASE.getName().equals(stereotypeName) ) {
            return CodeGeneratorMapping.create(me, new PhpControllerBaseGenerator(
                    cb -> cb.setToFileStrategy( new PhpFileStrategy("Controller/Base", mc.getName()+".php"))
            ));
        }

        if( me instanceof MClass mc && RESTStereotypes.REPOSITORY.getName().equals(stereotypeName) ) {
            return CodeGeneratorMapping.create(me, new PhpRepositoryGenerator(
                    cb -> cb.setToFileStrategy( new PhpFileStrategy("Repository", mc.getName()+".php"))
            ));
        }

        return null;
    }

    @Override
    public void onEmptyRootDir(NextGen nextGen, File rootDir) {
        try {
            NextGen.LOGGER.info("Initializing new symfony project. This may take a while...");
            nextGen.executeCommand(rootDir, null, "composer", "-n", "create-project", "symfony/website-skeleton",  ".");
        } catch( Exception e ) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterEmptyRootDir(NextGen nextGen, File rootDir, Model m) {
        try {
            NextGen.LOGGER.info("Initializing required symfony recipes...");
            ModelElement me = ModelHelper.findInStream(m.getModelElements().stream(), e -> e instanceof MPackage && StereotypeHelper.hasStereotype(e, "SymfonyApp"));
            if( me != null ) {
                String requiresList = ((ModelElementImpl)me).getTaggedValue("SymfonyApp", "requiresList");
                if( requiresList!=null ) {
                    for( String req : requiresList.split(",")) {
                        req = req.trim();
                        nextGen.executeCommand(rootDir, null, "composer", "req", req);
                    }
                }
            }
        } catch( Exception e ) {
            throw new RuntimeException(e);
        }
    }
}
