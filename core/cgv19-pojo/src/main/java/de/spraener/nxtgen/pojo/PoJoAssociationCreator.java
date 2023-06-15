package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.oom.cartridge.JavaHelper;
import de.spraener.nxtgen.oom.model.MAssociation;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.target.CodeBlockSnippet;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.java.JavaSections;

import java.util.function.BiConsumer;

public class PoJoAssociationCreator implements BiConsumer<CodeTarget, MClass> {

    public void accept(CodeTarget target, MClass pojo) {
        for (MAssociation assoc : pojo.getAssociations()) {
            target.inContext(PoJoCodeTargetCreator.ASSOCIATION, assoc, t -> {
                if (isToNAssociation(assoc)) {
                    createToNAssociation(target, pojo, assoc);
                } else {
                    createToOneAssociation(target, pojo, assoc);
                }
            });
        }
    }

    protected void createToOneAssociation(CodeTarget target, MClass pojo, MAssociation assoc) {
        MClass targetType = ((OOModel) assoc.getModel()).findClassByName(assoc.getType());
        crateImports(target, pojo, assoc, targetType);
        target.getSection(JavaSections.ATTRIBUTE_DECLARATIONS)
                .add(new SingleLineSnippet("    private " + targetType.getName() + " " + assoc.getName() + " = null;"));
        String accessName = JavaHelper.firstToUpperCase(assoc.getName());
        String listType = targetType.getName();
        StringBuilder sb = new StringBuilder();
        sb.append("    public " + listType + " get" + accessName + "() {\n");
        sb.append("        return this." + assoc.getName() + ";\n");
        sb.append("    }");
        sb.append("\n");
        sb.append("    public void set" + accessName + "( " + listType + " value ) {\n");
        sb.append("        this." + assoc.getName() + " = value;\n");
        sb.append("    }\n");
        sb.append("\n");
        target.getSection(JavaSections.METHODS)
                .add(new CodeBlockSnippet(sb.toString()));
    }

    private void createToNAssociation(CodeTarget target, MClass pojo, MAssociation assoc) {
        MClass targetType = ((OOModel) assoc.getModel()).findClassByName(assoc.getType());
        crateImports(target, pojo, assoc, targetType);
        target.getSection(JavaSections.ATTRIBUTE_DECLARATIONS)
                .add(new SingleLineSnippet("    private List<" + targetType.getName() + "> " + assoc.getName() + " = null;"));
        String accessName = JavaHelper.firstToUpperCase(assoc.getName());
        String listType = targetType.getName();

        StringBuilder sb = new StringBuilder();
        sb.append("    public List<" + listType + "> get" + accessName + "() {\n");
        sb.append("        return this." + assoc.getName() + ";\n");
        sb.append("    }");
        sb.append("\n");
        sb.append("    public void set" + accessName + "( List<" + listType + "> value ) {\n");
        sb.append("        this." + assoc.getName() + " = value;\n");
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public void addTo" + accessName + "( " + listType + " value ) {\n");
        sb.append("        this." + assoc.getName() + ".add(value);\n");
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public void removeFrom" + accessName + "( " + listType + " value ) {\n");
        sb.append("        this." + assoc.getName() + ".remove(value);\n");
        sb.append("    }\n");
        sb.append("\n");
        target.getSection(JavaSections.METHODS)
                .add(new CodeBlockSnippet(sb.toString()));
    }

    protected void crateImports(CodeTarget target, MClass pojo, MAssociation assoc, MClass targetType) {
        target.getSection(JavaSections.IMPORTS)
                .add(new SingleLineSnippet("import java.util.List;"));
        if (!pojo.getPackage().getFQName().equals(targetType.getPackage().getFQName())) {
            target.getSection(JavaSections.IMPORTS)
                    .add(new SingleLineSnippet("import", assoc, "import " + targetType.getFQName() + ";"));
        }
    }

    protected boolean isToNAssociation(MAssociation assoc) {
        return assoc.getMultiplicity().endsWith("*") || assoc.getMultiplicity().toLowerCase().endsWith("n");
    }
}
