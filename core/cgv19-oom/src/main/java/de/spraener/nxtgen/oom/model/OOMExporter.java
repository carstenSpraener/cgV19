package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class OOMExporter {

    private static final String INDENT = "  ";

    public String export(OOModel model) {
        StringBuilder sb = new StringBuilder();
        sb.append("import de.spraener.nxtgen.groovy.ModelDSL\n");
        sb.append("\n");
        sb.append("ModelDSL.make {\n");
        for (ModelElement element : model.getChilds()) {
            exportElement(sb, element, 1);
        }
        sb.append("}");
        return sb.toString();
    }

    public void export(OOModel model, Path target) throws IOException {
        Files.writeString(target, export(model), StandardCharsets.UTF_8);
    }

    private void exportElement(StringBuilder sb, ModelElement element, int level) {
        if (element instanceof MPackage) {
            exportPackage(sb, (MPackage) element, level);
        } else if (element instanceof MClass) {
            exportClass(sb, (MClass) element, level);
        }
    }

    private void exportPackage(StringBuilder sb, MPackage pkg, int level) {
        appendLine(sb, level, "mPackage {");
        appendLine(sb, level + 1, "name " + quote(pkg.getName()));
        for (MPackage sub : pkg.getPackages()) {
            exportPackage(sb, sub, level + 1);
        }
        for (MClass clz : pkg.getClasses()) {
            exportClass(sb, clz, level + 1);
        }
        appendLine(sb, level, "}");
    }

    private void exportClass(StringBuilder sb, MClass clz, int level) {
        appendLine(sb, level, "mClass {");
        appendLine(sb, level + 1, "name " + quote(clz.getName()));
        for (Stereotype st : clz.getStereotypes()) {
            exportStereotype(sb, st, level + 1);
        }
        for (MAttribute attr : clz.getAttributes()) {
            exportAttribute(sb, attr, level + 1);
        }
        for (MOperation op : clz.getOperations()) {
            exportOperation(sb, op, level + 1);
        }
        appendLine(sb, level, "}");
    }

    private void exportStereotype(StringBuilder sb, Stereotype st, int level) {
        List<TaggedValue> taggedValues = st.getTaggedValues();
        if (taggedValues == null || taggedValues.isEmpty()) {
            appendLine(sb, level, "stereotype " + quote(st.getName()));
        } else {
            appendLine(sb, level, "stereotype " + quote(st.getName()) + ", {");
            for (TaggedValue tv : taggedValues) {
                appendLine(sb, level + 1, "taggedValue " + quote(tv.getName()) + ", " + quote(tv.getValue()));
            }
            appendLine(sb, level, "}");
        }
    }

    private void exportAttribute(StringBuilder sb, MAttribute attr, int level) {
        appendLine(sb, level, "mAttribute {");
        appendLine(sb, level + 1, "name " + quote(attr.getName()));
        if (attr.getType() != null) {
            appendLine(sb, level + 1, "type " + quote(attr.getType()));
        }
        String visibility = attr.getProperty("visibility");
        if (visibility != null) {
            appendLine(sb, level + 1, "visibility " + quote(visibility));
        }
        appendLine(sb, level, "}");
    }

    private void exportOperation(StringBuilder sb, MOperation op, int level) {
        appendLine(sb, level, "mOperation {");
        appendLine(sb, level + 1, "name " + quote(op.getName()));
        if (op.getType() != null) {
            appendLine(sb, level + 1, "type " + quote(op.getType()));
        }
        for (MParameter param : op.getParameters()) {
            exportParameter(sb, param, level + 1);
        }
        appendLine(sb, level, "}");
    }

    private void exportParameter(StringBuilder sb, MParameter param, int level) {
        appendLine(sb, level, "mParameter {");
        appendLine(sb, level + 1, "name " + quote(param.getName()));
        if (param.getType() != null) {
            appendLine(sb, level + 1, "type " + quote(param.getType()));
        }
        appendLine(sb, level, "}");
    }

    private void appendLine(StringBuilder sb, int level, String content) {
        for (int i = 0; i < level; i++) {
            sb.append(INDENT);
        }
        sb.append(content).append("\n");
    }

    private String quote(String value) {
        if (value == null) {
            return "''";
        }
        return "'''" + value + "'''";
    }
}
