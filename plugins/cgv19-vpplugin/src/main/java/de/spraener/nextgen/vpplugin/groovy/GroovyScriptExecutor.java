package de.spraener.nextgen.vpplugin.groovy;

import de.spraener.nextgen.vpplugin.CgV19Plugin;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroovyScriptExecutor {

    /**
     * Executes a Groovy script with VP API bindings.
     * 
     * @param script the Groovy source code
     * @return result string (success message or error with line number)
     */
    public String execute(String script) {
        Binding binding = new Binding();

        // Bind VP API objects (may fail if VP is not running)
        try {
            binding.setVariable("modelFactory", com.vp.plugin.model.factory.IModelElementFactory.instance());
        } catch (Throwable t) {
            // VP not running, skip binding
        }

        try {
            binding.setVariable("appManager", com.vp.plugin.ApplicationManager.instance());
        } catch (Throwable t) {
            // VP not running, skip binding
        }

        try {
            if (binding.getVariable("appManager") != null) {
                binding.setVariable("project",
                    ((com.vp.plugin.ApplicationManager) binding.getVariable("appManager"))
                        .getProjectManager().getProject());
            }
        } catch (Throwable t) {
            // VP not running, skip binding
        }

        try {
            if (binding.getVariable("project") != null) {
                com.vp.plugin.model.IProject project =
                    (com.vp.plugin.model.IProject) binding.getVariable("project");
                com.vp.plugin.model.IModelElement root = null;
                for (com.vp.plugin.model.IModelElement me : project.toModelElementArray()) {
                    if (me.getModelType().equals(com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_MODEL)) {
                        root = me;
                        break;
                    }
                }
                binding.setVariable("rootModel", root != null ? root : project.toModelElementArray()[0]);
            }
        } catch (Throwable t) {
            // VP not running, skip binding
        }

        GroovyShell shell = new GroovyShell(binding);
        try {
            Object result = shell.evaluate(script);
            return result != null ? result.toString() : "Script executed successfully.";
        } catch (Throwable t) {
            CgV19Plugin.log(t instanceof Exception ? (Exception) t : new RuntimeException(t));
            int line = extractLineNumber(t);
            if (line > 0) {
                return "Error at line " + line + ": " + t.getMessage();
            }
            return "Error: " + t.getMessage();
        }
    }

    private int extractLineNumber(Throwable t) {
        String message = t.getMessage() != null ? t.getMessage() : "";

        Matcher m = Pattern.compile("line\\s+(\\d+)").matcher(message);
        if (m.find()) return Integer.parseInt(m.group(1));

        m = Pattern.compile("\\.(\\d+):").matcher(message);
        if (m.find()) return Integer.parseInt(m.group(1));

        for (StackTraceElement ste : t.getStackTrace()) {
            if (ste.getFileName() != null && ste.getFileName().endsWith(".groovy") && ste.getLineNumber() > 0) {
                return ste.getLineNumber();
            }
        }

        for (StackTraceElement ste : t.getStackTrace()) {
            if (ste.getFileName() != null && ste.getLineNumber() > 0) {
                return ste.getLineNumber();
            }
        }

        return 0;
    }

    /**
     * Extrahiert die Zeilennummer aus einer Exception.
     * Versucht mehrere Quellen in Reihenfolge:
     * 1. Parse Message auf "line \d+" oder "\.\d+:" Pattern
     * 2. Erste .groovy Stacktrace-Line mit getLineNumber() > 0
     * 3. Erste Stacktrace-Line mit Zeilennummer
     * 
     * @param e die Exception
     * @return Zeilennummer > 0 oder 0 wenn nicht gefunden
     */
    private int extractLineNumber(Exception e) {
        String message = e.getMessage() != null ? e.getMessage() : "";
        
        // Parse Message auf "line X" Pattern
        Matcher m = Pattern.compile("line\\s+(\\d+)").matcher(message);
        if (m.find()) return Integer.parseInt(m.group(1));

        // Parse Message auf ".X:" Pattern (z.B. Script1.groovy:5)
        m = Pattern.compile("\\.(\\d+):").matcher(message);
        if (m.find()) return Integer.parseInt(m.group(1));

        // Fallback: erste Stacktrace-Line mit .groovy Datei und Zeilennummer
        for (StackTraceElement ste : e.getStackTrace()) {
            if (ste.getFileName() != null && ste.getFileName().endsWith(".groovy") && ste.getLineNumber() > 0) {
                return ste.getLineNumber();
            }
        }

        // Kein .groovy gefunden, versuche erste Stacktrace-Line mit Zeilennummer
        for (StackTraceElement ste : e.getStackTrace()) {
            if (ste.getFileName() != null && ste.getLineNumber() > 0) {
                return ste.getLineNumber();
            }
        }

        return 0; // Keine Zeile gefunden
    }
}
