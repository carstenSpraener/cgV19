package de.spraener.nxtgen.cartridge.rest.transformations;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;

import java.time.format.ResolverStyle;

import static de.spraener.nxtgen.cartridge.rest.transformations.ResourceToEntity.toTableName;

public class EntityToDDL implements Transformation {

    @Override
    public void doTransformation(ModelElement element) {
        if( !(element instanceof MClass) ) {
            return;
        }
        if( !((MClass) element).hasStereotype(RESTStereotypes.ENTITY.getName()) ) {
            return;
        } else {
            ResourceToEntity.ensureEntityDefinition((MClass)element);
        }
        create((MClass)element);
    }

    public MClass create(MClass mClass) {
        MClass ddl = mClass.getPackage().createMClass(mClass.getName() + "DDL");
        Stereotype sType = new StereotypeImpl(RESTStereotypes.DDL.getName());
        ddl.addStereotypes(sType);
        sType.setTaggedValue("dbTable", toTableName(mClass));
        for(MAttribute a : mClass.getAttributes() ) {
            MAttribute dbAttr = a.cloneTo(ddl);
            dbAttr.addStereotypes(StereotypeHelper.getStereotype(a,RESTStereotypes.DBFIELD.getName()));
        }
        return ddl;
    }
}
