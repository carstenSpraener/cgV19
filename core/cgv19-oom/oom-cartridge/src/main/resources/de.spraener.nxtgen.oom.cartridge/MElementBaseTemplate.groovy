import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.cartridge.GeneratorHelper
import de.spraener.nxtgen.oom.model.MAssociation
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();

def String associationDeclarations(MClass mc) {
    StringBuffer sb = new StringBuffer();
    for(MAssociation assoc : GeneratorHelper.listToNMElementAssociations(mc)) {
        if( !sb.isEmpty() ) {
            sb.append("\n");
        }
        sb.append("  private List<${assoc.type}> ${assoc.name} = null;\n")
    }

    for(MAssociation assoc : GeneratorHelper.listToOneMElementAssociations(mc)) {
        if( !sb.isEmpty() ) {
            sb.append("\n");
        }
        sb.append("  private ${assoc.type} ${assoc.name} = null;\n")
    }

    return sb.toString();
}

String toNGetterBody(MAssociation assoc) {
    String name = assoc.name;
    String type = assoc.type;
    return """
      if( this.${name} == null ) {
        ${name} = filterChilds(child -> child instanceof ${type})
                 .map(child -> (${type})child)
                .collect(Collectors.toList());
      }
      return ${name};
"""
}

def String associationGetter(MClass mc) {
    StringBuffer sb = new StringBuffer();
    for(MAssociation assoc : GeneratorHelper.listToNMElementAssociations(mc)) {
        if( !sb.isEmpty() ) {
            sb.append("\n");
        }
        sb.append("  public List<${assoc.type}> get${GeneratorHelper.firstToUpperCase(assoc.name)}() {")
        sb.append(toNGetterBody(assoc))
        sb.append("  }")
    }
    for(MAssociation assoc : GeneratorHelper.listToOneMElementAssociations(mc)) {
        if( !sb.isEmpty() ) {
            sb.append("\n");
        }
        sb.append(
"""
    public ${assoc.type} get${GeneratorHelper.firstToUpperCase(assoc.name)}() {
        return this.${assoc.name};
    }
    
    public ${mc.name} set${GeneratorHelper.firstToUpperCase(assoc.name)}(${assoc.type} value) {
        this.${assoc.name} = value;
        return this;
    }
""");
    }

    return sb.toString();
}
MClass orgClass = GeneratorGapTransformation.getOriginalClass(mClass);
"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

public class ${mClass.getName()}${GeneratorHelper.extendsStr(mClass, " extends MAbstractModelElement")} {
${associationDeclarations(orgClass)}
${associationGetter(orgClass)}
}
"""
