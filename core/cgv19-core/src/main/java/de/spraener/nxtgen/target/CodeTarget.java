package de.spraener.nxtgen.target;

import java.util.*;

/**
 * <h4>Responsibility</h4>
 * A CodeTarget is an ordered collection of CodeSections. It can take each
 * CodeSection with a key. Each CodeSection can be retrieved by this key for
 * later modifications. A CodeTarget can only hold one CodeSection per key.
 * Replacing is not possible and will rise a IllegalArgumentException.
 * <p/>
 * It also delivers a collection of all added section in the
 * order of insertion.
 */
public class CodeTarget {
    private Map<Object, CodeSection> mySectionMap = new TreeMap<>();

    /**
     * Add a CodeSection under the given Key to the CodeTarget. A former
     * added CodeSection under that key will be replaced.
     *
     * @param key A key to reference a CodeSection for later modification
     * @param aSection the CodeSection to be inserted.
     */
    public void addCodeSection(Object key, CodeSection aSection) {
        if( this.mySectionMap.get(key) != null ) {
            throw new IllegalArgumentException("CodeSection with key "+key+" already added.");
        }
        this.mySectionMap.put(key, aSection);
    }

    /**
     * A wither for the addCodeSection-method to provide a fluent api.
     * @param key A key to reference a CodeSection for later modification.
     * @param section The CodeSection to be inserted.
     * @return The CodeTarget itself.
     */
    public CodeTarget withCodeSection(Object key, CodeSection section) {
        addCodeSection(key, section);
        return this;
    }

    /**
     * Retrieve the CodeSection that was added with the specified key or null.
     * @param key The key for the CodeSection requested
     * @return an Optional of the CodeSection. This can be empty if no CodeSection with that key is present.
     */
    public Optional<CodeSection> getSection(Object key) {
        return Optional.ofNullable(mySectionMap.get(key));
    }

    /**
     * Delivers all added CodeSections in the order of Insertion.
     * @return
     */
    public Collection<CodeSection> getSectionsOrdered() {
        return this.mySectionMap.values();
    }
}
