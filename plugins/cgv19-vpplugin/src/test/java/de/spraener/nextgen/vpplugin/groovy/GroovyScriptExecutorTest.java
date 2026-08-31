package de.spraener.nextgen.vpplugin.groovy;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class GroovyScriptExecutorTest {

    @Test
    void testSimpleArithmetic() {
        GroovyScriptExecutor executor = new GroovyScriptExecutor();
        String result = executor.execute("1 + 1");
        assertEquals("2", result);
    }

    @Test
    void testScriptReturnsString() {
        GroovyScriptExecutor executor = new GroovyScriptExecutor();
        String result = executor.execute("'Hello World'");
        assertEquals("Hello World", result);
    }

    @Test
    void testScriptWithNoReturnValue() {
        GroovyScriptExecutor executor = new GroovyScriptExecutor();
        // Groovy returns the last expression value; use a statement with no return
        String result = executor.execute("def x = 5; null");
        assertEquals("Script executed successfully.", result);
    }

    @Test
    void testRuntimeError() {
        GroovyScriptExecutor executor = new GroovyScriptExecutor();
        String result = executor.execute("throw new RuntimeException('test error')");
        assertTrue(result.startsWith("Error"));
    }

    @Test
    void testRuntimeErrorWithLineNumber() {
        GroovyScriptExecutor executor = new GroovyScriptExecutor();
        // Zeile 1: def x = 1\nZeile 2: throw ...
        String result = executor.execute("def x = 1\nthrow new RuntimeException('fail')");
        assertTrue(result.contains("line"), "Should contain line number information: " + result);
    }

    @Test
    void testCompilationError() {
        GroovyScriptExecutor executor = new GroovyScriptExecutor();
        // Missing closing quote causes compilation error
        String result = executor.execute("def x = \"unclosed");
        assertTrue(result.startsWith("Error"));
    }

    @Test
    void testGroovyClosures() {
        GroovyScriptExecutor executor = new GroovyScriptExecutor();
        String result = executor.execute("[1,2,3].collect { it * 2 }.join(',')");
        assertEquals("2,4,6", result);
    }

    @Test
    void testMultiLineScript() {
        GroovyScriptExecutor executor = new GroovyScriptExecutor();
        String result = executor.execute(
            "def list = []\n" +
            "list.add('a')\n" +
            "list.add('b')\n" +
            "list.join('-')"
        );
        assertEquals("a-b", result);
    }
}
