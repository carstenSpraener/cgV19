import de.spraener.nxtgen.NextGen
import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.model.ModelElement
import de.spraener.nxtgen.oom.model.MAssociation
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass

def cName = modelElement.name;
MClass mClass = (MClass)getProperty("modelElement");

String baseClass(mClass) {
    String baseClassList = "";
    for(MAssociation assoc : mClass.getAssociations() ) {
        if( "base_Class".equals(assoc.name) ) {
            baseClass = assoc.getProperty("type");
            if( baseClass.startsWith("UML Standard Profile.UML2 Metamodel.")) {
                baseClass = baseClass.substring("UML Standard Profile.UML2 Metamodel.".length())
            }
            baseClassList += baseClassList+"\n* "+baseClass;
        }
    }
    if( baseClassList.equals("") ) {
        baseClassList = "\n* Element"
    }
    return """

## BaseClass(es)
This stereotype is applicable to the following UML-ELements:
${baseClassList}
"""
}

String documentation(ModelElement attr) {
    String doc = attr.getProperty('documentation');
    if( null == doc || "".equals(doc) ) {
        doc = "There is no documentation yet."
    }
    if( attr.getProperty("defaultValue") != null ) {
        doc +="\n\n_Default Value:_ '${attr.getProperty('defaultValue')}'"
    }
    if( attr.getProperty("enumValues") != null ) {
        doc += "\n\n__Allowed values:__\n"
        String[] enumValues = attr.getProperty('enumValues').split("\n");
        for( String v : enumValues ) {
            if( v.trim().equals("")) {
                continue;
            }
            doc += "* '"+v.trim()+"'\n"
        }
    }
    return doc;
}

String documentTaggedValue( MAttribute attr ) {
    String documentation = documentation(attr);
    String type = attr.type;
    if( type==null ) {
        type = "Any"
    }
    if( type.startsWith("UML Standard Profile.MagicDraw Profile.datatypes.")) {
        type = type.substring("UML Standard Profile.MagicDraw Profile.datatypes.".length());
    }
    return """|__${attr.name}__| ${type} | ${documentation.replaceAll("\\n","<br/>")} |
"""
}

String listTagedValues( MClass mClass ) {
    String result  = "";

    for(MAttribute attr: mClass.getAttributes() ) {
        result += documentTaggedValue(attr);
    }
    if( "".equals(result)) {
        return "This Stereotype has no associated tagged vales.";
    }
    return """| Name | Type | Documentation |
|------|-------|----------------------------------------|
${result}"""
}

"""
[comment]: <> (${ProtectionStrategieDefaultImpl.GENERATED_LINE})

# Stereotype \"${cName}\"

${documentation(mClass)}
${baseClass(mClass)}

## Associated Tagged Values
${listTagedValues(mClass)}
"""
