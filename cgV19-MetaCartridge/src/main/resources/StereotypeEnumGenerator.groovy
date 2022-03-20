import de.spraener.nxtgen.NextGen
import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cartridge.meta.MetaCartridge
import de.spraener.nxtgen.model.ModelElement
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

def mClass = (MClass) getProperty("modelElement");

String listStereotypes(MClass mClass) {
    String stList = "";
    Set<String> addedTypes = new HashSet<>();
    for (ModelElement c : ((OOModel) mClass.getModel()).getClassesByStereotype(MetaCartridge.STEREOTYPE_NAME)) {
        if( addedTypes.contains(c.getName()) ) {
            continue;
        }
        addedTypes.add(c.getName());
        if (!"".equals(stList)) {
            stList += ",\n    ";
        }
        stList += "    ${c.name.toUpperCase()}(\"${c.name}\")"
    }
    return stList;
}

String pkgName = mClass.getPackage().getFQName();
String cName = mClass.getName();
NextGen.LOGGER.info("Generating class "+mClass.getFQName()+" in package "+pkgName);

return """// ${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${pkgName};

public enum ${cName} {
    ${listStereotypes(mClass)}
    ;

    private String name;

    ${cName}(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }
}
"""
