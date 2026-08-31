package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.target.dsl.ForAspectDSL;
import de.spraener.nxtgen.target.java.JavaSections;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * <strong>Responsibility</strong>
 * <p>
 * A CodeTarget is an ordered collection of CodeSections. It can take each
 * CodeSection with a key. Each CodeSection can be retrieved by this key for
 * later modifications. A CodeTarget can only hold one CodeSection per key.
 * Replacing is not possible and will rise a IllegalArgumentException.
 * </p>
 * <p>
 * It also delivers a collection of all added section in the
 * order of insertion.
 * </p>
 */
public class CodeTarget {
    private Map<Object, CodeSection> mySectionMap = new LinkedHashMap<>();
    private CodeTargetRenderer renderer = null;
    private ModelElement defaultModelElement = null;

    /**
     * Add a CodeSection under the given Key to the CodeTarget. A former
     * added CodeSection under that key will be replaced.
     *
     * @param key      A key to reference a CodeSection for later modification
     * @param aSection the CodeSection to be inserted.
     */
    public void addCodeSection(Object key, CodeSection aSection) {
        if (this.mySectionMap.get(key) != null) {
            throw new IllegalArgumentException("CodeSection with key " + key + " already added.");
        }
        aSection.setId(key);
        this.mySectionMap.put(key, aSection);
    }

    /**
     * Add a CodeSection at a specific position in the ordered list.
     */
    public void addCodeSectionAt(Object key, CodeSection aSection, int index) {
        if (this.mySectionMap.get(key) != null) {
            throw new IllegalArgumentException("CodeSection with key " + key + " already added.");
        }
        aSection.setId(key);
        // Convert LinkedHashMap to list, insert at index, rebuild map
        java.util.List<java.util.Map.Entry<Object, CodeSection>> entries = new ArrayList<>(mySectionMap.entrySet());
        entries.add(index, new AbstractMap.SimpleEntry<>(key, aSection));
        LinkedHashMap<Object, CodeSection> newMap = new LinkedHashMap<>();
        for (Map.Entry<Object, CodeSection> entry : entries) {
            newMap.put(entry.getKey(), entry.getValue());
        }
        this.mySectionMap = newMap;
    }

    /**
     * Remove a CodeSection by key. Returns the removed section or null.
     */
    public CodeSection removeCodeSection(Object key) {
        return mySectionMap.remove(key);
    }

    /**
     * A wither for the addCodeSection-method to provide a fluent api.
     *
     * @param key     A key to reference a CodeSection for later modification.
     * @param section The CodeSection to be inserted.
     * @return The CodeTarget itself.
     */
    public CodeTarget withCodeSection(Object key, CodeSection section) {
        addCodeSection(key, section);
        return this;
    }

    /**
     * Retrieve the CodeSection that was added with the specified key or null.
     *
     * @param key The key for the CodeSection requested
     * @return an Optional of the CodeSection. This can be empty if no CodeSection with that key is present.
     */
    public CodeSection getSection(Object key) {
        return mySectionMap.get(key);
    }

    /**
     * Delivers all added CodeSections in the order of Insertion.
     *
     * @return
     */
    public Collection<CodeSection> getSectionsOrdered() {
        return this.mySectionMap.values();
    }

    /**
     * Opens a new CodeTargetContext and calls all consumers on this CodeTarget, so they are working in that
     * given CodeTargetContext.
     *
     * @param aspect    An aspect the consumer working on or null.
     * @param me        A ModelElement the consumers working on or null.
     * @param consumers a list of consumers to do some work on this CodeTarget.
     * @return CodeTarget itself for queuing.
     */
    public CodeTarget forAspect(Object aspect, ModelElement me, Consumer<CodeTarget>... consumers) {
        try (var ctxt = new CodeTargetContext(aspect, me)) {
            if (consumers != null) {
                for (Consumer<CodeTarget> consumer : consumers) {
                    consumer.accept(this);
                }
            }
        }
        return this;
    }

    /**
     * DSL variant: opens a CodeTargetContext and executes a Groovy closure with ForAspectDSL as delegate.
     * Snippets added via the DSL automatically get the aspect and model element from context.
     */
    public CodeTarget forAspect(Object aspect, ModelElement me, Closure closure) {
        try (var ctxt = new CodeTargetContext(aspect, me)) {
            ForAspectDSL dsl = new ForAspectDSL(this);
            closure.setDelegate(dsl);
            closure.setResolveStrategy(Closure.DELEGATE_FIRST);
            closure.call();
        }
        return this;
    }

    /**
     * 2-arg DSL variant: uses defaultModelElement (set via setDefaultModelElement).
     * Enables fluent chaining in Groovy scripts:
     * <pre>
     * ct.setDefaultModelElement(mClass)
     *    .forAspect('logging') { to 'imports', "import java.util.logging.Logger;" }
     * </pre>
     */
    public CodeTarget forAspect(Object aspectName, Closure closure) {
        return forAspect(aspectName, this.defaultModelElement, closure);
    }

    /**
     * Set the default model element for 2-arg forAspect calls. Chainable.
     */
    public CodeTarget setDefaultModelElement(ModelElement me) {
        this.defaultModelElement = me;
        return this;
    }

    /**
     * Get the default model element.
     */
    public ModelElement getDefaultModelElement() {
        return this.defaultModelElement;
    }

    /**
     * Load and execute an external Groovy script with binding:
     * - ct = this CodeTarget
     * - mClass = defaultModelElement
     * - modelElement = defaultModelElement
     * Chainable for fluent usage.
     */
    public CodeTarget evaluate(String scriptPath) {
        String script = loadScript(scriptPath);
        if (script == null) return this;

        Binding b = new Binding();
        b.setVariable("ct", this);
        b.setVariable("mClass", this.defaultModelElement);
        b.setVariable("modelElement", this.defaultModelElement);

        GroovyShell shell = new GroovyShell(b);
        try {
            shell.evaluate(script, scriptPath);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating script " + scriptPath + ": " + e.getMessage(), e);
        }
        return this;
    }

    /**
     * Load a Groovy script from classpath or URL.
     */
    private String loadScript(String scriptPath) {
        try {
            InputStreamReader reader;
            if (scriptPath.startsWith("http")) {
                reader = new InputStreamReader(new URL(scriptPath).openStream());
            } else {
                // Try classpath resource (from package context)
                InputStream is = CodeTarget.class.getResourceAsStream(scriptPath);
                
                // Try classpath root (for test resources)
                if (is == null) {
                    is = Thread.currentThread().getContextClassLoader().getResourceAsStream(scriptPath.substring(1));
                }
                
                // Try as file path
                if (is == null) {
                    java.io.File f = new java.io.File(scriptPath);
                    if (f.exists()) {
                        is = new java.io.FileInputStream(f);
                    } else {
                        return null;
                    }
                }
                reader = new InputStreamReader(is);
            }

            BufferedReader br = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not load script " + scriptPath, e);
        }
    }

    public void setRenderer(CodeTargetRenderer renderer) {
        this.renderer = renderer;
    }

    public CodeTargetRenderer getRenderer() {
        return renderer;
    }

    public CodeTarget append(Object sectionKey, String code) {
        if( CodeTargetContext.getActiveContext()!=null ) {
            Object aspect = CodeTargetContext.getActiveContext().getAspect();
            getSection(sectionKey).add(new SingleLineSnippet(aspect, code));
        } else {
            getSection(sectionKey).add(new SingleLineSnippet(code));
        }
        return this;
    }

    public void beforeLastSnippetOfAspect(Object sectionKey, Object aspectRef, String code) {
        CodeSection section = getSection(sectionKey);
        CodeSnippet snippet = section.getLastSnippetForAspect(aspectRef).get();
        if( CodeTargetContext.getActiveContext()!=null ) {
            Object aspect = CodeTargetContext.getActiveContext().getAspect();
            section.insertBefore(snippet, new SingleLineSnippet(aspect, code));
        } else {
            section.insertBefore(snippet, new SingleLineSnippet(code));
        }
    }
}
