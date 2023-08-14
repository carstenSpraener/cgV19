package de.spraener.nxtgen.cloud;

import de.spraener.nxtgen.CGV19Config;
import de.spraener.nxtgen.cloud.model.MComponent;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

public class CloudModuleRequiredDatabaseTransformation extends CloudModuleRequiredDatabaseTransformationBase {

    @Override
    public void doTransformationIntern(MPackage pkg) {
        String requiredDatabase = pkg.getTaggedValue(CloudStereotypes.CLOUDMODULE.getName(), "requiredDatabase");
        if(  requiredDatabase!=null && !"".equals(requiredDatabase) && !"none".equals(requiredDatabase) ) {
            MPackage parent = (MPackage)pkg.getParent();
            MPackage deploymentPkg = getDeploymenPackage(parent);

            // create the database module
            MPackage dbPackage = parent.findOrCreatePackage(pkg.getName()+"db");
            dbPackage.setModel(pkg.getModel());
            Stereotype sType = new StereotypeImpl(CloudStereotypes.CLOUDMODULE.getName());
            sType.setTaggedValue("dockerRegistry", pkg.getTaggedValue(CloudStereotypes.CLOUDMODULE.getName(),"dockerRegistry"));
            sType.setTaggedValue("cg19Cartridge", CloudCartridge.NAME);
            sType.setTaggedValue("dockerImage", toDockerImage(requiredDatabase));
            sType.setTaggedValue("port", toDbPort(requiredDatabase));
            sType.setTaggedValue("requiredDatabase", "none");
            dbPackage.addStereotypes(sType);

            // create the (cluster-ip) service
            Stereotype componentSType = new StereotypeImpl(CloudStereotypes.CLOUDSERVICE.getName());
            componentSType.setTaggedValue("replicas", "1");
            componentSType.setTaggedValue("dockerImage", toDockerImage(requiredDatabase));
            MComponent service = new MComponent(pkg.getName()+"db", parent,
                    c -> c.addStereotypes(componentSType),
                    c -> c.addStereotypes(new StereotypeImpl(CloudStereotypes.CLOUDDEPLOYABLE.getName())),
                    c -> c.addStereotypes(new StereotypeImpl(CloudStereotypes.CLOUDCLUSTERIPSERVICE.getName())),
                    c -> c.addPort(toDbPort(requiredDatabase)),
                    c -> c.addDependency(dbPackage.getFQName(), CloudStereotypes.PROVIDES.getName())
            );

            deploymentPkg.addChilds(service);
            service.setParent(deploymentPkg);

            // create the persistent volume claim
            ModelElementImpl claim = new ModelElementImpl();
            claim.setName(pkg.getName()+"db");
            Stereotype stClaim = new StereotypeImpl(CloudStereotypes.PERMANENTVOLUME.getName());
            stClaim.setTaggedValue("path", pkg.getName()+"db");
            stClaim.setTaggedValue("size", "2Gi");
            claim.getStereotypes().add(stClaim);
            deploymentPkg.getChilds().add(claim);
            claim.setParent(deploymentPkg);

            // Mount the PVC to the Service
            String stMountName = CloudStereotypes.MOUNTS.getName();
            service.addDependency(deploymentPkg.getFQName()+"."+claim.getName(), CloudStereotypes.MOUNTS.getName(),
                    d -> StereotypeHelper.getStereotype(d, stMountName).setTaggedValue("mountPath", toMountPath(requiredDatabase)),
                    d -> StereotypeHelper.getStereotype(d, stMountName).setTaggedValue("subPath",pkg.getName()+"-data")
                    );
        }
    }

    private static MPackage getDeploymenPackage(MPackage parent) {
        return ModelHelper.streamModelElement(
                        parent.getModel(),
                        e -> e instanceof MPackage && StereotypeHelper.hasStereotype(e, CloudStereotypes.DEPLOYMENT.getName())
                )
                .map(e -> (MPackage) e)
                .findFirst().orElse(parent);
    }

    private String toDockerImage(String requiredDatabase) {
        String configKey = requiredDatabase+".image";
        String dbImage = CGV19Config.definitionOf(configKey);
        if( !dbImage.equals(configKey) ) {
            return dbImage;
        }

        switch( requiredDatabase ) {
            case "MariaDB":
                return "mariadb:10.9.5";
            default:
                return "mariadb:latest";
        }
    }

    private String toMountPath(String requiredDatabase) {
        String configKey = requiredDatabase+".mountPath";
        String dbImage = CGV19Config.definitionOf(configKey);
        if( !dbImage.equals(configKey) ) {
            return dbImage;
        }

        switch( requiredDatabase ) {
            case "MariaDB":
                return "/var/lib/mysql";
            default:
                return null;
        }
    }


    private String toDbPort(String requiredDatabase) {
        String configKey = requiredDatabase+".port";
        String dbImage = CGV19Config.definitionOf(configKey);
        if( !dbImage.equals(configKey) ) {
            return dbImage;
        }

        switch( requiredDatabase ) {
            default:
                return "3306";
        }
    }

}
