package de.csp.nxtgen;

import de.csp.nxtgen.model.ModelElement;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class GroovyCodeBlockImpl extends CodeBlockImpl {
    private ModelElement me = null;
    private String templateScript;

    public GroovyCodeBlockImpl(String name, ModelElement me, String templateScriptURL) {
        super(name);
        this.me = me;
        try {
            InputStreamReader reader = toInputStreamReader(templateScriptURL);
            BufferedReader templateReader = new BufferedReader(reader);
            this.templateScript = templateScriptURL;
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
        } catch( MalformedURLException mfuExc ) {
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
        Script scr = shell.parse(templateScript);
        Object value = scr.run();
        return value.toString();
    }
}
