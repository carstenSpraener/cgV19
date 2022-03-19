package entity


import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.cartridge.rest.RESTJavaHelper
import de.spraener.nxtgen.model.ModelElement
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.MOperation
import de.spraener.nxtgen.oom.model.MParameter

def pkgName = ((MClass)modelElement).getPackage().getFQName()
def cName = modelElement.name;

def toLogicName(me) {
    return me.name.replace("Controller", "Logic");
}

def extendsExpr(MClass me) {
    if (me.getProperty("extends") == null) {
        return "";
    }
    return " extends " + me.getProperty("extends");
}

def constructorCode(MClass me) {
    if (me.getProperty("constructorCode") == null) {
        return "";
    }
    return me.getProperty("constructorCode");

}

def constructor(MClass c) {
    def args = c.getProperty("constructorArgs");
    def superCallArgs = c.getProperty("superCallArgs");
    if (args != null) {
        return """    public ${c.name}(${args}) {
        super(${superCallArgs});${constructorCode(c)}
    }""";
    } else {
        return """    public ${c.name}(${constructorCode(c)}) {
    }""";
    }
}

def importList(MClass c) {
    def imports = c.getProperty("importList");
    if (imports != null) {
        return "\n" + imports;
    } else {
        return "";
    }
}

def elementAnnotations(ModelElement c) {
    def annotations = c.getProperty("annotations");
    if (annotations != null) {
        return annotations;
    } else {
        return "";
    }
}

def toMethodStub(MOperation op ) {
    def type = op.getType();
    def name = op.getName();
    def paramList = "";
    def annotations=elementAnnotations(op);

    for(MParameter p : op.getParameters() ) {
        paramList += "${p.getType()} ${p.getName()}, ";
    }
    if( paramList.length()>2 ) {
        paramList = paramList.substring(0, paramList.length()-2);
    }
    def methodBody = op.getProperty("methodBody");
    if( methodBody==null ) {
        def returnStatement = op.getProperty("returnStatement");
        if( null == returnStatement ) {
            returnStatement = "return${'void'.equals(type) ? '' : ' null'};";
        }
        methodBody = "// TODO: Implement this method\n        ${returnStatement}";
    }
    return """
    ${annotations}public ${type} ${name}(${paramList}) {
        ${methodBody}
    } 
""";
}
def methodStubs(MClass c) {
    StringBuilder sb = new StringBuilder();
    for (MOperation op : c.getOperations()) {
         sb.append(toMethodStub(op));
    }
    return sb.toString();
}

def attributeDeclaration(MClass modelElement) {
    StringBuilder sb = new StringBuilder();
    modelElement.attributes.each({
        sb.append("    private ${it.type} ${it.name};\n");
    })
    return sb.toString();
}

return """// ${ProtectionStrategie.GENERATED_LINE}
package ${pkgName};
${importList(modelElement)}
${elementAnnotations(modelElement)}public class ${cName}${extendsExpr(modelElement)} {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(${cName}.class.getName());
${attributeDeclaration(modelElement)}
${constructor(modelElement)}
${methodStubs(modelElement)}
}
"""
