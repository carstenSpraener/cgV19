import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.model.Stereotype
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.cartridge.JavaHelper
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass

MClass mClass = this.getProperty("modelElement");

String generateAttr(MAttribute a) {
    Stereotype sType = StereotypeHelper.getStereotype(a, "OptionEnumEntry");
    String value = sType.getTaggedValue("value");
    String display = sType.getTaggedValue("display");
    return "    ${a.name.toUpperCase()}(\"${value}\", \"${display}\")";
}

String generateAttrs(MClass mc) {
    StringBuffer sb = new StringBuffer();
    for(MAttribute attr : mc.attributes ) {
        if( sb.length()>0 ) {
            sb.append(",\n")
        }
        sb.append(generateAttr(attr));
    }
    sb.append("\n    ;")
    return sb.toString();
}

"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}

public enum ${mClass.name} {
${generateAttrs(mClass)}

    private String value;
    private String display;

    public ${mClass.name}(String value, String display) {
        this.value = value;
        this.display = display;
    }

    public String value() {
        return this.value;
    }

    public String display() {
        return this.display;
    }
}
"""
