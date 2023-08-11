package de.spraener.nxtgen.cartridges;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.SimpleStringCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorWrapperTest {

        public static class MyGenerator implements CodeGenerator {
            private boolean called = false;
                private Consumer<CodeBlock>[] modifieres;

            public   MyGenerator(Consumer<CodeBlock>... modifiers) {
                            this.modifieres = modifiers;
            }
            @Override
            public CodeBlock resolve(ModelElement element, String templateName) {
                called = true;
                return  new SimpleStringCodeBlock("called");
            }
        }

        @Test
    public void testInstanciation() {
            try {
                            GeneratorWrapper uut = new GeneratorWrapper(MyGenerator.class);
                            ModelElement meMock = Mockito.mock(ModelElement.class);
                        String result = uut.resolve(meMock, "").toCode();
                        assertEquals("called\n", result);
            } catch( Throwable t ) {
                t.printStackTrace();
                fail("Resolve throw an exception: "+t);
            }
        }
}
