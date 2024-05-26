import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

import static de.spraener.nxtgen.symfony.php.PhpCodeHelper.readLabel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();
templatePathName = "templates/components/${GeneratorGapTransformation.getOriginalClass(mClass).getName().toLowerCase()}" +
        "/${mClass.getName()}";

if (mClass.getName().endsWith("EditComponent")) {
    return generateEditView(mClass);
} else if (mClass.getName().endsWith("RowComponent")) {
    return generateRowView(mClass);
} else {
    return generateDefaultView(mClass);
}

String generateEditView(MClass mClass) {
    return """<!--${ProtectionStrategieDefaultImpl.GENERATED_LINE}-->
{# ${templatePathName}.html.twig #}
${listFormAttributes(mClass)}    
"""
}

String generateRowView(MClass mClass) {
    return """<!--${ProtectionStrategieDefaultImpl.GENERATED_LINE}-->
<tr>
${listTableAttributes(mClass)}
</tr>
"""
}

String generateDefaultView(MClass mClass) {
    """<!--${ProtectionStrategieDefaultImpl.GENERATED_LINE}-->
{# ${templatePathName}.html.twig #}
<div class="panel">
${listAttributes(mClass)}    
</div>
"""
}

StringBuilder listAttributes(MClass mc) {
    StringBuilder sb = new StringBuilder();
    for (MAttribute a : mc.attributes) {
        String lable = readLabel(a);
        sb.append(
                """
<div class="fieldOutput">
    <lable class="fieldLabel">$lable</lable>
    <div class="fieldValue">{{ ${a.getName()} }}</div>
</div>
""")
    }
    return sb;
}

StringBuilder listFormAttributes(MClass mc) {
    StringBuilder sb = new StringBuilder();
    String entityName = GeneratorGapTransformation.getOriginalClass(mc).getName();
    for (MAttribute a : mc.attributes) {
        String lable = readLabel(a);
        sb.append(
                """
    <div class="form-group">
        <lable class="fieldLabel" for="${entityName}.${a.getName()}">$lable</lable>
        <input class="input" name="${entityName}.${a.getName()}" id="${entityName}.${a.getName()}" value="{{ ${a.getName()} }}"/>
    </div>
""")
    }
    return sb;
}


StringBuilder listTableAttributes(MClass mc) {
    StringBuilder sb = new StringBuilder();
    for (MAttribute a : mc.attributes) {
        String lable = readLabel(a);
        sb.append("    <td>{{ ${a.getName()} }}</td>\n")
    }
    return sb;
}

