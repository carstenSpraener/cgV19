import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.model.Stereotype
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = (MClass)this.getProperty("modelElement")
OOModel model = (OOModel)mClass.getModel()

String getMetaType(MClass mClass) {
    Stereotype sType = StereotypeHelper.getStereotype(mClass, "Transformation");
    String metaType = sType.getTaggedValue("transformedMetaType");
    if( metaType==null || "".equals(metaType)) {
        metaType = "ModelElement";
    }
    return metaType;
}
String metaType = getMetaType(mClass)

"""///${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

public class ${mClass.getName()} extends ${mClass.getName()}Base {

    @Override
    public void doTransformationIntern(${metaType} me) {
        //TODO: Implement this transformation
    }
}
"""
