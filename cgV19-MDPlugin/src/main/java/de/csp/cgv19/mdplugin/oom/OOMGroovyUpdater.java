package de.csp.cgv19.mdplugin.oom;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import de.spraener.nxtgen.model.ModelElement;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

public class OOMGroovyUpdater {
    private OOMImport oomImport;
    private String groovyScript;
    private File groovyScriptFile;
    private long lastScriptUpdate;

    public OOMGroovyUpdater(OOMImport oomImport, String groovyScript) {
        this.oomImport = oomImport;
        this.groovyScript = groovyScript;
    }

    public void update(Element e, ModelElement modelElement) {
        try {
            this.groovyScriptFile = new File("plugins/cgV19/" + groovyScript);
            Application.getInstance().getGUILog().writeLogText("Looking for Grovvy Script in '"+groovyScriptFile.getAbsolutePath()+"'", true);
            Binding binding = new Binding();
            binding.setProperty("magicDrawElement", e);
            binding.setVariable("magicDrawElement", e);

            binding.setProperty("modelElement", modelElement);
            binding.setVariable("modelElement", modelElement);

            binding.setProperty("magicDrawProject", Application.getInstance().getProject());
            binding.setVariable("magicDrawProject", Application.getInstance().getProject());
            GroovyShell shell = new GroovyShell(binding);
            shell.evaluate(this.groovyScriptFile);
        } catch( Exception xc ) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            xc.printStackTrace(ps);
            ps.flush();
            Application.getInstance().getGUILog().writeLogText("Fehler: "+new String(baos.toByteArray()), true);
        }
    }
}
