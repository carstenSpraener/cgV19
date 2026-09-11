package de.spraener.nxtgen.target.dsl;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.target.*;
import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
     * Add a snippet to the beginning of the named section. The new snippet inherits the
     * aspect and model element from the active {@link CodeTargetContext}.
     */
    public void first(Object sectionKey, String content) {
        CodeSection section = codeTarget.getSection(sectionKey);
        if (section == null) {
            throw new IllegalArgumentException("Cannot add to section " + sectionKey + ": not found");
        }
        Object aspect = CodeTargetContext.getActiveContext().getAspect();
        section.addFirst(new SingleLineSnippet(aspect, content));
    }

    /**
     * Add a new SIMPLE section with the given name at the end of the table of contents.
     * Idempotent: if a section with that name already exists, nothing happens.
     */
    public void addSection(String name) {
        addSection(name, SectionType.SIMPLE);
    }

    /**
     * Add a new section of the given type with the given name at the end of the table
     * of contents. Idempotent: if a section with that name already exists, nothing happens.
     */
    public void addSection(String name, SectionType type) {
        if (codeTarget.getSection(name) != null) {
            return;
        }
        codeTarget.addCodeSection(name, type.create(name));
    }

    /**
     * Add a new section of the given type with configuration at the end of the table of
     * contents. Idempotent: if a section with that name already exists, nothing happens.
     */
    public void addSection(String name, SectionType type, Map<String, Object> config) {
        if (codeTarget.getSection(name) != null) {
            return;
        }
        codeTarget.addCodeSection(name, type.create(name, config));
    }

    /**
     * Add a new SIMPLE section with the given name directly after the existing section
     * identified by {@code afterKey}. Idempotent: if a section with that name already
     * exists, nothing happens.
     */
    public void addSection(String name, String afterKey) {
        if (codeTarget.getSection(name) != null) {
            return;
        }
        int index = indexOf(afterKey) + 1;
        codeTarget.addCodeSectionAt(name, SectionType.SIMPLE.create(name), index);
    }

    /**
     * Returns the position of the section identified by {@code key} in the ordered table
     * of contents, matching either its id or its map key.
     */
    private int indexOf(String key) {
        List<CodeSection> sections = new ArrayList<>(codeTarget.getSectionsOrdered());
        for (int i = 0; i < sections.size(); i++) {
            if (key.equals(sections.get(i).getId())) {
                return i;
            }
        }
        throw new IllegalArgumentException("Cannot add section after " + key + ": not found");
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
            // A null criterion matches any value, so callers can filter by aspect alone,
            // element alone, or both.
            boolean aspectMatches = aspect == null || (snippet.getAspect() != null && snippet.getAspect().equals(aspect));
            boolean elementMatches = element == null || (snippet.getModelElement() != null && snippet.getModelElement().equals(element));
            if (aspectMatches && elementMatches) {
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
