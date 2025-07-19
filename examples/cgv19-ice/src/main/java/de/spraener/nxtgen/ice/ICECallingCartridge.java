package de.spraener.nxtgen.ice;

import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.annotations.CGV19Cartridge;
import de.spraener.nxtgen.filestrategies.GeneralFileStrategy;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;

import java.util.stream.Collectors;

@CGV19Cartridge("ICECallingCartridge")
public class ICECallingCartridge extends ICECallingCartridgeBase{
    public static final String NAME = "ICECallingCartridge";

    public ICECallingCartridge() {
        super();
    }

    public static String evaluateDockerComposeServices(OOModel ooModel) {
        StringBuilder sb = new StringBuilder();
        for( MPackage cloudModule : listDockerServices(ooModel) ) {
            String cartridge = cloudModule.getTaggedValue(CloudStereoTypes.DOCKERSERVICE.getName(), "cgv19Cartridge");
            Stereotype sType = StereotypeHelper.getStereotype(cloudModule, CloudStereoTypes.DOCKERSERVICE.getName());
            sb.append(
                    NextGen.evaluate(cartridge, cloudModule.getModel(), cloudModule, sType, "docker-compose")
            );
        }
        return sb.toString();
    }

    private static Iterable<? extends MPackage> listDockerServices(OOModel ooModel) {
        return ooModel.getModelElements().stream()
                .filter(me->me instanceof MPackage)
                .map(me -> (MPackage)me)
                .filter(me->StereotypeHelper.hasStereotype(me, CloudStereoTypes.DOCKERSERVICE.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected CodeGeneratorMapping createMapping(ModelElement me, String stereotypeName) {
        if( me instanceof MClass mc && stereotypeName.equals(CloudStereoTypes.DOCKERAPPLICATION.getName())) {
            return CodeGeneratorMapping.create(mc, new DockerComposeYmlGenerator(
                    cb -> cb.setToFileStrategy(new GeneralFileStrategy(NextGen.getWorkingDir(),"docker-compose","yml"))
            ));
        }
        return super.createMapping(me, stereotypeName);
    }
}
