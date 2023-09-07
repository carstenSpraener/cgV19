package de.spraener.nxtgen.laravel;

import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.cartridge.JavaHelper;
import de.spraener.nxtgen.oom.model.MAssociation;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;

import java.util.ArrayList;
import java.util.List;

import static de.spraener.nxtgen.php.PhpHelper.setOutputDirForModelElement;

public class CreateRelationManager extends CreateRelationManagerBase {
    public static final String RELATION_MANAGERS = "RelationManagers";
    public static final String TARGET_MODEL = "TargetModel";

    @Override
    public void doTransformationIntern(MClass rsrc) {
        MClass orgClass = GeneratorGapTransformation.getOriginalClass(rsrc);
        MClass modelClass = CreateResourceInfrastructure.getModelClass(orgClass);
        for (MAssociation assoc : modelClass.getAssociations()) {
            if (LaravelHelper.Associations.isMultiple(assoc.getMultiplicity())) {
                MClass rsrcTargetClass = CreateResourceInfrastructure.getResource(LaravelHelper.Associations.getTarget(assoc));
                createRelationManager(rsrc, rsrcTargetClass, assoc);
            }
        }
    }

    private void createRelationManager(MClass rsrcFrom, MClass rsrcTo, MAssociation assoc) {
        MPackage rmPkg = rsrcFrom.getPackage().findOrCreatePackage("RelationManagers");
        String rmName = JavaHelper.firstToUpperCase(assoc.getName())+"RelationManager";
        MClass relationManager = OOModelBuilder.createMClass(rmPkg, rmName,
                c -> OOModelBuilder.addStereotype(c, LaravelStereotypes.FILAMENTRELATIONMANAGER.getName(),
                        "relationName=" + assoc.getName()
                ),
                c->setOutputDirForModelElement(c,LaravelStereotypes.FILAMENTRELATIONMANAGER.getName(), "app/" ),
                c->setTargetModelClass(c, LaravelHelper.Associations.getTarget(assoc)),
                c->CreateResourceInfrastructure.setResource(c, rsrcFrom)

        );
        addRelationManagerToResource(rsrcFrom, relationManager);
    }

    private void setTargetModelClass(MClass relMgr, MClass modelClass) {
        relMgr.putObject(TARGET_MODEL, modelClass);
    }

    public static MClass getTargetModel(MClass relMgr) {
        return (MClass)relMgr.getObject(TARGET_MODEL);
    }

    private void addRelationManagerToResource(MClass rsrcFrom, MClass relationManager) {
        List<MClass> rmList = (List<MClass>)rsrcFrom.getObject(RELATION_MANAGERS);
        if( rmList == null ) {
            rmList = new ArrayList<>();
            rsrcFrom.putObject(RELATION_MANAGERS, rmList);
        }
        rmList.add(relationManager);
    }

    public static List<MClass> getRelationManagers(MClass rsrc) {
        return (List<MClass>) rsrc.getObject(RELATION_MANAGERS);
    }
}
