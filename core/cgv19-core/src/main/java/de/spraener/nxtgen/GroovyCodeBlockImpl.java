package de.spraener.nxtgen;

import de.spraener.nxtgen.model.ModelElement;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This CodeBlock implements a bridge to groovy generator templates. It takes a name,
 * a ModelElement and a URL to the graoovy script that generates the output.
 *
 */
public class GroovyCodeBlockImpl extends CodeBlockImpl {
    private ModelElement me = null;
    private String templateScript;
    private String templateScriptURL;

    public GroovyCodeBlockImpl(String name, ModelElement me, String templateScriptURL) {
        super(name);
        this.me = me;
        this.templateScriptURL = templateScriptURL;
        try {
            InputStreamReader reader = toInputStreamReader(this.templateScriptURL);
            BufferedReader templateReader = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = templateReader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            this.templateScript = sb.toString();
        } catch (IOException ioXc) {
            throw new RuntimeException("Could not read from Scripts URL '" + templateScriptURL + "'. Check Claspath and URL form.", ioXc);
        }
    }

    private InputStreamReader toInputStreamReader(String templateScriptURL) throws IOException {
        try {
            return new InputStreamReader(new URL(templateScriptURL).openStream());
        } catch (MalformedURLException mfuExc) {
            return new InputStreamReader(GroovyCodeBlockImpl.class.getResourceAsStream(templateScriptURL));
        }
    }


    @Override
    public String toCode() {
        Binding b = new Binding();
        b.setVariable("modelElement", this.me);
        b.setVariable("codeBlock", this);
        b.setProperty("modelElement", this.me);
        b.setProperty("codeBlock", this);
        GroovyShell shell = new GroovyShell(b);
        Script scr = shell.parse(templateScript,templateScriptURL);
        try {
            Object value = scr.run();
            return value.toString();
        } catch (Exception e) {
            NextGen.LOGGER.severe(() -> "Error while executing script " + this.templateScriptURL + ": "+e.getMessage());
            throw new NxtGenRuntimeException(e);
        }
    }
}
