package de.spraener.nxtgen.cloud;

import de.spraener.nxtgen.cloud.model.MComponent;
import de.spraener.nxtgen.cloud.model.MPort;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;

public class MElementToComponentTransformation extends MElementToComponentTransformationBase {

    @Override
    public void doTransformationIntern(ModelElement me) {
        if(me instanceof ModelElementImpl meImpl) {
            if( "Component".equals(readModelMetaType(meImpl)) ) {
                MComponent comp = new MComponent(meImpl);
                for( ModelElement child : me.getChilds() ) {
                    if( readModelMetaType(child).equals("Port")) {
                        comp.addPort(new MPort((ModelElementImpl) child));
                    }
                }
                if(StereotypeHelper.hasStereotype(comp, CloudStereotypes.CLOUDSERVICE.getName())) {
                    comp.addStereotypes(new StereotypeImpl(CloudStereotypes.CLOUDDEPLOYABLE.getName()));
                    comp.addStereotypes(new StereotypeImpl(CloudStereotypes.CLOUDCLUSTERIPSERVICE.getName()));
                }
            }
        }
    }

    private String readModelMetaType(ModelElement meImpl) {
        return meImpl.getProperty("metaType");
    }


}
