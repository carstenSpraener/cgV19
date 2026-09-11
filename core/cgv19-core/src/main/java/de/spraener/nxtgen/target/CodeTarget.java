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
import java.util.function.Supplier;

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
     * <p>
     * String keys may use a Unix-style path syntax separated by '/': the first segment
     * is the top-level section key, each following segment addresses a child scope via
     * {@link CodeSection#getScope(String)} (e.g. "METHODS/IN_OPERATION"). If the first
     * segment does not match a key exactly, it is matched against the section ids — so
     * sections added under enum keys (e.g. {@code JavaSections.METHODS}) are reachable
     * via their name ("METHODS/..."). If any segment is missing, null is returned.
     *
     * @param key The key for the CodeSection requested
     * @return an Optional of the CodeSection. This can be empty if no CodeSection with that key is present.
     */
    public CodeSection getSection(Object key) {
        if (key instanceof String path && path.contains("/")) {
            String[] segments = normalizePath(path).split("/", -1);
            CodeSection section = mySectionMap.get(segments[0]);
            if (section == null) {
                for (CodeSection candidate : mySectionMap.values()) {
                    if (segments[0].equals(candidate.getId())) {
                        section = candidate;
                        break;
                    }
                }
            }
            for (int i = 1; section != null && i < segments.length; i++) {
                section = section.getScope(segments[i]);
            }
            return section;
        }
        return mySectionMap.get(key);
    }

    /**
     * Returns the section at the given path, creating it via the supplier if it does not exist yet.
     * <p>
     * The path is '/'-separated; a leading '/' is tolerated. For a single segment the section
     * is looked up top-level (exact key first, then section id) and created at the end of the
     * table of contents if absent. For multi-segment paths all segments except the last must
     * resolve via {@link #getSection(Object)}; only the last segment is created, via
     * {@link CodeSection#getOrCreateScope(String, java.util.function.Supplier)} on the resolved
     * parent. The supplier is invoked at most once (deterministic caching).
     * </p>
     *
     * @param path the '/'-separated section path, e.g. "OPERATIONS/myOperation"
     * @param supplier creates the section if it does not exist yet; invoked at most once
     * @return the section at the given path (existing or newly created)
     * @throws IllegalArgumentException if a multi-segment parent path does not resolve to a section
     */
    public synchronized CodeSection getOrCreate(String path, Supplier<CodeSection> supplier) {
        String normalized = normalizePath(path);
        int slash = normalized.lastIndexOf('/');
        if (slash < 0) {
            CodeSection existing = mySectionMap.get(normalized);
            if (existing == null) {
                for (CodeSection candidate : mySectionMap.values()) {
                    if (normalized.equals(candidate.getId())) {
                        existing = candidate;
                        break;
                    }
                }
            }
            if (existing != null) {
                return existing;
            }
            CodeSection section = supplier.get();
            addCodeSection(normalized, section);
            return section;
        }
        String parentPath = normalized.substring(0, slash);
        CodeSection parent = getSection(parentPath);
        if (parent == null) {
            throw new IllegalArgumentException("Cannot create section at path '" + path + "': parent path '" + parentPath + "' does not resolve to a section");
        }
        String name = normalized.substring(slash + 1);
        return parent.getOrCreateScope(name, supplier);
    }

    private static String normalizePath(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
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

    /**
     * Add a snippet to the named section. Used by ForAspectDSL.
     */
    public void addSnippetToSection(Object sectionKey, String content) {
        append(sectionKey, content);
    }
}
