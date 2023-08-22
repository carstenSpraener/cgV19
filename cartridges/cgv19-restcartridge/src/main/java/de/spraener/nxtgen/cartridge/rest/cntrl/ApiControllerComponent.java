package de.spraener.nxtgen.cartridge.rest.cntrl;

import de.spraener.nxtgen.annotations.CGV19Component;
import de.spraener.nxtgen.annotations.CGV19Transformation;
import de.spraener.nxtgen.cartridge.rest.RESTCartridge;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.cartridge.rest.transformations.ResourceToLogic;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.MClass;

@CGV19Component
public class ApiControllerComponent {

    @CGV19Transformation(
            requiredStereotype = "ApiRessource",
            operatesOn = MClass.class
    )
    public void apiControllerGeneratorGapTransformation(ModelElement me) {
        MClass mc = (MClass)me;
        new GeneratorGapTransformation().doTransformation(mc);
        MClass logicClass = new ResourceToLogic().create(mc);
        ((StereotypeImpl)StereotypeHelper.getStereotype(logicClass, RESTStereotypes.LOGIC.getName()))
                .removeTaggedValue("dataType");
    }
}
