package de.spraener.nxtgen.symfony;

import de.spraener.nxtgen.cartridge.rest.cntrl.ApiControllerComponent;
import de.spraener.nxtgen.cartridge.rest.transformations.ResourceToEntity;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

public class EnsureRepositoryForControlledEntity extends EnsureRepositoryForControlledEntityBase {

    @Override
    public void doTransformationIntern(MClass me) {
        String entityName = ApiControllerComponent.getDataType(me);
        OOModel model = (OOModel) me.getModel();
        MClass entityClass = model.findClassByName(entityName);
        MClass repository = model.findClassByName(entityClass.getFQName()+"Repository");
        if( repository == null ) {
            ResourceToEntity.createReporitoryForEntity(entityClass);
        }
    }
}
