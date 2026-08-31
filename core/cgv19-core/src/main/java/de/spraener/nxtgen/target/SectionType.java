package de.spraener.nxtgen.target;

/**
 * Enum defining the types of CodeSections that can be created.
 */
public enum SectionType {
    SIMPLE,
    UNIQUE_LINES,
    PREFIXED_LIST;

    /**
     * Creates a CodeSection of this type with the given id and optional configuration.
     *
     * @param id      The section identifier
     * @param config  Optional map with keys: "prefix", "sep" (used for PREFIXED_LIST)
     * @return A configured CodeSection instance
     */
    public CodeSection create(String id, java.util.Map<String, Object> config) {
        CodeSection section = switch (this) {
            case SIMPLE -> new SimpleCodeSection();
            case UNIQUE_LINES -> new UniqueLineSection();
            case PREFIXED_LIST -> {
                NonEmptyPrefixedListSection s = new NonEmptyPrefixedListSection();
                if (config != null) {
                    if (config.get("prefix") != null) s.setPrefix((String) config.get("prefix"));
                    if (config.get("sep") != null) s.setSeparator((String) config.get("sep"));
                }
                yield s;
            }
        };
        section.setId(id);
        return section;
    }

    /**
     * Creates a CodeSection with no config (uses defaults).
     */
    public CodeSection create(String id) {
        return create(id, null);
    }
}
