package de.spraener.nxtgen;

import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.CodeTargetRenderer;
import de.spraener.nxtgen.target.SimpleCodeSection;
import de.spraener.nxtgen.target.SingleLineSnippet;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GroovyCodeBlockImplTest {

    @Test
    void stringReturnReturnsString() {
        String script = "return 'hello'";
        GroovyShell shell = new GroovyShell();
        Object value = shell.evaluate(script);
        assertEquals("hello", value.toString());
    }

    @Test
    void codeTargetWithRendererUsesRenderer() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        section.add(new SingleLineSnippet("test", "some code"));
        target.addCodeSection("s1", section);
        target.setRenderer(t -> "rendered-output");

        Binding binding = new Binding();
        binding.setVariable("target", target);
        GroovyShell shell = new GroovyShell(binding);
        Object value = shell.evaluate("return target");

        assertEquals(CodeTarget.class, value.getClass());
        CodeTargetRenderer renderer = ((CodeTarget) value).getRenderer();
        assertNotNull(renderer);
        assertEquals("rendered-output", renderer.render((CodeTarget) value));
    }

    @Test
    void codeTargetWithoutRendererUsesFallback() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        section.add(new SingleLineSnippet("test", "some code"));
        target.addCodeSection("s1", section);

        assertNull(target.getRenderer());

        Binding binding = new Binding();
        binding.setVariable("target", target);
        GroovyShell shell = new GroovyShell(binding);
        Object value = shell.evaluate("return target");

        assertEquals(CodeTarget.class, value.getClass());
        assertNull(((CodeTarget) value).getRenderer());
    }
}
