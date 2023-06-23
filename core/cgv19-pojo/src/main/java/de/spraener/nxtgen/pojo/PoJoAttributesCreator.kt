package de.spraener.nxtgen.pojo

import de.spraener.nxtgen.oom.cartridge.JavaHelper
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.target.CodeBlockSnippet
import de.spraener.nxtgen.target.CodeTarget
import de.spraener.nxtgen.target.SingleLineSnippet
import de.spraener.nxtgen.target.java.JavaSections
import java.util.function.BiConsumer
import java.util.function.Consumer

class PoJoAttributesCreator : BiConsumer<CodeTarget, MClass> {
    override fun accept(target: CodeTarget, pojo: MClass) {
        for (attr in pojo.attributes) {
            target.inContext(PoJoCodeTargetCreator.ATTRIBUTE_ASPECT, attr, Consumer { t: CodeTarget -> addAttribute(t, attr) })
        }
    }

    private fun addAttribute(target: CodeTarget, attr: MAttribute) {
        target.getSection(JavaSections.ATTRIBUTE_DECLARATIONS)
                .add(SingleLineSnippet("    private ${attr.type} ${attr.name};"))
        if (!attr.type.startsWith("java.lang") && attr.type.indexOf('.') >= 0) {
            target.getSection(JavaSections.IMPORTS)
                    .add(SingleLineSnippet("import ${attr.type};"))
        }
        val type = attr.type
        val accessName = JavaHelper.firstToUpperCase(attr.name)
        val field = attr.name
        target.getSection(JavaSections.METHODS)
                .add(CodeBlockSnippet("attribute", attr,"""
                |
                |    public $type get$accessName() {
                |        return $field;
                |    }
                |
                """.trimMargin()
                ))
        target.getSection(JavaSections.METHODS)
                .add(CodeBlockSnippet("attribute", attr,"""
                |
                |    public void set$accessName( $type value) {
                |        this.$field = value;
                |    }
                |
                """.trimMargin()))
    }
}
