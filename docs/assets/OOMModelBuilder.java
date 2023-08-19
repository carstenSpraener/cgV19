package de.spraener.nxtgen.oom;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MAssociation;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;

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
        assoc.setType(to.getFQName());
        from.getAssociations().add(assoc);
        applyModifiers(assoc, modifiers);
        return assoc;
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
