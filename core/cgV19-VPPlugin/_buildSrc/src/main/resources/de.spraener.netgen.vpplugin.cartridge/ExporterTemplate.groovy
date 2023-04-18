import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();

System.out.println(">>>>>>> Running GroovyTemplate on "+mClass.getFQName());

"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}

package ${mClass.getPackage().getFQName()};

public class ${mClass.getName()} extends ${mClass.getName()}Base {
}
"""
