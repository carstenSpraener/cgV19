package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;

import java.util.Collection;
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
     * @param me        A ModelElement the consumers wokring on or null.
     * @param consumers a list of consumers to do some work on this CodeTarget.
     * @return CodeTarget itself for queuing.
     */
    public CodeTarget inContext(Object aspect, ModelElement me, Consumer<CodeTarget>... consumers) {
        try (var ctxt = new CodeTargetContext(aspect, me)) {
            if (consumers != null) {
                for (Consumer<CodeTarget> consumer : consumers) {
                    consumer.accept(this);
                }
            }
        }
        return this;
    }
}
