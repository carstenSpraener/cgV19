package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

import java.util.List;
import java.util.stream.Collectors;

public class MPackage extends MAbstractModelElement {

    public MPackage() {
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

    public MPackage findOrCreatePackage(String name) {
        MPackage child = findSubPackageByName(name);
        if( child == null ) {
            child = new MPackage();
            child.setName(name);
            getChilds().add(child);
            child.setParent(this);
        }
        return child;
    }

    public String getFQName() {
        if( this.getParent() instanceof MPackage ) {
            return ((MPackage)getParent()).getFQName()+ "."+ getName();
        } else {
            return getName();
        }
    }
    public MClass createMClass(String s) {
        MClass child = new MClass();
        child.setName(s);
        child.setParent(this);
        getChilds().add(child);
        return child;
    }
}
