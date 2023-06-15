package de.spraener.nxtgen.cartridge.rest.transformations;

import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;

public class DDLTransformation implements Transformation {
    @Override
    public void doTransformation(ModelElement modelElement) {
        if( !(modelElement instanceof MClass) ) {
            return;
        }
        MClass mClass = (MClass)modelElement;
        if( mClass.hasStereotype(RESTStereotypes.RESSOURCE.getName()) ) {
            MClass entity = addEntity(mClass);
            addDDL(entity);
            MClass restController = addRESTController(mClass);
            MClass restLogic = addRESTLogic(mClass);
            MClass tsType = addTSType(mClass);
        }
        if( mClass.hasStereotype(RESTStereotypes.ENTITY.getName()) ) {
            ResourceToEntity.ensureEntityDefinition(mClass);
            addDDL(mClass);
        }
    }

    private MClass addRESTLogic(MClass mClass) {
        return new RessourceToLogic().create(mClass);
    }

    private MClass addEntity(MClass mClass) {
        return new ResourceToEntity().create(mClass);
    }

    private MClass addDDL(MClass entity) {
        System.err.println("Creating DDL for entity "+entity.getName());
        return new EntityToDDL().create(entity);
    }

    private MClass addRESTController(MClass mClass) {
        return new ResourceToContoller().create(mClass);
    }

    private MClass addTSType(MClass mClass) {
        return new ResourceToTSType().create(mClass);
    }
}
