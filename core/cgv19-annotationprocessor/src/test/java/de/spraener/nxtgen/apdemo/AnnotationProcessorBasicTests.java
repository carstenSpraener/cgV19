package de.spraener.nxtgen.apdemo;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.Compiler;
import de.spreaner.nxtgen.annoproc.Cgv19AP;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static org.junit.Assert.assertTrue;

public class AnnotationProcessorBasicTests {

    @Test
    public void testAnnotationParsed() throws Exception {
        JavaFileObject input = JavaFileObjects.forSourceLines(
                "de.spraener.nxtgen.apdemo.HelloWorld",
                "package de.spraener.nxtgen.apdemo;",
                "",
                "import de.spraener.nxtgen.cartridge.rest.annotations.PayLoad;",
                "",
                "@PayLoad",
                "public class HelloWorld {" +
                "   private String name;" +
                "}"
        );

        Compilation compilation = Compiler.javac()
                .withProcessors(new Cgv19AP())
                .withOptions("-Acgv19.AP.config=./cgv19-ap.properties")
                .compile(input);

        assertThat(compilation).succeeded();
        var generatedCompilationUnit = compilation.generatedSourceFile("de.spraener.nxtgen.apdemo.HelloWorldPayLoader");
        Assertions.assertTrue(generatedCompilationUnit.isPresent());
        String code = generatedCompilationUnit.get().getCharContent(true).toString();
        assertTrue("No toPayload Method generated.", code.contains("public static String toPayload(HelloWorld data)"));
        assertTrue("No fromPayload Method generated.", code.contains("public static HelloWorld fromPayload(String jsonString)"));
    }
}
