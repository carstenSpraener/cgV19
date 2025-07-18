package de.spraener.nxtgen.oom.cartridge;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;

import java.util.function.Consumer;

public class OOModelMother {
    public static final String MODEL_ROOT_PKG = "my.test.model";
    public static final String MCLASS_FQNAME = MODEL_ROOT_PKG +"."+"MClass";

    public static OOModel createDefaultModel(Consumer<OOModel>... modifiers) {
        OOModel model = OOModelBuilder.createModel(
                m -> OOModelBuilder.createPackage(m, MODEL_ROOT_PKG)
        );
        MPackage pkg = (MPackage) ModelHelper.findByFQName(model, MODEL_ROOT_PKG, ".");
        MClass mClass = OOModelBuilder.createMClass(pkg, "MClass",
                c -> OOModelBuilder.createStereotype(c, "MElement",
                        st -> st.setTaggedValue("modelElementName", "mClass")
                )
        );
        MClass mOperation = OOModelBuilder.createMClass(pkg, "MOperation",
                c -> OOModelBuilder.createStereotype(c, "MElement")
        );
        MClass mClassRef = OOModelBuilder.createMClass(pkg, "MClassRef",
                c -> OOModelBuilder.createStereotype(c, "MElement")
        );
        OOModelBuilder.createAssociation(mClass, mOperation, "operations", "1..*");
        OOModelBuilder.createAssociation(mClass, mClassRef, "inheritsFrom", "0..1");
        applyModifiers(model, modifiers);
        runTransformations(new OOMMetaCartridge(), model);

        return model;
    }

    public static MPackage getRootPkg(OOModel model) {
        return (MPackage)model.getChilds().stream().filter(me -> me instanceof MPackage)
                .findFirst().orElse(null);
    }

    public static <T> T applyModifiers(T object, Consumer<T>... modifiers) {
        if( modifiers!=null && modifiers.length>0 ) {
            for(  Consumer<T> mod : modifiers ) {
                mod.accept(object);
            }
        }
        return object;
    }

    public static void runTransformations(Cartridge cartridge, OOModel model ) {
        for( Transformation t : cartridge.getTransformations() ) {
            for( ModelElement me : model.getModelElements() ) {
                t.doTransformation(me);
            }
        }
    }

    public static MClass getMClass(OOModel model) {
        return (MClass)model.findClassByName(MCLASS_FQNAME);
    }
}
