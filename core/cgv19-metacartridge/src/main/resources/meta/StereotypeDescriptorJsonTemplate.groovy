import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cartridge.meta.MetaStereotypes
import de.spraener.nxtgen.cartridge.meta.StereotypeEnumToDescriptorTransformation
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();

String cartridgeName(OOModel m) {
    return m.getClassesByStereotype(MetaStereotypes.CGV19CARTRIDGE.name).get(0).getName();
}

String generateTaggedValuesJSON(List<MAttribute> attrs ) {
    if( attrs == null || attrs.isEmpty() ) {
        return "\n"
    }
    StringBuilder sb = new StringBuilder();
    boolean firstTaggedValue = true;
    for( MAttribute attr : attrs ) {
        if( !firstTaggedValue ) {
            sb.append(",\n");
        }
        firstTaggedValue = false;
        sb.append( """        {
          "name": "${attr.getName()}",
          "type": "${attr.getType()}",
"""
        )
        String enumValues = attr.getProperty("enumValues")
        if( enumValues!= null ) {
            sb.append("          \"allowedValues\": [\n")
            boolean firstValue = true;
            for( String value: enumValues.split("\n")) {
                value = value.trim()
                if( !firstValue ) {
                    sb.append(",\n");
                }
                sb.append("            \"${value}\"")
                firstValue = false;

            }
            sb.append("        ],\n")
        }
        sb.append("          \"typeID\": \"${attr.getType()}\"\n        }");
    }
    sb.append("\n");
    return sb.toString();
}

String generateSTypeJSON(MClass sType ) {
    return """  {
    "name": "${sType.getName()}",
    "taggedValues": [${generateTaggedValuesJSON(sType.getAttributes())}    ]
  }
"""
}

String listStereotypes(MClass mc) {
    StringBuilder sb = new StringBuilder();
    boolean firstValue = true;
    for( MClass stClass : StereotypeEnumToDescriptorTransformation.getStereotypeList(mc) ) {
        if( !firstValue ) {
            sb.append(",\n");
        }
        sb.append(generateSTypeJSON(stClass));
        firstValue = false;
    }
    return sb.toString();
}

"""{
  "comment": "${ProtectionStrategieDefaultImpl.GENERATED_LINE}",
  "cartridge": "${cartridgeName(model)}",
  "stereotypes": [
${listStereotypes(mClass)}  
  ]
}
"""
