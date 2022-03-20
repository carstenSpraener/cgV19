package de.spraener.nxtgen.cartridge.rest.transformations;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MOperation;

public class ResourceToContoller implements Transformation {

    @Override
    public void doTransformation(ModelElement element) {
        if( !(element instanceof MClass) ) {
            return;
        }
        if( !((MClass) element).hasStereotype(RESTStereotypes.RESSOURCE.getName()) ) {
            return;
        }
        create((MClass)element);
    }

    public MClass create(MClass mClass) {
        MClass cntrl = mClass.getPackage().createMClass(mClass.getName()+"ControllerBase");
        Stereotype stType = new StereotypeImpl(RESTStereotypes.RESTCONTROLLER.getName());
        cntrl.getStereotypes().add(stType);
        stType.setTaggedValue("dataType", mClass.getFQName());
        for(MOperation op : mClass.getOperations() ) {
            MOperation opClone = op.cloneTo(cntrl);
            String type = ResourceToLogic.toLogicReturnType(opClone.getType());
            opClone.setType(type);
            op.setProperty("returnStatement", "return null;");
        }

        MClass cntrlImpl = mClass.getPackage().createMClass(mClass.getName()+"Controller");
        Stereotype stTypeImpl = new StereotypeImpl(RESTStereotypes.IMPL.getName());
        cntrlImpl.getStereotypes().add(stTypeImpl);
        cntrlImpl.setProperty("extends", cntrl.getFQName());
        cntrlImpl.setProperty("constructorArgs", mClass.getPackage().getName()+".logic."+mClass.getName()+"Logic logic");
        cntrlImpl.setProperty("superCallArgs","logic");
        cntrlImpl.setProperty("annotations",
                "@CrossOrigin(origins = \"*\")\n"+
                "@Controller\n"+
                "@RequestMapping(\"/"+mClass.getName().toLowerCase()+"s\")\n"
        );
        cntrlImpl.setProperty("importList",
                "import org.springframework.stereotype.Controller;\n"
                +"import org.springframework.web.bind.annotation.*;\n"
        );
        return cntrl;
    }
}
