package de.spraener.nxtgen.cloud;

import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.cloud.model.MComponent;
import de.spraener.nxtgen.cloud.model.MPort;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.OOMModelLoader;
import de.spraener.nxtgen.oom.model.OOModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class K8SDeploymentGeneratorTest {
    private OOMModelLoader oomModelLoader = new OOMModelLoader();

    @Test
    public void testDeploymentDescriptor() {
        OOModel model = new OOModel();
        ModelElementImpl me = new ModelElementImpl();
        me.setName("HelloWorldService");
        model.addModelElement(me);

        StereotypeImpl sType = new StereotypeImpl(CloudStereotypes.CLOUDSERVICE.getName());
        sType.setTaggedValue("replicas", "2");
        sType.setTaggedValue("dockerImage", "frontend");
        me.addStereotypes(sType);
        ModelElementImpl ePort = new ModelElementImpl();
        ePort.setParent(me);
        ePort.setName("80");
        me.getChilds().add(ePort);

        MComponent c = new MComponent(me);
        MPort port = new MPort(ePort);
        c.addPort(port);

        K8SDeploymentGenerator uut = new K8SDeploymentGenerator();
        String code = uut.resolve(c, "").toCode();
        System.out.println(code);
        Assertions.assertThat(code)
                .contains("replicas: 2");
    }

    @Test
    public void testServiceDescriptor() throws Exception {
        NextGen.setActiveLoader(oomModelLoader);
        OOModel m = (OOModel) oomModelLoader.loadModel("/de.spraener.tinyapp.oom");
        CloudCartridge cartridge = new CloudCartridge();
        cartridge.getTransformations().stream().forEach(
                t -> m.getModelElements().stream().forEach(e -> t.doTransformation(e))
        );
        for (CodeGeneratorMapping mapping : cartridge.mapGenerators(m)) {
            if( mapping.getCodeGen() instanceof K8SServiceGenerator uut ) {
                String code = uut.resolve(mapping.getGeneratorBaseELement(), "").toCode();
                System.out.println(code);
            }
        }
    }
}
