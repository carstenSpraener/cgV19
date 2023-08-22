package de.spraener.nxtgen.cartridge.rest.cntrl;

import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.cartridge.rest.RESTCartridge;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestApiControllerGenerator {
    RESTCartridge uut = new RESTCartridge();

    @Test
    void testTransformations() {
        OOModel model = createDefaultModel();
        runTransformations(model, uut.getTransformations());
        MClass apiCntrl = model.findClassByName("my.rest.api.MyAPIController");
        assertThat(apiCntrl)
                .isNotNull()
        ;
        MClass base = model.findClassByName("my.rest.api.MyAPIControllerBase");
        assertThat(base)
                .isNotNull()
                .has(new Condition<MClass>(mc -> GeneratorGapTransformation.getOriginalClass(mc) != null, "originalClass"))
                .has(new Condition<MClass>(mc -> GeneratorGapTransformation.getOriginalClass(mc).getFQName().equals("my.rest.api.MyAPIController"), "originalClass type"))
                .has(new Condition<MClass>(mc -> StereotypeHelper.hasStereotype(mc, RESTStereotypes.APIRESSOURCE.getName() + "Base"), "base stereotype"))
        ;
        MClass logic = model.findClassByName("my.rest.api.logic.MyAPIControllerLogic");
        assertThat(logic)
                .isNotNull()
                .has(new Condition<MClass>(mc -> StereotypeHelper.hasStereotype(mc, RESTStereotypes.IMPL.getName()), "Logic-Stereotype"))
        ;
        MClass logicBase = model.findClassByName("my.rest.api.logic.MyAPIControllerLogicBase");
        assertThat(logicBase)
                .isNotNull()
                .has(new Condition<MClass>(mc -> StereotypeHelper.hasStereotype(mc, RESTStereotypes.LOGIC.getName()), "LogicBase-Stereotype"))
        ;
    }

    @Test
    void testMappingCreated() {
        OOModel model = createDefaultModel();
        runTransformations(model, uut.getTransformations());
        List<CodeGeneratorMapping> mappingList = uut.mapGenerators(model);
        assertThat(mappingList)
                .filteredOn(mapping -> mapping.getGeneratorBaseELement() instanceof MClass)
                .filteredOn(mapping -> mapping.getCodeGen() instanceof ApiControllerGenerator)
                .hasSize(1);
        assertThat(mappingList)
                .filteredOn(mapping -> mapping.getGeneratorBaseELement() instanceof MClass)
                .filteredOn(mapping -> mapping.getCodeGen() instanceof ApiControllerBaseGenerator)
                .hasSize(1);
    }

    @Test
    void testBaseCodeGenerator() {
        OOModel model = createDefaultModel();
        runTransformations(model, uut.getTransformations());
        MClass baseClass = model.findClassByName("my.rest.api.MyAPIControllerBase");
        ApiControllerBaseGenerator codeGen = new ApiControllerBaseGenerator();
        String code = codeGen.resolve(baseClass, "").toCode();
        assertThat(code)
                .contains("public abstract class MyAPIControllerBase {")
                .contains("private MyAPILogic logic;")
                .contains("private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(MyAPIController.class.getName());")
                .contains("@PostMapping(path = \"/{eMail}\")")
                .contains("public ResponseEntity<String> myHandler(@PathVariable() String eMail) {")
                .contains("   return ResponseEntity.ok().body(this.logic.myHandler(eMail));")
        ;
    }

    private static OOModel createDefaultModel() {
        OOModel model = OOModelBuilder.createModel(
                m -> OOModelBuilder.createPackage(m, "my.rest.api",
                        pkg -> OOModelBuilder.createMClass(pkg, "MyAPIController",
                                c -> c.getStereotypes().add(new StereotypeImpl(RESTStereotypes.APIRESSOURCE.getName())),
                                c -> OOModelBuilder.createOperation(c, "myHandler", "String",
                                        op -> OOModelBuilder.addStereotype(op,
                                                RESTStereotypes.REQUESTHANDLER.getName(),
                                                "path=/",
                                                "method=POST"
                                        ),
                                        op -> OOModelBuilder.addParameter(op, "eMail", "String",
                                                p -> OOModelBuilder.addStereotype(p, "RequestAttribute",
                                                        "name=email",
                                                        "readFrom=Query",
                                                        "default=<null>"
                                                )
                                        )
                                )
                        )
                )
        );
        return model;
    }

    public static void runTransformations(Model model, List<Transformation> transformations) {
        for (ModelElement me : model.getModelElements()) {
            for (Transformation t : transformations) {
                t.doTransformation(me);
            }
        }
    }
}
