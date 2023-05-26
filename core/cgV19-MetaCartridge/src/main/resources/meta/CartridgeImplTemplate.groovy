import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");


"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

public class ${mClass.getName()} extends ${mClass.getInheritsFrom().getFullQualifiedClassName()} {

    @Override
    public String getName() {
        return "${mClass.getName()}";
    }

}
"""
