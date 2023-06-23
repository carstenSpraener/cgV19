package de.spraener.nxtgen.pojo

import de.spraener.nxtgen.oom.cartridge.JavaHelper
import de.spraener.nxtgen.oom.model.MAssociation
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel
import de.spraener.nxtgen.target.CodeBlockSnippet
import de.spraener.nxtgen.target.CodeTarget
import de.spraener.nxtgen.target.SingleLineSnippet
import de.spraener.nxtgen.target.java.JavaSections
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer

class PoJoAssociationCreator : BiConsumer<CodeTarget,MClass> {

    override fun accept(target: CodeTarget, pojo: MClass) {
        for (assoc in pojo.associations) {
            target.inContext(PoJoCodeTargetCreator.ASSOCIATION, assoc, Consumer<CodeTarget> {
                t: CodeTarget -> run {
                    if (isToNAssociation(assoc)) {
                        createToNAssociation(t, pojo, assoc)
                    } else {
                        createToOneAssociation(t, pojo, assoc)
                    }
                }
            })
        }
    }

    protected fun isToNAssociation(assoc: MAssociation): Boolean {
        return assoc.multiplicity.endsWith("*") || assoc.multiplicity.lowercase(Locale.getDefault()).endsWith("n")
    }

    protected fun createToOneAssociation(target: CodeTarget, pojo: MClass?, assoc: MAssociation) {
        val targetType = (assoc.model as OOModel).findClassByName(assoc.type)
        crateImports(target, pojo!!, assoc, targetType)
        target.getSection(JavaSections.ATTRIBUTE_DECLARATIONS)
                .add(SingleLineSnippet("    private ${targetType.name} ${assoc.name} = null;"))
        val accessName = JavaHelper.firstToUpperCase(assoc.name)
        val listType = targetType.name
        target.getSection(JavaSections.METHODS)
                .add(CodeBlockSnippet(
                        """
                        |    public $listType get$accessName() {
                        |        return this.${assoc.name};
                        |    }
                        |    public void set$accessName( $listType value ) {
                        |        this.${assoc.name}= value;
                        |    }
                        |
                        """.trimMargin()
                ))
    }

    private fun createToNAssociation(target: CodeTarget, pojo: MClass, assoc: MAssociation) {
        val targetType = (assoc.model as OOModel).findClassByName(assoc.type)
        crateImports(target, pojo, assoc, targetType)
        target.getSection(JavaSections.ATTRIBUTE_DECLARATIONS)
                .add(SingleLineSnippet("    private List<" + targetType.name + "> " + assoc.name + " = null;"))
        val accessName = JavaHelper.firstToUpperCase(assoc.name)
        val listType = targetType.name
        val code = """
        |
        |    public List<$listType> get$accessName() {
        |        return this.${assoc.name};
        |    }
        |
        |    public void set$accessName( List<$listType> value ) {
        |        this.${assoc.name} = value;
        |    }
        |
        |    public void addTo$accessName( $listType value ) {
        |        this.${assoc.name}.add(value);
        |    }
        |
        |    public void removeFrom$accessName( $listType value ) {
        |        this.${assoc.name}.remove(value);
        |    }
        |
        """.trimMargin();
        target.getSection(JavaSections.METHODS)
                .add(CodeBlockSnippet(code))
    }

    protected fun crateImports(target: CodeTarget, pojo: MClass, assoc: MAssociation?, targetType: MClass) {
        target.getSection(JavaSections.IMPORTS)
                .add(SingleLineSnippet("import java.util.List;"))
        if (pojo.getPackage().fqName != targetType.getPackage().fqName) {
            target.getSection(JavaSections.IMPORTS)
                    .add(SingleLineSnippet("import", assoc, "import " + targetType.fqName + ";"))
        }
    }

}
