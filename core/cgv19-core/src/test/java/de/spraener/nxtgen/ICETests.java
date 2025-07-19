package de.spraener.nxtgen;

import de.spraener.nxtgen.cartridges.EvaluationRequest;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.model.impl.ModelImpl;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ICETests {
    private Cartridge requestingCartridge = mock(Cartridge.class);
    private Cartridge handlingCartridge = mock(Cartridge.class);
    private Cartridge nonHandlingCartridge = mock(Cartridge.class);
    private ModelLoader modelLoader = Mockito.mock(ModelLoader.class);
    private ModelImpl testModel = new ModelImpl();
    private ModelElementImpl me = new ModelElementImpl();

    @BeforeEach
    public void setup() {
        ModelImpl testModel = new ModelImpl();
        ModelElementImpl me = new ModelElementImpl();
        me.setModel(testModel);
        me.setMetaType("class");
        me.setProperty("name", "AClass");
        me.getStereotypes().add(new StereotypeImpl("Test"));
        when(modelLoader.loadModel(any())).thenReturn(
                testModel
        );
        when(modelLoader.canHandle(any())).thenReturn(true);
    }

    @Test
    public void testSubEvaluation() throws Exception {
        CodeGenerator codeGenMock = mock(CodeGenerator.class);
        final EvaluationRequest request = new EvaluationRequest(me, new StereotypeImpl("AnyType"), "myAspect", "mySubAspect");
        final ValueHolder<CodeBlock> vhCodeBlock = new ValueHolder<>();

        when(codeGenMock.resolve(any(ModelElement.class), any(String.class))).thenAnswer(i -> {
                ModelElementImpl me = i.getArgument(0);
                String templateName = i.getArgument(1);
                CodeBlock codeBlock = new JavaCodeBlock("./src/main/java", "test", "TestClass");
                codeBlock.addCodeBlock(NextGen.evaluateByAny(request));
                vhCodeBlock.setValue(codeBlock);
                return codeBlock;
            }
        );

        final List<CodeGeneratorMapping> mappingList = Arrays.asList(new CodeGeneratorMapping[]{
                CodeGeneratorMapping.create(me, codeGenMock)
        });
        when(requestingCartridge.mapGenerators(any(Model.class)) ).thenReturn(mappingList);

        NextGen.setWorkingDir("./build");
        NextGen.addCartridge(requestingCartridge);
        NextGen.addCartridge(handlingCartridge);
        NextGen.addCartridge(nonHandlingCartridge);
        NextGen.addModelLoader(modelLoader);
        when(handlingCartridge.canHandle(any())).thenReturn(Boolean.TRUE);
        when(handlingCartridge.subEvaluate(any(EvaluationRequest.class))).thenAnswer(i -> {
            EvaluationRequest req = i.getArgument(0);
            String code = "HelloWorld to Aspect "+req.getAspect()+"."+req.getSubAspect();
            return new SimpleStringCodeBlock(code);
        });
        when(nonHandlingCartridge.canHandle(any())).thenReturn(Boolean.FALSE);
        NextGen.main(new String[]{"-m", "testmodel"});

        CodeBlock cb = vhCodeBlock.getValue();
        assertNotNull(cb);
        Assertions.assertThat(cb.toCode())
                .containsIgnoringWhitespaces("HelloWorld")
                .containsIgnoringWhitespaces("myAspect")
                .containsIgnoringWhitespaces("mySubAspect")
                ;
    }
}
