package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeGeneratorGeneratorTests extends AbstractOOModelTest {
    public static final String STANDARD_HEADER = """
            // THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
            package pkg;

            import java.util.function.Consumer;

            import de.spraener.nxtgen.CodeBlock;
            import de.spraener.nxtgen.CodeGenerator;
            import de.spraener.nxtgen.GroovyCodeBlockImpl;
            import de.spraener.nxtgen.java.JavaCodeBlock;
            import de.spraener.nxtgen.model.ModelElement;
            import de.spraener.nxtgen.oom.model.*;
            """;
    public static final String STANDARD_CONSTRUCTOR = """
            public class AGenerator implements CodeGenerator {
                private Consumer<CodeBlock>[] codeBlockModifiers;

                public AGenerator(Consumer<CodeBlock>... codeBlockModifiers) {
                    this.codeBlockModifiers = codeBlockModifiers;
                }
            """;
    public static final String RESOLVE_METHOD_SIGNATURE =
            """
                        @Override
                        public CodeBlock resolve(ModelElement element, String templateName) {
                    """;
    public static final String RESOLVE_METHOD_DEFAULT_PART = """
                    GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("AGenerator", me, "/Template.groovy");
                    jcb.addCodeBlock(gcb);

                    if( codeBlockModifiers!=null ) {
                        for (Consumer<CodeBlock> codeBlockModifier: this.codeBlockModifiers) {
                            codeBlockModifier.accept(jcb);
                        }
                    }

                    return jcb;
            """;

    private CodeGeneratorGenerator uut = new CodeGeneratorGenerator();
    private StereotypeImpl stGen = new StereotypeImpl(MetaCartridge.STEREOTYPE_CODE_GENERATOR);

    @BeforeEach
    public void setup() {
        super.setup();
        stGen.setTaggedValue("generatesOn", "MClass");
        stGen.setTaggedValue(MetaCartridge.TV_REQUIRED_STEREOTYPE, "AStereoType");
        stGen.setTaggedValue("templateScript", "/Template.groovy");
        stGen.setTaggedValue("outputTo", "src-gen");
    }

    @Test
    public void testCodeGeneratorGeneratorStandards() throws Exception {
        this.stGen.setTaggedValue("outputType", "Java");
        MClass gen = oomObjectMother.createClass("AGenerator",
                c -> c.addStereotypes(stGen)
        );

        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains(STANDARD_HEADER)
                .contains(STANDARD_CONSTRUCTOR)
                .contains(RESOLVE_METHOD_SIGNATURE)
                .contains(RESOLVE_METHOD_DEFAULT_PART)
        ;
    }

    @Test
    public void testJavaCodeGeneratorGenerator() throws Exception {
        this.stGen.setTaggedValue("outputType", "Java");
        MClass gen = oomObjectMother.createClass("AGenerator",
                c -> c.addStereotypes(stGen)
        );
        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains("JavaCodeBlock jcb = new JavaCodeBlock(\"src/main/java-gen\", me.getPackage().getFQName(), me.getName());")
        ;
    }

    @Test
    public void testPhpCodeGeneratorGeneratorTest() throws Exception {
        this.stGen.setTaggedValue("outputType", "PHP");
        MClass gen = oomObjectMother.createClass("AGenerator",
                c -> c.addStereotypes(stGen)
        );
        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains("de.spraener.nxtgen.cartridge.rest.php.PhpCodeBlock phpCB = new de.spraener.nxtgen.cartridge.rest.php.PhpCodeBlock(\"src\", \"\", me.getName());\n")
        ;
    }

    @Test
    public void testXmlCodeGeneratorGeneratorTest() throws Exception {
        this.stGen.setTaggedValue("outputType", "Xml");
        MClass gen = oomObjectMother.createClass("AGenerator",
                c -> c.addStereotypes(stGen)
        );
        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains("""
                                GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("xml", element, "/Template.groovy");
                                gcb.setToFileStrategy(new de.spraener.nxtgen.filestrategies.XmlFileStrategy("", element));
                                
                                if( codeBlockModifiers!=null ) {
                        """)
        ;
    }

    @Test
    public void testTypeScriptCodeGeneratorGeneratorTest() throws Exception {
        this.stGen.setTaggedValue("outputType", "TypeScript");
        MClass gen = oomObjectMother.createClass("AGenerator",
                c -> c.addStereotypes(stGen)
        );
        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains("""
                                GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("typscript", element, "/Template.groovy");
                                gcb.setToFileStrategy(new de.spraener.nxtgen.filestrategies.TypeScriptFileStrategy("angular/src/app/model", element.getName()));
                                
                                if( codeBlockModifiers!=null ) {
                        """)
        ;
    }

}

