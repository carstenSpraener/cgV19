package de.spraener.nxtgen.laravel;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.php.PhpHelper;
import de.spraener.nxtgen.symfony.SymfonyStereotypes;
import static de.spraener.nxtgen.php.PhpHelper.*;

public class CreateResourceInfrastructure extends CreateResourceInfrastructureBase {

    @Override
    public void doTransformationIntern(MClass mc) {
        MClass modelClass = getModelClass(mc);
        if( modelClass == null ){
            NextGen.LOGGER.warning("Resource "+mc.getFQName()+" not controlling any entity. Missing <<ControlledEntity>> dependency!");
            return;
        }
        MPackage appRoot = LaravelHelper.getApplicationRoot(mc);
        MPackage rsrcPackage = getRsrcPackage(appRoot, mc);
        MClass rsrc = OOModelBuilder.createMClass(rsrcPackage, mc.getName(),
                c->OOModelBuilder.addStereotype(c, LaravelStereotypes.FILAMENTRESOURCE.getName()),
                c->setOutputDirForModelElement(c,LaravelStereotypes.FILAMENTRESOURCE.getName(), "app/" )
                );
        GeneratorGapTransformation.setOriginalClass(rsrc, mc);
        setResource(modelClass, rsrc);

        MPackage pagesPkg = rsrcPackage.findOrCreatePackage(mc.getName()).findOrCreatePackage("Pages");

        MClass editPage = OOModelBuilder.createMClass(pagesPkg, "Edit"+modelClass.getName(),
                e->OOModelBuilder.addStereotype(e, LaravelStereotypes.FILAMENTPAGE.getName(),
                        "headerActions=Actions\\DeleteAction::make()",
                        "baseResourcePage=Filament\\Resources\\Pages\\EditRecord"
                ),
                e->setOutputDirForModelElement(e,LaravelStereotypes.FILAMENTPAGE.getName(), "app/" ),
                e->setResource(e, rsrc)
                );

        MClass createPage = OOModelBuilder.createMClass(pagesPkg, "Create"+modelClass.getName(),
                e->OOModelBuilder.addStereotype(e, LaravelStereotypes.FILAMENTPAGE.getName(),
                        "baseResourcePage=Filament\\Resources\\Pages\\CreateRecord"
                ),
                e->setOutputDirForModelElement(e,LaravelStereotypes.FILAMENTPAGE.getName(), "app/" ),
                e->setResource(e, rsrc)
        );

        MClass listPage = OOModelBuilder.createMClass(pagesPkg, "List"+LaravelHelper.toPlural(modelClass.getName()),
                e->OOModelBuilder.addStereotype(e, LaravelStereotypes.FILAMENTPAGE.getName(),
                        "headerActions=Actions\\CreateAction::make()",
                        "baseResourcePage=Filament\\Resources\\Pages\\ListRecords"
                ),
                e->setOutputDirForModelElement(e,LaravelStereotypes.FILAMENTPAGE.getName(), "app/" ),
                e->setResource(e, rsrc)
        );

    }

    public static MClass getModelClass(MClass mc) {
        for( MDependency d : mc.getDependencies() ) {
            if( StereotypeHelper.hasStereotype(d, RESTStereotypes.CONTROLLEDENTITY.getName()) ) {
                return (MClass)d.getTargetElement();
            }
        }
        return null;
    }

    private MPackage getRsrcPackage(MPackage appRoot, MClass mc) {
        String rootPkgName = appRoot.getFQName();
        String mcPkg = mc.getPackage().getFQName();
        mcPkg = mcPkg.substring(rootPkgName.length()+1);
        if( mcPkg.contains("http.controllers") ) {
            mcPkg = mcPkg.substring(mcPkg.indexOf("http.controllers")+16);
            if( mcPkg.startsWith(".")) {
                mcPkg = mcPkg.substring(1);
            }
        }
        String rscrPkg = "Filament.Resources." + mcPkg;
        String[] pkgNames = rscrPkg.split("\\.");
        MPackage result = appRoot;
        for( String pkgName : pkgNames ) {
            result = result.findOrCreatePackage(pkgName);
        }
        return result;
    }

    public static void setResource(MClass c, MClass rsrc) {
        c.putObject("Filament.Resource", rsrc);
    }

    public static MClass getResource(MClass c) {
        return (MClass) c.getObject("Filament.Resource");
    }
}
