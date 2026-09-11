package de.spraener.nxtgen.target.dsl

import de.spraener.nxtgen.target.*
import groovy.lang.Closure

/**
 * Groovy DSL for building CodeTargets declaratively.
 *
 * Usage in a compiled Groovy class:
 * <pre>
 * import de.spraener.nxtgen.target.dsl.CodeTargetDSL as DSL
 * import de.spraener.nxtgen.target.SectionType
 *
 * def target = DSL.build { CodeTargetDSL._BuildDSL ctx ->
 *     ctx.tableOfContents { CodeTargetDSL._BuildDSL tocCtx ->
 *         tocCtx.section('header')
 *         tocCtx.section('imports', SectionType.UNIQUE_LINES)
 *         tocCtx.section('impl', SectionType.PREFIXED_LIST, [prefix: 'implements ', sep: ', '])
 *     }
 * }
 * </pre>
 */
class CodeTargetDSL {

    /** Build a new CodeTarget (no renderer). */
    static CodeTarget build(Closure closure) {
        return build(null, closure)
    }

    /** Build a new CodeTarget with an optional renderer. */
    static CodeTarget build(CodeTargetRenderer renderer, Closure closure) {
        def target = new CodeTarget()
        if (renderer != null) target.setRenderer(renderer)
        def ctx = new _BuildDSL(target)
        closure.call(ctx)
        return target
    }

    /**
     * Inner DSL class for the build closure.
     */
    static class _BuildDSL {
        private final CodeTarget target

        _BuildDSL(CodeTarget target) { this.target = target }

        /**
         * Define the table of contents (sections).
         */
        void tableOfContents(Closure toc) {
            toc.call(this)
        }

        /** Add a section with default type (SIMPLE). */
        void section(String id) {
            target.addCodeSection(id, SectionType.SIMPLE.create(id))
        }

        /** Add a section with explicit type. */
        void section(String id, SectionType type) {
            target.addCodeSection(id, type.create(id))
        }

        /** Add a section with explicit type and configuration. */
        void section(String id, SectionType type, Map<String, Object> config) {
            target.addCodeSection(id, type.create(id, config))
        }
    }
}
