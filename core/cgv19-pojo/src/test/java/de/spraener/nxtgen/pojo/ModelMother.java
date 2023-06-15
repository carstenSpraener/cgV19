package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.*;

import java.util.function.Consumer;

public class ModelMother {

    public static OOModel createModel() {
        OOModel model = new OOModel();
        MPackage pkgA = new MPackage();
        pkgA.setName("a");
        model.addModelElement(pkgA);
        pkgA.setModel(model);

        MPackage pkgB = new MPackage();
        pkgB.setName("b");
        model.addModelElement(pkgB);
        pkgB.setModel(model);

        MClass clazzA = createClass(pkgA, "APojo", "PoJo",
                mc -> addAttribute(mc, "String", "name"),
                mc -> addAttribute(mc, "String", "surname"),
                mc -> addAttribute(mc, "Integer", "age"),
                mc -> addAssociation(mc, "myBPojo",  createClass(pkgB, "BPojo", "PoJo"), assoc -> assoc.setMultiplicity("1..1")),
                mc -> addAssociation(mc, "myCPoJos",  createClass(pkgB, "CPojo", "PoJo"), assoc -> assoc.setMultiplicity("1..n"))
        );
        return model;
    }
    public static MAssociation addAssociation(MClass mc, String name, MClass target, Consumer<MAssociation>... modifiers) {
        MAssociation assoc = new MAssociation();
        assoc.setModel(mc.getModel());
        assoc.setName(name);
        mc.getAssociations().add(assoc);
        assoc.setParent(mc);
        assoc.setType("aggregation");
        assoc.setType(target.getFQName());
        if (modifiers != null) {
            for (Consumer<MAssociation> c : modifiers) {
                c.accept(assoc);
            }
        }
        return assoc;
    }

    public static MAttribute addAttribute(MClass mc, String type, String name, Consumer<MAttribute>... modifiers) {
        MAttribute attr = mc.createAttribute(name, type);
        attr.setModel(mc.getModel());
        if (modifiers != null) {
            for (Consumer<MAttribute> c : modifiers) {
                c.accept(attr);
            }
        }
        return attr;
    }

    public static MClass createClass(MPackage mPackage, String className, String stereotype, Consumer<MClass>... modifiers) {
        MClass mc = mPackage.createMClass(className);
        mc.setModel(mPackage.getModel());
        mc.addStereotypes(new StereotypeImpl(stereotype));
        if (modifiers != null) {
            for (Consumer<MClass> c : modifiers) {
                c.accept(mc);
            }
        }
        return mc;
    }

}
