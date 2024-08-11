import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass

import static de.spraener.nxtgen.oom.cartridge.JavaHelper.*

MClass mClass = this.getProperty("modelElement");

def String attribureDeclaration(MClass mc) {
    StringBuilder sb = new StringBuilder();
    for(MAttribute a : mc.getAttributes() ) {
        sb.append("    private ${a.type} ${a.name};\n")
    }
    return sb.toString();
}

def String accessors(MClass mc) {
    StringBuilder sb = new StringBuilder();
    for(MAttribute a : mc.getAttributes() ) {
        String mName = firstToUpperCase(a.getName());
        sb.append(
"""    public ${mc.getName()} set${mName}( ${a.getType()} value ) {
        this.${a.getName()} = value;
        return this;
    }
    
    public ${a.type} get${mName}() {
        return this.${a.name};
    }
"""
        );
    }
    return sb.toString();
}

"""// ${ProtectionStrategie.GENERATED_LINE}

public class ${mClass.getName()} {
${attribureDeclaration(mClass)}
    public ${mClass.getName()} {
    }
    
${accessors(mClass)}
}
"""