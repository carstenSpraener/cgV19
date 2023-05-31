import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.model.Stereotype
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = (MClass)this.getProperty("modelElement")
OOModel model = (OOModel)mClass.getModel()

String checkStereotype(MClass mClass) {
    Stereotype sType = StereotypeHelper.getStereotype(mClass, "Transformation");
    String requiredStereotype = sType.getTaggedValue("requiredStereotype");
    if( requiredStereotype!=null && !"".equals(requiredStereotype) ) {
        return """
        if( !StereotypeHelper.hasStereotye(element, "${requiredStereotype}")) {
            return;
        }"""
    } else {
        return "";
    }
}

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

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

public abstract class ${mClass.getName()}Base implements de.spraener.nxtgen.Transformation {

    @Override
    public void doTransformation(ModelElement element) {
        if( !(element instanceof ${metaType}) ) {
            return;
        }${checkStereotype(mClass)}
        doTransformationIntern((${metaType})element);
    }

    public abstract void doTransformationIntern(${metaType} modelElement);
}
"""