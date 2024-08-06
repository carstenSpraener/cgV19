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
import de.spraener.nxtgen.oom.model.MDependency;
import de.spraener.nxtgen.oom.model.OOModel;

@CGV19Component
public class ApiControllerComponent {

    @CGV19Transformation(
            requiredStereotype = "ApiRessource",
            operatesOn = MClass.class
    )
    public void apiControllerGeneratorGapTransformation(ModelElement me) {
        MClass mc = (MClass) me;
        new GeneratorGapTransformation().doTransformation(mc);
        MClass logicClassBase = new ResourceToLogic().create(mc, this::removeConstructorArgsFromImpl);
    }

    private void removeConstructorArgsFromImpl(MClass mClass) {
        if (StereotypeHelper.hasStereotype(mClass, RESTStereotypes.LOGIC.getName())) {
            ((StereotypeImpl) StereotypeHelper.getStereotype(mClass, RESTStereotypes.LOGIC.getName()))
                    .removeTaggedValue("dataType");
        }
        if (StereotypeHelper.hasStereotype(mClass, RESTStereotypes.IMPL.getName())) {
            mClass.setProperty("constructorArgs", "Gson gson");
            mClass.setProperty("superCallArgs","gson");
        }
    }

    public static String getDataType(MClass controllerClass) {
        String dataType = controllerClass.getTaggedValue(RESTStereotypes.RESTCONTROLLER.getName(), "dataType");
        if (dataType == null) {
            MDependency dep = controllerClass.getDependencies().stream()
                    .filter(d -> StereotypeHelper.hasStereotype(d, RESTStereotypes.CONTROLLEDENTITY.getName()))
                    .findFirst()
                    .orElse(null);
            if (dep != null) {
                return dep.getTarget();
            }
        }
        return null;
    }
}
