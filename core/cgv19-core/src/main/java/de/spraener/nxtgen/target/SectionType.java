package de.spraener.nxtgen.target;

import java.util.Map;

/**
 * Enum defining the different types of CodeSections that can be created.
 */
public enum SectionType {

    /** Simple section – snippets are rendered in order, each on its own line. */
    SIMPLE {
        @Override
        public CodeSection create(String id) {
            SimpleCodeSection section = new SimpleCodeSection();
            section.setId(id);
            return section;
        }

        @Override
        public CodeSection create(String id, Map<String, Object> config) {
            SimpleCodeSection section = new SimpleCodeSection();
            section.setId(id);
            return section;
        }
    },

    /** Unique lines section – duplicate lines are removed. */
    UNIQUE_LINES {
        @Override
        public CodeSection create(String id) {
            UniqueLineSection section = new UniqueLineSection();
            section.setId(id);
            return section;
        }

        @Override
        public CodeSection create(String id, Map<String, Object> config) {
            UniqueLineSection section = new UniqueLineSection();
            section.setId(id);
            return section;
        }
    },

    /** Prefixed list section – non-empty snippets are joined with a separator and optional prefix. */
    PREFIXED_LIST {
        @Override
        public CodeSection create(String id) {
            NonEmptyPrefixedListSection section = new NonEmptyPrefixedListSection();
            section.setId(id);
            return section;
        }

        @Override
        public CodeSection create(String id, Map<String, Object> config) {
            NonEmptyPrefixedListSection section = new NonEmptyPrefixedListSection();
            section.setId(id);
            if (config != null) {
                String prefix = (String) config.get("prefix");
                String sep = (String) config.get("sep");
                if (prefix != null) section.setPrefix(prefix);
                if (sep != null) section.setSeparator(sep);
            }
            return section;
        }
    };

    /** Create a CodeSection with the given id and default configuration. */
    public abstract CodeSection create(String id);

    /** Create a CodeSection with the given id and configuration map. */
    public abstract CodeSection create(String id, Map<String, Object> config);
}
