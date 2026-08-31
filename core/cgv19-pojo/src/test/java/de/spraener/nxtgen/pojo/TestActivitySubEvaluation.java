package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.*;
import de.spraener.nxtgen.cartridges.EvaluationRequest;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MActivity;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.CodeTargetToCodeConverter;
import de.spraener.nxtgen.target.java.JavaSections;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestActivitySubEvaluation {

    private Cartridge activitySupportingCartridgeMock = mock(Cartridge.class);
    private OOModel model;
    private MActivity activity;
    private MClass mc;

    private PoJoCartridge uut = new PoJoCartridge();

    @BeforeEach
    public void setup() {
        model = ModelMother.createModel();
        mc = model.findClassByName("a.APojo");
        activity = new MActivity();
        activity.setName("operation");
        activity.setParent(mc);
        mc.getActivities().add(activity);

        for (Transformation t : new PoJoCartridge().getTransformations()) {
            for (ModelElement me : model.getModelElements()) {
                t.doTransformation(me);
            }
        }

        NextGen.addCartridge(activitySupportingCartridgeMock);
        when(activitySupportingCartridgeMock.subEvaluate(any(EvaluationRequest.class))).thenAnswer(i -> {
            EvaluationRequest req = i.getArgument(0);
            return subEvaluate(req);
        });
        when(activitySupportingCartridgeMock.canHandle(any(EvaluationRequest.class))).thenReturn(true);
    }

    public CodeBlock subEvaluate(EvaluationRequest request) {
        CodeBlock result = null;
        if (request.getAspect().equals(PoJoCartridge.ACTIVITY_ASPECT)) {
            MActivity activity = (MActivity) request.getMe();
            CodeTarget target = request.getCodeTarget();
            target.getSection(JavaSections.IMPORTS).add("fsmImports", "//// Import for the FSM-Implementation\n");
            target.getSection(JavaSections.ATTRIBUTE_DECLARATIONS)
                    .add("fsmFields", request.getMe(), "// required fields for fsm-Implementation of " + activity.getName() + "\n");
            target.getSection(JavaSections.METHODS)
                    .add("fsmMethods", request.getMe(), "// method for fsm-Implementation of " + activity.getName() + "\n");
        }
        return result;
    }

    @Test
    public void testSubEvaluationOfActivityImports() throws Exception {
        CodeBlock cb = new PoJoGenerator().generatePoJoBase(model.findClassByName("a.APojoBase"), "");
        Assertions.assertThat(cb.toCode())
                .containsIgnoringWhitespaces("// Import for the FSM-Implementation")
        ;
    }

    @Test
    public void testSubEvaluationOfActivityImportsOnlyOncePerMClass() throws Exception {
        CodeBlock cb = new PoJoGenerator().generatePoJoBase(model.findClassByName("a.APojoBase"), "");
        Assertions.assertThat(cb.toCode())
                .containsIgnoringWhitespaces("// Import for the FSM-Implementation")
        ;
    }

    @Test
    public void testSubEvaluationOfActivityFields() throws Exception {
        CodeBlock cb = new PoJoGenerator().generatePoJoBase(model.findClassByName("a.APojoBase"), "");
        Assertions.assertThat(cb.toCode())
                .containsIgnoringWhitespaces("// required fields for fsm-Implementation of " + activity.getName())
        ;
    }

    @Test
    public void testSubEvaluationOfActivityImplementation() throws Exception {
        CodeBlock cb = new PoJoGenerator().generatePoJoBase(model.findClassByName("a.APojoBase"), "");
        Assertions.assertThat(cb.toCode())
                .containsIgnoringWhitespaces("// method for fsm-Implementation of " + activity.getName())
        ;
    }

    @Test
    public void testPoJoCartridgeActivityOnJavaCodeTarget() throws Exception {
        CodeBlock cb = new PoJoGenerator().generatePoJoBase(model.findClassByName("a.APojoBase"), "");
        String code = cb.toCode();
        Assertions.assertThat(code)
                .containsIgnoringWhitespaces("// required fields for fsm-Implementation of " + activity.getName())
                .containsIgnoringWhitespaces("// method for fsm-Implementation of " + activity.getName())
                .containsIgnoringWhitespaces("// Import for the FSM-Implementation")
        ;
    }
}
