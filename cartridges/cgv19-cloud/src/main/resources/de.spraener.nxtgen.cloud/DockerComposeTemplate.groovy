import de.spraener.nxtgen.NextGen
import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cloud.CloudCartridge
import de.spraener.nxtgen.cloud.CloudStereotypes
import de.spraener.nxtgen.cloud.DockerComposeCreatorTransformation
import de.spraener.nxtgen.model.Stereotype
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.MPackage
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = DockerComposeCreatorTransformation.getDockerComposeMClass()
OOModel model = mClass.getModel();

String generateDockerService(MPackage cloudModule) {
    String cartridge = cloudModule.getTaggedValue(CloudStereotypes.CLOUDMODULE.name, "cgv19Cartridge");
    if( cartridge==null) {
        cartridge = CloudCartridge.NAME;
    }
    Stereotype sType = StereotypeHelper.getStereotype(cloudModule, CloudStereotypes.CLOUDMODULE.name);
    return NextGen.evaluate(cartridge, cloudModule.getModel(), cloudModule, sType, "docker-compose");
}

String generateServiceList(List<MPackage> pkgList) {
    StringBuilder sb = new StringBuilder();
    for( MPackage cloudModule : pkgList ) {
        sb.append( generateDockerService(cloudModule));
        sb.append("\n");
    }
    return sb.toString();
}

String version = mClass.getTaggedValue(CloudStereotypes.DOCKERCOMPOSEFILE.name, "version");
if( version==null ) {
    version = "3";
}

"""# ${ProtectionStrategieDefaultImpl.GENERATED_LINE}
version: "${version}"

services:
${generateServiceList(DockerComposeCreatorTransformation.getCloudModuleList())}
"""
