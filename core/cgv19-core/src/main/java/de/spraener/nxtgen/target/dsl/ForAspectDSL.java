package de.spraener.nxtgen.target.dsl;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.target.*;
import groovy.lang.Closure;

import java.util.Collection;
import java.util.Map;

/**
 * DSL helper for CodeTarget. Provides fluent methods for adding code sections via Groovy closures.
 */
public class ForAspectDSL {

    private final CodeTarget codeTarget;

    public ForAspectDSL(CodeTarget codeTarget) {
        this.codeTarget = codeTarget;
    }

    /**
     * Add a snippet to the named section.
     */
    public void to(Object sectionKey, String content) {
        codeTarget.addSnippetToSection(sectionKey, content);
    }

    /**
     * Add a snippet to the named section using a closure.
     */
    public void to(Object sectionKey, Closure closure) {
        String content = String.valueOf(closure.call());
        codeTarget.addSnippetToSection(sectionKey, content);
    }

    /**
     * Insert a snippet before the first snippet in the section that matches the given criteria.
     * @param sectionKey the section identifier
     * @param criteria map with optional 'aspect' and/or 'element' keys
     * @param content the snippet content to insert
     */
    public void beforeSnippet(Object sectionKey, Map<String, Object> criteria, String content) {
        CodeSection section = codeTarget.getSection(sectionKey);
        CodeSnippet target = findMatchingSnippet(section, criteria);
        if (target != null) {
            String aspect = CodeTargetContext.getActiveContext() != null ?
                    String.valueOf(CodeTargetContext.getActiveContext().getAspect()) : "default";
            section.insertBefore(target, new SingleLineSnippet(aspect, content));
        }
    }

    /**
     * Insert a snippet after the first snippet in the section that matches the given criteria.
     * @param sectionKey the section identifier
     * @param criteria map with optional 'aspect' and/or 'element' keys
     * @param content the snippet content to insert
     */
    public void afterSnippet(Object sectionKey, Map<String, Object> criteria, String content) {
        CodeSection section = codeTarget.getSection(sectionKey);
        CodeSnippet target = findMatchingSnippet(section, criteria);
        if (target != null) {
            String aspect = CodeTargetContext.getActiveContext() != null ?
                    String.valueOf(CodeTargetContext.getActiveContext().getAspect()) : "default";
            section.insertAfter(target, new SingleLineSnippet(aspect, content));
        }
    }

    private CodeSnippet findMatchingSnippet(CodeSection section, Map<String, Object> criteria) {
        String aspect = (String) criteria.get("aspect");
        ModelElement element = (ModelElement) criteria.get("element");

        for (CodeSnippet snippet : section.getSnippetsOrdered()) {
            if (snippet.matches(aspect, element)) {
                return snippet;
            }
        }
        return null;
    }

    /**
     * Returns the current ModelElement (mClass) from the active CodeTargetContext.
     */
    public ModelElement getMClass() {
        if (CodeTargetContext.getActiveContext() != null) {
            return CodeTargetContext.getActiveContext().getModelElement();
        }
        return null;
    }
}
