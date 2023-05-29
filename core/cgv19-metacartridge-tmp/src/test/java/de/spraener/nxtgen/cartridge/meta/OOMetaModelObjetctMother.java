package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.*;

import java.util.function.Consumer;

public class OOMetaModelObjetctMother {
    final OOModel oom = new OOModel();

    public OOModel getModel() {
        return oom;
    }

    public OOModel createDefaultOOModel(Consumer<OOModel>... modifiers) {
        oom.addModelElement(createPackage("pkg"));
        if( modifiers != null ) {
            for (Consumer<OOModel> c : modifiers) {
                c.accept(oom);
            }
        }
        return oom;
    }

    public MPackage createPackage(String name, Consumer<MPackage>... modifiers) {
        MPackage pkg = new MPackage();
        pkg.setName(name);
        pkg.setModel(oom);
        if( modifiers != null ) {
            for (Consumer<MPackage> c : modifiers) {
                c.accept(pkg);
            }
        }
        return pkg;
    }

    public MPackage createSubPackage(MPackage parent, String name, Consumer<MPackage>... modifiers) {
        MPackage pkg = new MPackage();
        pkg.setName(name);
        pkg.setParent(parent);
        parent.addChilds(pkg);
        pkg.setModel(this.oom);
        if( modifiers != null ) {
            for (Consumer<MPackage> c : modifiers) {
                c.accept(pkg);
            }
        }
        return pkg;
    }

    public MClass createClass(MPackage pkg, String name, Consumer<MClass>... modifiers) {
        MClass mc = pkg.createMClass(name);
        mc.setModel(oom);
        if( modifiers != null ) {
            for (Consumer<MClass> c : modifiers) {
                c.accept(mc);
            }
        }
        return mc;
    }

    public MClass createClass(String name, Consumer<MClass>... modifiers) {
        MPackage defaultPkg = oom.getChilds().stream()
                .filter( me -> me.getName().equals("pkg"))
                .map(me -> (MPackage)me)
                .findFirst()
                .orElseThrow();
        return createClass(defaultPkg, name, modifiers);
    }

    public MClass getDefaultMClass() {
        return oom.findClassByName("pkg.DefaultClass");
    }

    public MUsage createUsage( ModelElement parent, MClass target, Consumer<MUsage>... modifiers) {
        MUsage usage = new MUsage();
        usage.setProperty("target", target.getFQName());
        usage.setModel(this.getModel());
        usage.setParent(parent);
        usage.postDefinition();
        if( modifiers != null ) {
            for( Consumer<MUsage> c : modifiers ) {
                c.accept(usage);
            }
        }
        return usage;
    }

    public MAttribute createAttribute(MClass mc, String name, String type, Consumer<MAttribute>... modifiers) {
        MAttribute attr = mc.createAttribute(name, type);
        if( modifiers != null ) {
            for( Consumer<MAttribute> c : modifiers ) {
                c.accept(attr);
            }
        }
        return attr;
    }
}
