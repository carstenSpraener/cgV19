package de.spraener.nxtgen.oom;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.*;

import java.util.function.Consumer;

/**
 * This class is intended to be used in JUnit-Tests to create small
 * test models.
 */
public class OOModelBuilder {

    public static OOModel createModel(Consumer<OOModel>... modifiers) {
        OOModel model = new OOModel();
        applyModifiers(model, modifiers);
        return model;
    }

    public static MPackage createPackage(OOModel m, String name, Consumer<MPackage>...modifiers) {
        MPackage pkg = new MPackage();
        pkg.setModel(m);
        pkg.setName(name);
        m.addModelElement(pkg);
        applyModifiers(pkg, modifiers);
        return pkg;
    }

    public static MPackage createPackage(MPackage parent, String name, Consumer<MPackage>...modifiers) {
        MPackage pkg = parent.findOrCreatePackage(name);
        pkg.setModel(parent.getModel());
        pkg.setName(name);
        applyModifiers(pkg, modifiers);
        return pkg;
    }

    public static Stereotype createStereotype(ModelElement me, String name, Consumer<Stereotype>...  modifiers) {
        Stereotype st = new StereotypeImpl(name);
        me.getStereotypes().add(st);
        applyModifiers(st, modifiers);
        return st;
    }

    public static MClass createMClass(MPackage pkg, String name, Consumer<MClass>...modifiers) {
        MClass mc = pkg.createMClass(name);
        mc.setModel( pkg.getModel() );
        applyModifiers(mc, modifiers);
        return mc;
    }

    public static MAssociation createAssociation( MClass from, MClass to, String fromName, String multiplicity, Consumer<MAssociation>... modifiers) {
        MAssociation assoc = new MAssociation();
        assoc.setModel(from.getModel());
        assoc.setName(fromName);
        assoc.setMultiplicity(multiplicity);
        assoc.setParent(from);
        from.getAssociations().add(assoc);
        applyModifiers(assoc, modifiers);
        return assoc;
    }

    public static MClass createOperation(MClass mc, String name, String returnType, Consumer<MOperation>... modifiers) {
        MOperation operation = mc.createOperation(name);
        operation.setModel(mc.getModel());
        operation.setType(returnType);
        applyModifiers(operation, modifiers);
        return mc;
    }

    public static MOperation addParameter(MOperation op, String name, String type, Consumer<MParameter>... modifiers) {
        MParameter p = new MParameter(type);
        p.setName(name);
        p.setModel(op.getModel());
        op.getParameters().add(p);
        op.addChilds(p);
        p.setParent(op);
        applyModifiers(p, modifiers);
        return op;
    }

    public static <T extends ModelElement> T  addStereotype(T me, String stName, String... taggedValueSpecs) {
        StereotypeImpl sType = new StereotypeImpl(stName);
        if( taggedValueSpecs!= null ) {
            for( String tvSpec : taggedValueSpecs ) {
                String[] keyValue = tvSpec.split("=");
                if( keyValue.length != 2 ) {
                    NextGen.LOGGER.warning("Illegal taggedvalue specification: "+tvSpec+". should by key=value");
                    continue;
                }
                if( "<null>".equals(keyValue[1]) ) {
                    keyValue[1] = null;
                }
                sType.setTaggedValue(keyValue[0], keyValue[1]);
            }
        }
        me.getStereotypes().add(sType);
        return me;
    }
    private static void applyModifiers(Object obj, Consumer<? extends Object>[] modifiers) {
        if( modifiers != null ) {
            for( Consumer<? extends Object> modifier : modifiers ) {
                ((Consumer<Object>)modifier).accept(obj);
            }
        }
    }

    public static MClass createSimpleModelForClass(String className) {
        OOModel model = createModel(
                m -> createPackage(m, "de",
                        p -> createPackage(p, "testapp",
                                p2-> createMClass(p2, className)
                        )
                )
        );
        return model.findClassByName("de.testapp."+className);
    }
}
