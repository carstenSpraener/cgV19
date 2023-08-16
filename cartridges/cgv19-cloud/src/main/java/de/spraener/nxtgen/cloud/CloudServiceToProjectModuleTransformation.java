package de.spraener.nxtgen.cloud;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.cloud.model.MComponent;
import de.spraener.nxtgen.cloud.model.OOSubModel;
import de.spraener.nxtgen.invocation.NextGenInvocation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

import java.util.ArrayList;
import java.util.List;

public class CloudServiceToProjectModuleTransformation extends CloudServiceToProjectModuleTransformationBase {

    @Override
    public void doTransformationIntern(ModelElement me) {
        if(StereotypeHelper.hasStereotype(me, CloudStereotypes.CLOUDMODULE.getName())) {
            createSubModule((MPackage)me);
        }
    }

    private void createSubModule(MPackage pkg) {
        OOModel oom =(OOModel) pkg.getModel();
        MClass gradleSettings = oom.findClassByName("GradleSettings");
        if( gradleSettings == null ) {
            gradleSettings = new MClass();
            gradleSettings.setModel(oom);
            oom.getChilds().add(gradleSettings);
            gradleSettings.setName("GradleSettings");
            gradleSettings.getStereotypes().add(new StereotypeImpl("GradleSettings"));
            setModuleList(gradleSettings);
        }
        List<MPackage> moduleList = getModuleList(gradleSettings);
        moduleList.add(pkg);
        pkg.getStereotypes().add(new StereotypeImpl(CloudStereotypes.CLOUDMODULE.getName()));
        String cartridgeName = pkg.getTaggedValue(CloudStereotypes.CLOUDMODULE.getName(), "cgv19Cartridge");
        if( cartridgeName == null ) {
            cartridgeName = "REST-Cartridge";
        }
        if( !cartridgeName.equals(CloudCartridge.NAME) ) {
            NextGen.scheduleInvocation(
                    NextGenInvocation
                            .builder()
                            .withWorkdir(NextGen.getWorkingDir() + "/" + toDirName(pkg))
                            .withModel(toSubModel(pkg))
                            .withCartridge(cartridgeName)
                            .build()
            );
        }
    }

    public static List<MPackage> getModuleList(MClass gradleSettings) {
        return (List<MPackage>) gradleSettings.getObject("moduleList");
    }

    private static void setModuleList(MClass gradleSettings) {
        gradleSettings.putObject("moduleList", new ArrayList<MPackage>());
    }

    public static String toDirName(ModelElement mc) {
        StringBuilder sb = new StringBuilder();
        char[] chars = mc.getName().toCharArray();
        for( int i=0; i<chars.length; i++ ) {
            char c = chars[i];
            if( i < chars.length-1 && Character.isLowerCase(c) &&  Character.isUpperCase(chars[i+1]) ) {
                sb.append('-');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    private OOModel toSubModel(ModelElement me) {
        return new OOSubModel(me);
    }
}
