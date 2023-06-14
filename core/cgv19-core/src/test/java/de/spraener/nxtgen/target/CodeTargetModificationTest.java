package de.spraener.nxtgen.target;

import de.spraener.nxtgen.ProtectionStrategieDefaultImpl;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.target.java.JavaAspects;
import de.spraener.nxtgen.target.java.JavaSections;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeTargetModificationTest {

    @Test
    void testCodeTargetModification() {
        ModelElement nameAttr = new ModelElementImpl();

        CodeTarget target = ObjectMother.createPoJoTarget(nameAttr);
        // add Aspect of Serializable to PoJo
        target.getSection(JavaSections.IMPORTS).get()
                .add(new CodeBlockSnippet(JavaAspects.SERIALIZABLE, null, "import java.io.Serializable;\n"));

        target.getSection(JavaSections.CLASS_DECLARATION)
                .get()
                .getLastSnippetForAspect("BeginDeclaration")
                .insertAfter(new CodeBlockSnippet(JavaAspects.SERIALIZABLE, null, "implements Serializable "));

        target.getSection(JavaSections.CLASS_DECLARATION).get()
                .getLastSnippetForAspect("FinishDeclaration")
                .insertAfter(new CodeBlockSnippet(JavaAspects.SERIALIZABLE, null, "    private static final long serialVersionUID=1L;\n\n"))
        ;

        assertThat(new CodeTargetToCodeConverter(target).toString())
                .containsIgnoringWhitespaces("""
                        //THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
                        package me.test.pkg;
                                                
                        import java.util.*;
                        import java.io.Serializable;
                                                
                        public class AClass implements Serializable {
                            private static final long serialVersionUID=1L;
                                                
                            private String name;
                            public String getName() {
                                return name;
                            }
                            public void setName(String value) {
                                this.name = value;
                            }
                        }
                                                
                        """);
    }
}
