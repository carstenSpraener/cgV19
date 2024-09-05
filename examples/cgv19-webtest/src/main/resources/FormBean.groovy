import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.model.Stereotype
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.cartridge.JavaHelper
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass

MClass mClass = this.getProperty("modelElement");

String generateAttr(MAttribute a) {
    Stereotype sType = StereotypeHelper.getStereotype(a, "FormField");
    return """    @FormField(xpath="${sType.getTaggedValue("xpath")}", type = ${sType.getTaggedValue("type")})
    private String ${a.name};
"""
}

String generateAttrs(MClass mc) {
    StringBuffer sb = new StringBuffer();
    for(MAttribute attr : mc.attributes ) {
        sb.append(generateAttr(attr));
    }
    return sb.toString();
}

String generateAttrMethod(MClass mc, MAttribute a) {
    String mName = JavaHelper.firstToUpperCase(a.name);
    return """    public String get${mName}() {
        return this.${a.name};
    }

    public ${mc.name} set${mName}(String value) {
        this.${a.name} = value;
        return this;
    }
    
"""
}

String generateMethods(MClass mc) {
    StringBuffer sb = new StringBuffer();
    for(MAttribute attr : mc.attributes ) {
        sb.append(generateAttrMethod(mc, attr));
    }
    return sb.toString();
}

"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}

public class ${mClass.name} {
${generateAttrs(mClass)}

    public ${mClass.name}() {
    }

${generateMethods(mClass)}
}
"""
