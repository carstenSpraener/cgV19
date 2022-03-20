package entity

import de.spraener.nxtgen.cartridge.rest.RESTStereotypes
import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.model.Stereotype
import de.spraener.nxtgen.cartridge.rest.RESTJavaHelper
import de.spraener.nxtgen.model.impl.ModelElementImpl
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.model.MAssociation
import de.spraener.nxtgen.oom.model.MClass

def findStereotype(eStType, me = modelElement) {
    for(Stereotype sType : me.getStereotypes() ) {
        if( sType.getName().equals(eStType.name) ) {
            return sType;
        }
    }
    return null;
}

String attributeDefinition() {
    StringBuilder sb = new StringBuilder();
    modelElement.attributes.each({
        Stereotype sType = findStereotype(RESTStereotypes.DBFIELD, it);
        columnDef="";
        if( sType!=null ) {
            dbFieldName=sType.getTaggedValue('dbFieldName')
            dbFieldType=sType.getTaggedValue('dbType')
            if( "true".equals(sType.getTaggedValue("isKey")) ) {
                columnDef+="    @Id\n";
                columnDef+="    @GeneratedValue(strategy=GenerationType.SEQUENCE)\n"

            }
            columnDef += "    @Column(name=\"${dbFieldName}\", columnDefinition=\"${dbFieldType}\")\n";
        }
        sb.append("${columnDef}    private ${it.type} ${it.name};\n");
    });

    modelElement.associations.each({
        MAssociation assoc = (MAssociation)it;
        String orAnnotation = mapORAnnotation(assoc);
        if( assoc.getAssociationType().equals("OneToMany") || assoc.getAssociationType().equals("ManyToMany") ) {
            sb.append(
"""
    ${orAnnotation}
    private java.util.Set<${assoc.getType()}> ${assoc.getName()} = null;
""");
        } else {
            sb.append(
"""
    ${orAnnotation}
    private ${assoc.getType()} ${assoc.getName()} = null;
""");
        }
    });
    return sb.toString();
}

String attributeAccess(String cName) {
    StringBuilder sb = new StringBuilder();
    modelElement.attributes.each({
        accName = "${it.name.charAt(0).toUpperCase()}${it.name.substring(1)}";
        sb.append(
"""
    public ${it.type} get${accName}() {
        return this.${it.name};
    }

    public ${cName} set${accName}( ${it.type} value) {
        this.${it.name} = value;
        return this;
    }
"""
        );
    })

    modelElement.associations.each({
        MAssociation assoc = (MAssociation)it;
        String type = "java.util.Set<"+assoc.getType()+">";
        String name = assoc.getName();
        String accessName = name.substring(0,1).toUpperCase()+name.substring(1);
        String oppositeName = assoc.getOpositeAttribute();
        String oppositeAccessName = oppositeName.substring(0,1).toUpperCase() + oppositeName.substring(1);
        String targetClass = assoc.getType().substring(assoc.getType().lastIndexOf('.')+1);
        String setBackRef = "value.set${oppositeAccessName}(this);\n        ";
        String removeBackRef = "";
        if( "ManyToMany".equals(assoc.getAssociationType()) ) {
            setBackRef =
"""if( !value.get${oppositeAccessName}().contains(this) ) {
            value.get${oppositeAccessName}().add(this);
        }
        """;
            removeBackRef =
"""if( value.get${oppositeAccessName}().contains(this) ) {
            value.get${oppositeAccessName}().remove(this);
        }
        """
        }
        if( assoc.getAssociationType().equals("OneToMany") || assoc.getAssociationType().equals("ManyToMany") ) {
            sb.append(
"""
    public ${type} get${accessName}() {
        if( this.${name} == null ) {
            this.${name} = new java.util.HashSet<>();
        }
        return this.${name};
    }

    public ${cName} add${targetClass}(${assoc.getType()} value) {
        get${accessName}().add(value);
        ${setBackRef}return this;
    }

    public ${cName} remove${targetClass}(${assoc.getType()} value) {
        get${accessName}().remove(value);
        ${removeBackRef}return this;
    }
"""
            )
        } else {
            sb.append(
"""
    public ${assoc.getType()} get${accessName}() {
        return this.${name};
    }
    
    public ${cName} set${accessName}(${assoc.getType()} value) {
        this.${name} = value;
        return this;
    }
"""
            );
        }
    });

    return sb.toString();
}

String readDbFieldTaggedValue( em, value ) {
    try {
        return ((ModelElementImpl) em).getTaggedValue(RESTStereotypes.PERSISTENTFIELD.getName(), value);
    } catch( Exception e ) {
        return null;
    }
}

String attrMetaInf() {
    StringBuilder sb = new StringBuilder();
    def boolean first = true;
    def lastAttr = modelElement.attributes.last();
    modelElement.attributes.each({
        def notNull = "true";
        def listed = "true";
        def type = "string";
        def label = it.name;
        if (StereotypeHelper.hasStereotye(it, RESTStereotypes.PERSISTENTFIELD.getName()) ){
            label = readDbFieldTaggedValue(it, "label")
            if (label == null) {
                label = it.name;
            }

            def detailOnly = readDbFieldTaggedValue(it, "detailOnly")
            if (detailOnly == null || "false".equals(detailOnly.toLowerCase())) {
                listed = "true";
            } else {
                listed = "false";
            }

            type = readDbFieldTaggedValue(it, "type")
            if (type == null) {
                type = "string";
            }

            def nullable = readDbFieldTaggedValue(it, "nullable")
            if (nullable == null || "false".equals(nullable.toLowerCase())) {
                notNull = "false";
            }
        }
        def String lineEnd="},\\n";
        if( it==lastAttr ) {
            lineEnd="}\\n";
        }
        sb.append(
                """            sb.append("        {"+
                       "\\"name\\": \\"${it.name}\\","+
                       "\\"type\\": \\"${type}\\","+
                       "\\"listed\\": \\"${listed}\\","+
                       "\\"notNull\\": \\"${notNull}\\","+
                       "\\"label\\": \\"${label}\\""+
            "${lineEnd}");
"""
        );
        first = false;
    });
    return sb.toString();
}

String getExtendsRelation() {
    if( modelElement.inheritsFrom != null ) {
        return " extends ${modelElement.inheritsFrom.getFullQualifiedClassName()} ";
    }
    return "";
}

def pkgName = ((MClass)modelElement).getPackage().getFQName()
def cName = modelElement.name;
def dbTable = findStereotype(RESTStereotypes.ENTITY).getTaggedValue("dbTable")
def extendsStr = getExtendsRelation();

return """// ${ProtectionStrategie.GENERATED_LINE}
package ${pkgName};

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="$dbTable")
public class ${cName}${extendsStr}{
${attributeDefinition()}

    public $cName() {
    }
    
${attributeAccess(cName)}

    private static String __META_INF = null;
    public static String getMetaInfJSON() {
        if( __META_INF==null ) {
            StringBuffer sb = new StringBuffer();
            sb.append("{\\n");
            sb.append("    \\"name\\": \\"${cName}\\",\\n");
            sb.append("    \\"key\\": \\"id\\",\\n");
            sb.append("    \\"properties\\": [\\n");
${attrMetaInf()}
            sb.append("    ]\\n");
            sb.append("}\\n");
            __META_INF = sb.toString();
        }
        return __META_INF;
    }
}
"""

def String mapORAnnotation(MAssociation assoc) {
    StringBuffer sb = new StringBuffer();
    String associationType = assoc.getAssociationType();
    String orphands = assoc.getComposite().equals("true") ? "true": "false"
    String mappedBy = assoc.getOpositeAttribute();
    if( "OneToMany".equals(associationType) ) {
        sb.append(
"""@${associationType}(
        fetch=FetchType.LAZY,
        orphanRemoval=${orphands},
        mappedBy="${mappedBy}",
        cascade = CascadeType.ALL
    )"""
        );
    }
    if( "ManyToMany".equals(associationType) ) {
        sb.append(
"""@${associationType}(
        fetch=FetchType.LAZY,
        cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH}
    )""");
    }
    if( "ManyToOne".equals(associationType) ) {
        sb.append(
"""@${associationType}(
        fetch=FetchType.LAZY
    )""");
    }
    if( "OneToOne".equals(associationType) ) {
        sb.append(
"""@${associationType}(
        fetch=FetchType.LAZY,
        cascade = CascadeType.ALL
    )""");
    }
    return sb.toString();
}
