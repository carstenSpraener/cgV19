import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.cartridge.JavaHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.MClassRef
import de.spraener.nxtgen.oom.model.OOModel

import static de.spraener.nxtgen.oom.cartridge.GeneratorHelper.*;

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();
MClass baseClass = GeneratorGapTransformation.getOriginalClass(mClass);

"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

public class ${mClass.getName()}${extendsStr(mClass)} {
}
"""
