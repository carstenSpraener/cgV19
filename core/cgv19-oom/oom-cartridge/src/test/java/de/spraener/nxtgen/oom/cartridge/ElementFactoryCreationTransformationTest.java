package de.spraener.nxtgen.oom.cartridge;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ElementFactoryCreationTransformationTest {
    private OOModel model;
    private ElementFactoryCreationTransformation uut = new ElementFactoryCreationTransformation();

    @BeforeEach
    void setup() {
        model = OOModelMother.createDefaultModel(
                model-> {
                    ModelElement mClass = OOModelMother.getMClass(model);
                    StereotypeHelper.getStereotype(mClass, OomStereoTypes.MELEMENT.getName()).setTaggedValue(
                            "modelElementName", "roemmelBroemms"
                    );
                }
        );
    }

    @Test
    void testTransformationWithDefinedModelElementName() throws Exception {
        MClass meFactory = model.findClassByName(OOModelMother.MODEL_ROOT_PKG +"."+ElementFactoryCreationTransformation.FACTORY_CLASS_NAME);
        MClass mClass = OOModelMother.getMClass(model);
        assertNotNull(
                meFactory,
                "ModelElementFactory could not be found"
        );
        Assertions.assertThat(ElementFactoryCreationTransformation.getMClassList(meFactory))
                .isNotEmpty()
                .contains(mClass)
        ;
        Assertions.assertThat(ElementFactoryCreationTransformation.getModelClassToModelElementName(mClass).equals("roemmelBroemms"));
    }

    @Test
    void testTransformationWithUndefinedModelElementName() throws Exception {
        model = OOModelMother.createDefaultModel(
                model-> {
                    MPackage rootPkg = OOModelMother.getRootPkg(model);
                    MClass mc = rootPkg.createMClass("MOperation");
                    Stereotype sType = new StereotypeImpl(OomStereoTypes.MELEMENT.getName());
                    mc.addStereotypes(sType);
                }
        );
        MClass mClass = model.findClassByName(OOModelMother.MODEL_ROOT_PKG+".MOperation");
        Assertions.assertThat(ElementFactoryCreationTransformation.getModelClassToModelElementName(mClass).equals("mOperation"));
    }
}
