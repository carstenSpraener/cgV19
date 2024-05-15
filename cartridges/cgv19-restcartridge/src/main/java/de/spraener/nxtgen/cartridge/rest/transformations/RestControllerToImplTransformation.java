package de.spraener.nxtgen.cartridge.rest.transformations;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;

public class RestControllerToImplTransformation implements Transformation {

    @Override
    public void doTransformation(ModelElement element) {
        if( element instanceof MClass cntrlClass && StereotypeHelper.hasStereotype(element, RESTStereotypes.RESTCONTROLLER.getName())) {
            MClass orgClass = (MClass)cntrlClass.getObject(ResourceToContoller.ORIGINAL_CLASS);
            createImplForController(orgClass, cntrlClass);
        }
    }

    public static void createImplForController(MClass mClass, MClass cntrl) {
        if( !cntrl.getName().endsWith("Base") ) {
            cntrl.setName(cntrl.getName()+"Base");
        }
        MClass cntrlImpl = cntrl.getPackage().createMClass(cntrl.getName().replace("Base", ""));
        String path = cntrlImpl.getName();
        if( path.endsWith("Controller")) {
            path = path.replace("Controller", "");
        }
        path = path.toLowerCase();

        Stereotype stTypeImpl = new StereotypeImpl(RESTStereotypes.IMPL.getName());
        if( mClass!=null ) {
            stTypeImpl.setTaggedValue("dataType", mClass.getFQName());
            String logicName =  mClass.getPackage().getFQName()+".logic."+ mClass.getName()+"Logic logic";
            cntrlImpl.setProperty("constructorArgs", logicName);
            cntrlImpl.setProperty("superCallArgs","logic");
        }
        cntrlImpl.getStereotypes().add(stTypeImpl);
        cntrlImpl.setProperty("extends", cntrl.getName());
        cntrlImpl.setProperty("annotations",
                "@RestController\n"+
                        "@RequestMapping(\"/"+ path+"s\")\n"
        );
        cntrlImpl.setProperty("importList",
                "import org.springframework.web.bind.annotation.*;\n"
        );
    }
}
