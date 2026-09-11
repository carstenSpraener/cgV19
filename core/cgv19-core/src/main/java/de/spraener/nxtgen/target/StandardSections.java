package de.spraener.nxtgen.target;

import java.util.function.Supplier;

/**
 * Reusable {@link Supplier} templates for common code blocks in the hierarchical,
 * supplier-based section model. The standard sub-scopes are pre-created so that
 * cartridges can address them via {@link CodeSection#getScope(String)} without null checks.
 */
public final class StandardSections {

    private StandardSections() {
    }

    /** A plain section without child scopes. */
    public static Supplier<CodeSection> plain() {
        return SimpleCodeSection::new;
    }

    /** A section with BEFORE_OPERATION / IN_OPERATION / AFTER_OPERATION sub-scopes (e.g. a method body). */
    public static Supplier<CodeSection> operation() {
        return () -> {
            CodeSection section = new SimpleCodeSection();
            section.getOrCreateScope(SectionName.BEFORE_OPERATION.name(), StandardSections.plain());
            section.getOrCreateScope(SectionName.IN_OPERATION.name(), StandardSections.plain());
            section.getOrCreateScope(SectionName.AFTER_OPERATION.name(), StandardSections.plain());
            return section;
        };
    }

    /** A section with PREAMBLE / EPILOGUE sub-scopes (e.g. a class body). */
    public static Supplier<CodeSection> preamble() {
        return () -> {
            CodeSection section = new SimpleCodeSection();
            section.getOrCreateScope(SectionName.PREAMBLE.name(), StandardSections.plain());
            section.getOrCreateScope(SectionName.EPILOGUE.name(), StandardSections.plain());
            return section;
        };
    }
}
