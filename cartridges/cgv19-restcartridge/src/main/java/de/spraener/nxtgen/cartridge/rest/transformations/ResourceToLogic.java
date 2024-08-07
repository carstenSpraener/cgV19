package de.spraener.nxtgen.cartridge.rest.transformations;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.*;

import java.util.function.Consumer;

public class ResourceToLogic implements Transformation {

    @Override
    public void doTransformation(ModelElement element) {
        if( !(element instanceof MClass) ) {
            return;
        }
        if( !((MClass) element).hasStereotype(RESTStereotypes.RESOURCE.getName()) ) {
            return;
        }
        create((MClass)element);
    }

    public MClass create(MClass mClass, Consumer<MClass>...clazzModifier) {
        MPackage pkgLogic = mClass.getPackage().findOrCreatePackage("logic");
        MClass logicBase = pkgLogic.createMClass(mClass.getName()+"LogicBase");
        logicBase.setModel(mClass.getModel());
        logicBase.setProperty("originalClass", mClass.getFQName());
        Stereotype stType = new StereotypeImpl(RESTStereotypes.LOGIC.getName());
        logicBase.getStereotypes().add(stType);
        stType.setTaggedValue("dataType", mClass.getFQName()+"Entity");
        for(MOperation op : mClass.getOperations() ) {
            MOperation opClone = op.cloneTo(logicBase);
            String type = toLogicReturnType(opClone.getType());
            opClone.setType(type);
        }
        if( clazzModifier!=null ) {
            for( Consumer<MClass> m : clazzModifier ) {
                m.accept(logicBase);
            }
        }

        MClass logicImpl = pkgLogic.createMClass(mClass.getName()+"Logic");
        Stereotype stTypeLogic = new StereotypeImpl(RESTStereotypes.IMPL.getName());
        logicImpl.getStereotypes().add(stTypeLogic);
        logicImpl.setProperty("extends", logicBase.getFQName());
        logicImpl.setProperty("constructorArgs", mClass.getPackage().getFQName()+".model."+mClass.getName()+"Repository repository, Gson gson");
        logicImpl.setProperty("superCallArgs","repository, gson");
        logicImpl.setProperty("importList", "import org.springframework.stereotype.Component;\nimport com.google.gson.Gson;\n");
        logicImpl.setProperty("annotations", "@Component\n");

        for(MOperation op : mClass.getOperations() ) {
            MOperation opClone = op.cloneTo(logicImpl);
            String type = toLogicReturnType(opClone.getType());
            opClone.setType(type);
            op.setProperty("returnStatement", "return null;");
        }
        if( clazzModifier!=null ) {
            for( Consumer<MClass> m : clazzModifier ) {
                m.accept(logicImpl);
            }
        }

        return logicBase;
    }

    // FIXME: Transform return types of <<Resource>> to <<Entity>>
    public static String toLogicReturnType(String type) {
        if( type.indexOf("de.") > -1 ) {
            return type.substring(0,type.lastIndexOf('.'))+".model."+type.substring(type.lastIndexOf('.')+1);
        }
        return type;
    }
}
