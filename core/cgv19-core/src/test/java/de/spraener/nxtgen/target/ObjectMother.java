package de.spraener.nxtgen.target;

import de.spraener.nxtgen.ProtectionStrategieDefaultImpl;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.target.java.JavaSections;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectMother {

    public static final String POJO_CODE = """
            //THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
            package me.test.pkg;
                                    
            import java.util.*;
            public class AClass {
                private String name;
            
                public String getName() {
                    return name;
                }
            
                public void setName(String value) {
                    this.name = value;
                }
            }
            """;

    public static CodeTarget createPoJoTarget(ModelElement nameAttr) {
        CodeTarget target = JavaSections.createJavaCodeTarget("//"+ ProtectionStrategieDefaultImpl.GENERATED_LINE);
        target.getSection(JavaSections.HEADER).get()
                .getLastSnippetForAspect("preamble")
                .insertAfter(new CodeBlockSnippet("java", null, "package me.test.pkg;\n\n"));

        target.getSection(JavaSections.IMPORTS).get().add("PoJo", "import java.util.*;\n");
        target.getSection(JavaSections.CLASS_DECLARATION).get()
                .add(new CodeBlockSnippet("BeginDeclaration", null, "\npublic class AClass "))
                .add(new CodeBlockSnippet("FinishDeclaration", null, "{\n"));
        target.getSection(JavaSections.ATTRIBUTE_DECLARATION).get()
                .add(new CodeBlockSnippet("AttributeDeclaration", nameAttr,
                        "    private String name;\n"));
        target.getSection(JavaSections.METHODS).get()
                .add(new CodeBlockSnippet("Getter", nameAttr, """
                            
                            public String getName() {
                                return name;
                            }
                        """)
                );
        target.getSection(JavaSections.METHODS).get()
                .add(new CodeBlockSnippet("Setter", nameAttr, """
                            
                            public void setName(String value) {
                                this.name = value;
                            }
                        """)
                );
        target.getSection(JavaSections.FOOTER).get()
                .add("CloseClass", "}");
        String code = new CodeTargetToCodeConverter(target).toString();
        assertThat(code)
                .containsIgnoringWhitespaces(POJO_CODE);
        return target;
    }

}
