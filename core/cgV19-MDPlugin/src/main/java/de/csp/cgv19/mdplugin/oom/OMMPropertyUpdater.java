package de.csp.cgv19.mdplugin.oom;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.VisibilityKindEnum;
import de.spraener.nxtgen.model.ModelElement;

import java.util.*;

public class OMMPropertyUpdater {
    private OOMImport importer;
    private static Map<String, Type> JAVA_TYPES = new HashMap<>();

    public OMMPropertyUpdater(OOMImport importer) {
        this.importer = importer;
    }

    public void update(Property prop, ModelElement attr) {
        prop.setType(findOrCreateType(attr.getProperty("type")));
        switch( attr.getProperty("visibility")) {
            case "private":
                prop.setVisibility(VisibilityKindEnum.PRIVATE);
                break;
            case "protected":
                prop.setVisibility(VisibilityKindEnum.PROTECTED);
                break;
            case "public":
                prop.setVisibility(VisibilityKindEnum.PUBLIC);
                break;
            default:
                prop.setVisibility(VisibilityKindEnum.PACKAGE);
                break;
        }
        this.importer.stereotypesUpdater.update(prop, attr);
    }

    public Type findOrCreateType(String type) {
        if( type.indexOf('.')==-1 ) {
            type = "java.lang."+type;
        }
        if( JAVA_TYPES.get(type)!=null ) {
            return JAVA_TYPES.get(type);
        }
        String pkgName = type.substring(0, type.lastIndexOf('.'));
        String className = type.substring(type.lastIndexOf('.')+1);
        Class result = this.importer.findOrCreateClazz(pkgName, className);
        if(pkgName.startsWith("java") ) {
            JAVA_TYPES.put(className, result);
        }
        return result;
    }
}
