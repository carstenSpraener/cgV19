package de.spraener.nxtgen.target.dsl;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.target.CodeSection;
import de.spraener.nxtgen.target.SectionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.spraener.nxtgen.target.CodeSnippet;
import de.spraener.nxtgen.target.CodeSnippetRef;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.CodeTargetContext;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.SingleLineSnippet;

/**
 * DSL for the forAspect block. Provides to() and first() operations
 * that automatically use the aspect name and model element from the context.
 */
public class ForAspectDSL {
    private final CodeTarget target;

    public ForAspectDSL(CodeTarget target) {
        this.target = target;
    }

    /**
     * Add a snippet at the end of the given section.
     * The snippet automatically gets the aspect and element from forAspect context.
     * Accepts Object as sectionId to support both String and enum (e.g., JavaSections) values.
     */
    public void to(Object sectionId, String text) {
        CodeTargetContext ctx = CodeTargetContext.getActiveContext();
        Object aspect = ctx != null ? ctx.getAspect() : null;
        ModelElement element = resolveElement(ctx);
        CodeSection section = target.getSection(sectionId);
        if (section != null) {
            section.add(new SingleLineSnippet(aspect, element, text));
        }
    }

    /**
     * Add a snippet at the beginning of the given section.
     * The snippet automatically gets the aspect and element from forAspect context.
     * Accepts Object as sectionId to support both String and enum (e.g., JavaSections) values.
     */
    public void first(Object sectionId, String text) {
        CodeTargetContext ctx = CodeTargetContext.getActiveContext();
        Object aspect = ctx != null ? ctx.getAspect() : null;
        ModelElement element = resolveElement(ctx);
        CodeSection section = target.getSection(sectionId);
        if (section != null) {
            section.addFirst(new SingleLineSnippet(aspect, element, text));
        }
    }

    /**
     * Insert a snippet before the first existing snippet matching the given criteria.
     * @param sectionId the target section (String or enum like JavaSections)
     * @param find map with keys: "aspect" (Object), "element" (ModelElement)
     * @param text the snippet content
     */
    public void beforeSnippet(Object sectionId, Map<String, Object> find, String text) {
        CodeSection section = target.getSection(sectionId);
        if (section == null) return;

        CodeSnippetRef targetRef = findSnippetRef(section, find);
        if (targetRef == null) return;

        CodeTargetContext ctx = CodeTargetContext.getActiveContext();
        Object aspect = ctx != null ? ctx.getAspect() : null;
        ModelElement element = resolveElement(ctx);
        section.insertBefore(targetRef.get(), new SingleLineSnippet(aspect, element, text));
    }

    /**
     * Insert a snippet after the first existing snippet matching the given criteria.
     * @param sectionId the target section (String or enum like JavaSections)
     * @param find map with keys: "aspect" (Object), "element" (ModelElement)
     * @param text the snippet content
     */
    public void afterSnippet(Object sectionId, Map<String, Object> find, String text) {
        CodeSection section = target.getSection(sectionId);
        if (section == null) return;

        CodeSnippetRef targetRef = findSnippetRef(section, find);
        if (targetRef == null) return;

        CodeTargetContext ctx = CodeTargetContext.getActiveContext();
        Object aspect = ctx != null ? ctx.getAspect() : null;
        ModelElement element = resolveElement(ctx);
        section.insertAfter(targetRef.get(), new SingleLineSnippet(aspect, element, text));
    }

    /**
     * Add a new section to the CodeTarget (appended at end).
     * Idempotent: if a section with this id already exists, nothing happens.
     */
    public void addSection(String sectionId) {
        addSection(sectionId, SectionType.SIMPLE, (Map<String, Object>) null);
    }

    /**
     * Add a new section with explicit type (appended at end).
     */
    public void addSection(String sectionId, SectionType type) {
        addSection(sectionId, type, (Map<String, Object>) null);
    }

    /**
     * Add a new section with explicit type and configuration.
     * Config may contain "after" key to specify positioning after an existing section.
     */
    public void addSection(String sectionId, SectionType type, Map<String, Object> config) {
        if (target.getSection(sectionId) != null) return; // Idempotent

        Map<String, Object> sectionConfig = config != null ? new HashMap<>(config) : new HashMap<>();
        String after = (String) sectionConfig.remove("after");

        CodeSection section = type.create(sectionId, sectionConfig);

        if (after != null) {
            List<CodeSection> sections = new ArrayList<>(target.getSectionsOrdered());
            int afterIdx = -1;
            for (int i = 0; i < sections.size(); i++) {
                if (after.equals(sections.get(i).getId())) {
                    afterIdx = i;
                    break;
                }
            }
            if (afterIdx >= 0) {
                target.addCodeSectionAt(sectionId, section, afterIdx + 1);
            } else {
                target.addCodeSection(sectionId, section);
            }
        } else {
            target.addCodeSection(sectionId, section);
        }
    }

    /**
     * Add a new section after an existing section (SIMPLE type).
     */
    public void addSection(String sectionId, String after) {
        Map<String, Object> config = new HashMap<>();
        config.put("after", after);
        addSection(sectionId, SectionType.SIMPLE, config);
    }

    /**
     * Add a new section with type after an existing section.
     */
    public void addSection(String sectionId, SectionType type, String after) {
        Map<String, Object> config = new HashMap<>();
        config.put("after", after);
        addSection(sectionId, type, config);
    }

    /**
     * Find a snippet reference in the section based on aspect and/or element criteria.
     */
    private static CodeSnippetRef findSnippetRef(CodeSection section, Map<String, Object> find) {
        if (find == null || find.isEmpty()) return null;

        Object aspect = find.get("aspect");
        Object elementObj = find.get("element");
        ModelElement element = (elementObj instanceof ModelElement) ? (ModelElement) elementObj : null;

        if (aspect != null && element != null) {
            List<CodeSnippetRef> refs = section.getSnippetsForAspectAndModelElement(aspect, element);
            return refs.isEmpty() ? null : refs.get(0);
        } else if (aspect != null) {
            List<CodeSnippetRef> refs = section.getSnippetsForAspect(aspect);
            return refs.isEmpty() ? null : refs.get(0);
        } else if (element != null) {
            // Find by element across all aspects
            for (CodeSnippet snippet : section.getSnippetsOrdered()) {
                if (snippet.getModelElement() != null && snippet.getModelElement().equals(element)) {
                    return new CodeSnippetRef(section, snippet);
                }
            }
        }
        return null;
    }

    /**
     * Returns the model element from the current CodeTargetContext.
     * This allows Groovy scripts to access mClass directly in closures:
     * <pre>
     * forAspect('logging') { to 'imports', "import java.util.logging.Logger;" }
     * // or access mClass property: ${mClass.name}
     * </pre>
     */
    public ModelElement getMClass() {
        CodeTargetContext ctx = CodeTargetContext.getActiveContext();
        return resolveElement(ctx);
    }

    private static ModelElement resolveElement(CodeTargetContext ctx) {
        if (ctx == null) return null;
        Object me = ctx.getModelElement();
        if (me instanceof ModelElement) return (ModelElement) me;
        return null;
    }
}
