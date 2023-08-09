import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cloud.CloudServiceToProjectModuleTransformation
import de.spraener.nxtgen.cloud.model.MComponent
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.MPackage
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();


"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
${includeList(mClass)}
"""

String includeList(MClass mClass) {
    StringBuilder sb = new StringBuilder();
    for( MPackage pkg : CloudServiceToProjectModuleTransformation.getModuleList(mClass) ) {
        sb.append("include (':${CloudServiceToProjectModuleTransformation.toDirName(pkg)}')\n")
    }
    return sb.toString();
}
