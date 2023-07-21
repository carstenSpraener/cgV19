package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.oom.cartridge.JavaHelper;
import de.spraener.nxtgen.oom.model.MAssociation;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.target.CodeBlockSnippet;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.java.JavaSections;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

public class PoJoAssociationCreator implements BiConsumer<CodeTarget, MClass> {

    public void accept(CodeTarget target, MClass pojo) {
        for (MAssociation assoc : pojo.getAssociations()) {
            target.inContext(PoJoCodeTargetCreator.ASSOCIATION, assoc,
                    (t) -> {
                        if (isToNAssociation(assoc)) {
                            createToNAssociation(t, pojo, assoc);
                        } else {
                            createToOneAssociation(t, pojo, assoc);
                        }
                    });
        }
    }

    protected boolean isToNAssociation(MAssociation assoc) {
        return assoc.getMultiplicity().endsWith("*") || assoc.getMultiplicity().toLowerCase(Locale.getDefault()).endsWith("n");
    }

    protected void createToOneAssociation(CodeTarget target, MClass pojo, MAssociation assoc) {
        MClass targetType = ((OOModel)assoc.getModel()).findClassByName(assoc.getType());
        crateImports(target, pojo, assoc, targetType);
        Map<String,String> varMap = new HashMap<>();
        varMap.put("targetType.name", targetType.getName());
        varMap.put("assoc.name", assoc.getName());
        varMap.put("accessName", JavaHelper.firstToUpperCase(assoc.getName()));
        varMap.put("listType", targetType.getName());

        target.getSection(JavaSections.ATTRIBUTE_DECLARATIONS)
                .add(new SingleLineSnippet(StringEvaluator.evaluate("    private ${targetType.name} ${assoc.name} = null;", varMap))
                );
        target.getSection(JavaSections.METHODS)
                .add(new CodeBlockSnippet( StringEvaluator.evaluate(
                        """
                            public ${listType} get${accessName}() {
                                 return this.${assoc.name};
                            }
                            public void set${accessName}( ${listType} value ) {
                                this.${assoc.name}= value;
                            }
                                
                        """, varMap)
                ));
    }

    private void createToNAssociation(CodeTarget target, MClass pojo, MAssociation assoc) {
        MClass targetType = ((OOModel)assoc.getModel()).findClassByName(assoc.getType());
        crateImports(target, pojo, assoc, targetType);
        target.getSection(JavaSections.ATTRIBUTE_DECLARATIONS)
                .add(new SingleLineSnippet("    private List<" + targetType.getName() + "> " + assoc.getName() + " = null;"));
        Map<String, String> varMap = new HashMap<>();
        varMap.put("accessName",JavaHelper.firstToUpperCase(assoc.getName()));
        varMap.put("listType",targetType.getName());
        varMap.put("assoc.name", assoc.getName());
        String code = """
                
                    public List<${listType}> get${accessName}() {
                        return this.${assoc.name};
                    }
                
                    public void set${accessName}( List<${listType}> value ) {
                        this.${assoc.name} = value;
                    }
                
                    public void addTo${accessName}( ${listType} value ) {
                        this.${assoc.name}.add(value);
                    }
                
                    public void removeFrom${accessName}( ${listType} value ) {
                        this.${assoc.name}.remove(value);
                    }
                
                """;
        target.getSection(JavaSections.METHODS)
                .add(new CodeBlockSnippet(StringEvaluator.evaluate(code, varMap)));
    }

    protected void crateImports(CodeTarget target, MClass pojo, MAssociation assoc, MClass targetType) {
        target.getSection(JavaSections.IMPORTS)
                .add(new SingleLineSnippet("import java.util.List;"));
        if (pojo.getPackage().getFQName() != targetType.getPackage().getFQName()) {
            target.getSection(JavaSections.IMPORTS)
                    .add(new SingleLineSnippet("import", assoc, "import " + targetType.getFQName() + ";"));
        }
    }

}
