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
            return new SimpleCodeSection();
        }

        @Override
        public CodeSection create(String id, Map<String, Object> config) {
            return new SimpleCodeSection();
        }
    },

    /** Unique lines section – duplicate lines are removed. */
    UNIQUE_LINES {
        @Override
        public CodeSection create(String id) {
            return new UniqueLineSection();
        }

        @Override
        public CodeSection create(String id, Map<String, Object> config) {
            return new UniqueLineSection();
        }
    },

    /** Prefixed list section – non-empty snippets are joined with a separator and optional prefix. */
    PREFIXED_LIST {
        @Override
        public CodeSection create(String id) {
            return new NonEmptyPrefixedListSection();
        }

        @Override
        public CodeSection create(String id, Map<String, Object> config) {
            NonEmptyPrefixedListSection section = new NonEmptyPrefixedListSection();
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
