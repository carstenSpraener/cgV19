package entity

import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.cartridge.rest.RESTCartridge
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass

def tableName = de.spraener.nxtgen.oom.StereotypeHelper.getStereotype(modelElement,RESTStereotypes.DDL.getName()).getTaggedValue("dbTable");

def dbField(MAttribute a) {
    String dbField = a.getTaggedValue(RESTStereotypes.DBFIELD.getName(), "dbFieldName");
    String dbFieldType = a.getTaggedValue(RESTStereotypes.DBFIELD.getName(), "dbType");

    return "    ${dbField} ${dbFieldType}";
}

def dbFieldList(MClass ddl) {
    StringBuilder sb = new StringBuilder();
    for(MAttribute a : ddl.getAttributes() ) {
        if( a.getName().equalsIgnoreCase("id")) {
            continue;
        }
        sb.append(dbField(a));
        sb.append(",\n");
    }
    return sb.toString();
}
return """-- ${ProtectionStrategie.GENERATED_LINE}
drop table if exists ${tableName};

CREATE TABLE ${tableName} (
    id BIGINT not null AUTO_INCREMENT,
${dbFieldList(modelElement)}
    createTS TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updateTS TIMESTAMP DEFAULT 0 ON UPDATE CURRENT_TIMESTAMP,
    primary key (id)
);
"""
