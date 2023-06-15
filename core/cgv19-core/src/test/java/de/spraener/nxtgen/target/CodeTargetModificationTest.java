package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.target.java.JavaAspects;
import de.spraener.nxtgen.target.java.JavaSections;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CodeTargetModificationTest {

    public static final String SERIALIZABLE_CODE = """
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
                                    
            """;

    @Test
    void testCodeTargetModification() {
        ModelElement nameAttr = new ModelElementImpl();

        CodeTarget target = ObjectMother.createPoJoTarget(nameAttr);
        // add Aspect of Serializable to PoJo
        target.getSection(JavaSections.IMPORTS)
                .add(new CodeBlockSnippet(JavaAspects.SERIALIZABLE, null, "import java.io.Serializable;\n"));

        target.getSection(JavaSections.IMPLEMENTS)
                .add(new CodeBlockSnippet(JavaAspects.SERIALIZABLE, null, "Serializable"));

        target.getSection(JavaSections.CLASS_BLOCK_BEGIN)
                .add(new CodeBlockSnippet(JavaAspects.SERIALIZABLE, null, "    private static final long serialVersionUID=1L;\n\n"))
        ;

        assertThat(new CodeTargetToCodeConverter(target).toString())
                .containsIgnoringWhitespaces(
                        SERIALIZABLE_CODE);
    }

    @Test
    void testFluentAPI() {
        ModelElement attr = new ModelElementImpl();
        CodeTarget target = ObjectMother.createPoJoTarget(attr);
        // Make an existing classTarget a Serializable
        target.inContext(JavaAspects.SERIALIZABLE, null, t -> {
            // Add the import to the import section
            t.getSection(JavaSections.IMPORTS)
                    .add(new SingleLineSnippet("import java.io.Serializable;"));

            // add an "implements" to the class declaration
            t.getSection(JavaSections.IMPLEMENTS)
                    .add(new CodeBlockSnippet("Serializable"));

            // add a serialVersionUID as a static fields
            t.getSection(JavaSections.CLASS_BLOCK_BEGIN)
                    .add(new CodeBlockSnippet("    private static final long serialVersionUID=1L;\n\n"));
        });
        assertThat(new CodeTargetToCodeConverter(target).toString())
                .containsIgnoringWhitespaces(SERIALIZABLE_CODE);
    }


    @Test
    void testFluentAPITwoInterfaces() {
        ModelElement attr = new ModelElementImpl();
        CodeTarget target = ObjectMother.createPoJoTarget(attr);
        target.inContext(JavaAspects.SERIALIZABLE, null, t -> {
            t.getSection(JavaSections.IMPLEMENTS)
                    .add(new CodeBlockSnippet("Serializable"))
                    .add(new CodeBlockSnippet("Runnable"))
                    ;
        });
        String code = new CodeTargetToCodeConverter(target).toString();
        assertTrue(code.contains("implements Serializable, Runnable"));
    }
}
