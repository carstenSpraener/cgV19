package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OOModel extends ModelImpl {

    public OOModel( Model metaModel ) {
        ModelImpl mmImpl = (ModelImpl)metaModel;
        mmImpl.getChilds().forEach( me -> {
            switch( me.getMetaType() ) {
                case "mClass":
                    getChilds().add(new MClass(me));
                    break;
                case "mPackage":
                    getChilds().add(new MPackage(me));
                    break;
                default:
                    getChilds().add(me);
                    break;
            }
        });
    }

    public List<MClass> getClassesByStereotype( String stereotype ) {
        List<MClass> result = new ArrayList<>();
        for( ModelElement me : this.getChilds() ) {
            if( me instanceof MClass && ((MClass)me).hasStereotype(stereotype)) {
                result.add((MClass)me);
            } else if( me instanceof MPackage ) {
                ((MPackage)me).getClassesByStereotype(stereotype, result);
            }
        }
        return result;
    }

    public MClass findClassByName(String fullQualifiedClassName) {
        if( fullQualifiedClassName.indexOf('.') != -1 ) {
            String pkgName = fullQualifiedClassName.substring(0, fullQualifiedClassName.lastIndexOf('.'));
            String className = fullQualifiedClassName.substring(fullQualifiedClassName.lastIndexOf('.')+1);
            MPackage pkg = findPackageByName(pkgName);
            if( pkg!=null ) {
                return (MClass) pkg.filterChilds(me -> me.getName().equals(className)).findFirst().orElse(null);
            } else {
                return null;
            }
        } else {
            return (MClass) this.getChilds().stream().filter(me -> me.getName().equals(fullQualifiedClassName)).findFirst().orElse(null);
        }
    }

    private MPackage findPackageByName(String pkgName) {
        MPackage resultPkg = (MPackage)this.getChilds().stream()
                .filter(me -> me instanceof MPackage )
                .filter(pkg -> pkg.getName().equals(pkgName))
                .findFirst()
                .orElse(null);
        if(resultPkg==null) {
            List<MPackage> subPkgs = (List<MPackage>)this.getChilds().stream()
                    .filter(me -> me instanceof MPackage )
                    .map( me -> (MPackage)me)
                    .collect(Collectors.toList());
            for( MPackage p : subPkgs ) {
                resultPkg = p.findSubPackageByName(pkgName);
                if( resultPkg!=null ) {
                    break;
                }
            }
        }
        return resultPkg;
    }
}
