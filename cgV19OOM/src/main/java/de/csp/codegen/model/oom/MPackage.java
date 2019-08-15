package de.csp.codegen.model.oom;

import de.csp.nxtgen.model.ModelElement;
import de.csp.nxtgen.model.impl.ModelElementImpl;

import java.util.List;
import java.util.stream.Collectors;

public class MPackage extends ModelElementImpl {

    public MPackage() {
    }

    public MPackage(ModelElement me) {
        setName(me.getName());
        setMetaType(me.getMetaType());
        if( me.getParent()!=null ) {
            setName(me.getParent().getName() + "." + me.getName());
        }

        List<MClass> classes = ((ModelElementImpl)me).filterChilds( child -> {
            return child.getMetaType().equals("mClass");
        }).map( child -> {
            MClass result = MClass.from(this, child);
            return result;
        }).collect(Collectors.toList());
        getChilds().addAll(classes);

        List<MPackage> packages = ((ModelElementImpl)me).filterChilds( child -> {
            return child.getMetaType().equals("mPackage");
        }).map( child -> {
            MPackage result = new MPackage(child);
            result.setParent(this);
            result.setName(this.getName()+"."+result.getName());
            return result;
        }).collect(Collectors.toList());
        getChilds().addAll(packages);
    }

    public List<MClass> getClasses() {
        return filterChilds( me -> me instanceof MClass)
                .map(me -> (MClass)me)
                .collect(Collectors.toList());
    }

    public List<MPackage> getPackages() {
        return filterChilds( me -> me instanceof MPackage)
                .map(me -> (MPackage)me)
                .collect(Collectors.toList());
    }

    public List<MClass> getClassesByStereotype(String stereotype, List<MClass> result) {
        for( MClass mClz : getClasses() ) {
            if( mClz.hasStereotype(stereotype) ) {
                result.add(mClz);
            }
        }
        for( MPackage mPkg : this.getPackages() ) {
            mPkg.getClassesByStereotype(stereotype,result);
        }
        return result;
    }

    public MPackage findSubPackageByName(String pkgName) {
        if( this.getName().equals(pkgName) ) {
            return this;
        }
        if( !pkgName.startsWith(this.getName()) ) {
            return null;
        }
        for( MPackage subP : getPackages() ) {
            if( subP.getName().equals(pkgName) ) {
                return subP;
            }
        }
        for( MPackage subP : getPackages() ) {
            MPackage result = subP.findSubPackageByName(pkgName);
            if( result!=null ) {
                return result;
            }
        }
        return null;
    }

    public MPackage createPackage(String name) {
        MPackage child = new MPackage();
        child.setName(getName()+"."+name);
        getChilds().add(child);
        child.setParent(this);

        return child;
    }

    public MClass createMClass(String s) {
        MClass child = new MClass();
        child.setName(s);
        child.setParent(this);
        getChilds().add(child);
        return child;
    }
}
