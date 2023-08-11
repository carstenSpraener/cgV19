package de.spraener.nxtgen.cloud;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

import java.util.ArrayList;
import java.util.List;

public class DockerComposeCreatorTransformation extends DockerComposeCreatorTransformationBase {

    private static MClass dockerComposeClass = null;

    @Override
    public void doTransformationIntern(MPackage pkg) {
        if( dockerComposeClass==null ) {
            dockerComposeClass = createDockerComposeClass((OOModel)pkg.getModel());
        }
        getCloudModuleList().add(pkg);
    }

    private MClass createDockerComposeClass(OOModel model) {
        MClass dc = new MClass();
        dc.setName("docker-compose.yml");
        Stereotype sType = new StereotypeImpl(CloudStereotypes.DOCKERCOMPOSEFILE.getName());
        sType.setTaggedValue("version", "3");
        dc.getStereotypes().add(sType);
        model.addModelElement(dc);
        dc.setModel(model);
        dc.putObject("cloudModulesList", new ArrayList<MPackage>());
        return dc;
    }

    public static List<MPackage> getCloudModuleList() {
        return (List<MPackage>)dockerComposeClass.getObject("cloudModulesList");
    }
    public static MClass getDockerComposeMClass() {
        return dockerComposeClass;
    }
}
