package ts


import de.spraener.nxtgen.ProtectionStrategie

def cName = modelElement.name;

def attrList() {
    StringBuilder sb = new StringBuilder();
    modelElement.attributes.forEach({
        sb.append("    ${it.name} : ${it.type};\n")
    });

    return sb.toString();
}

return """// ${ProtectionStrategie.GENERATED_LINE}

export class ${cName} {
${attrList()}
}

"""
