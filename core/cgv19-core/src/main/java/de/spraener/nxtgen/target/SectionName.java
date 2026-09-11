package de.spraener.nxtgen.target;

/**
 * Generic standard section names for the hierarchical, supplier-based section model.
 * Use {@link #name()} as the scope name for {@link CodeSection#getOrCreateScope(String, java.util.function.Supplier)}.
 */
public enum SectionName {
    PREAMBLE,
    BEFORE_OPERATION,
    IN_OPERATION,
    AFTER_OPERATION,
    EPILOGUE;
}
