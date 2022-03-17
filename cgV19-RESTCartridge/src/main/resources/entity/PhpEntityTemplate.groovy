package entity

import de.spraener.nxtgen.cartridge.rest.RESTStereotypes
import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.cartridge.rest.php.PhpHelper
import de.spraener.nxtgen.model.Stereotype
import de.spraener.nxtgen.cartridge.rest.RESTJavaHelper
import de.spraener.nxtgen.model.impl.ModelElementImpl
import de.spraener.nxtgen.oom.StereotypeHelper

def findStereotype(eStType, me = modelElement) {
    for (Stereotype sType : me.getStereotypes()) {
        if (sType.getName().equals(eStType.name)) {
            return sType;
        }
    }
    return null;
}

def pkgName = RESTJavaHelper.toPkgName(modelElement.getPackage());
def cName = modelElement.name;
def dbTable = findStereotype(RESTStereotypes.ENTITY).getTaggedValue("dbTable")

String extractPhpTypeDefinition(em) {
    def dbFieldType = readDbFieldTaggedValue(em, "type");
    if( dbFieldType==null ) {
        Stereotype sType = findStereotype(RESTStereotypes.DBFIELD, em);
        if( sType!=null ) {
            dbFieldType = PhpHelper.toPhpORMType(sType.getTaggedValue('dbType'));
        }
    } else {
        dbFieldType = "type=\"${dbFieldType}\"";
    }
    def length=readDbFieldTaggedValue(em, "length");
    if( length != null ) {
        dbFieldType += ", length=${length}";
    }
    def precision=readDbFieldTaggedValue(em, "precision");
    if( precision != null ) {
        dbFieldType += ", precision=${precision}";
    }
    def scale=readDbFieldTaggedValue(em, "scale");
    if( scale != null ) {
        dbFieldType += ", scale=${precision}";
    }
    def unique=readDbFieldTaggedValue(em, "unique");
    if( unique != null && "true".equalsIgnoreCase(unique)) {
        dbFieldType += ", unique=true";
    }
    def nullable=readDbFieldTaggedValue(em, "nullable");
    if( nullable != null && "true".equalsIgnoreCase(nullable)) {
        dbFieldType += ", nullable=true";
    }
    return dbFieldType;
}

String attributeDefinition() {
    StringBuilder sb = new StringBuilder();
    modelElement.attributes.each({
        Stereotype sType = findStereotype(RESTStereotypes.DBFIELD, it);
        columnDef = "    /**\n";
        if (sType != null) {
            dbFieldName = sType.getTaggedValue('dbFieldName')
            dbFieldType = extractPhpTypeDefinition(it);
            if ("true".equals(sType.getTaggedValue("isKey"))) {
                columnDef += "     * @ORM\\Id\n";
                columnDef += "     * @ORM\\GeneratedValue\n"
                dbFieldType = "type=\"integer\"";
            }
            columnDef += "     * @ORM\\Column(${dbFieldType})\n";
        }
        columnDef += "     */\n";
        sb.append("${columnDef}    private \$${it.name};\n");
    })
    return sb.toString();
}

String readDbFieldTaggedValue( em, value ) {
    try {
        return ((ModelElementImpl) em).getTaggedValue(RESTStereotypes.PERSISTENT_FIELD.getName(), value);
    } catch( Exception e ) {
        return null;
    }
}

String attrArrayFilling() {
    StringBuilder sb = new StringBuilder();
    modelElement.attributes.each({
        accName = "${it.name.charAt(0).toUpperCase()}${it.name.substring(1)}";
        sb.append("\n            '${it.name}' => \$this->get${accName}(),");
    });
    return sb.toString();
}

String attrArrayReading(String data) {
    // empty($data['name']) ? true : $this->setName($data['name']);
    StringBuilder sb = new StringBuilder();
    modelElement.attributes.each({
        accName = "${it.name.charAt(0).toUpperCase()}${it.name.substring(1)}";
        sb.append("\n        empty(\$${data}['${it.name}']) ? true : \$this->set${accName}(\$${data}['${it.name}']);");
    });
    return sb.toString();
}

String attributeAccess() {
    StringBuilder sb = new StringBuilder();
    modelElement.attributes.each({
        accName = "${it.name.charAt(0).toUpperCase()}${it.name.substring(1)}";
        def valueConverted = "\$value";
        def valueReadConverted = "${it.name}";
        if( readDbFieldTaggedValue(it, "type").equals("date")) {
            valueConverted = "\\DateTime::createFromFormat(\"d.m.Y\", \$value);"
            valueReadConverted = "${it.name}->format(\"d.m.Y\");";
        }
        sb.append(
                """
    public function get${accName}() {
        return \$this->${valueReadConverted};
    }

    public function set${accName}( \$value) {
        \$this->${it.name} = $valueConverted;
        return \$this;
    }
"""
        );
    })
    return sb.toString();

}

String attrMetaInf() {
    StringBuilder sb = new StringBuilder();
    modelElement.attributes.each({
        def notNull = "true";
        def listed = "true";
        def type = "string";
        def label = it.name;
        if (StereotypeHelper.hasStereotye(it, RESTStereotypes.PERSISTENT_FIELD.getName()) ){
            label = readDbFieldTaggedValue(it, "label")
            if (label == null) {
                label = it.name;
            }

            def detailOnly = readDbFieldTaggedValue(it, "detailOnly")
            if (detailOnly == null || "false".equals(detailOnly.toLowerCase())) {
                listed = "true";
            } else {
                listed = "false";
            }

            type = readDbFieldTaggedValue(it, "type")
            if (type == null) {
                type = "string";
            }

            def nullable = readDbFieldTaggedValue(it, "nullable")
            if (nullable == null || "false".equals(nullable.toLowerCase())) {
                notNull = "false";
            }
        }
        sb.append(
                """                [
                       'name'=>'${it.name}',
                       'type'=>'${type}',
                       'listed'=>'${listed}',
                       'notNull'=>'${notNull}',
                       'label' => '${label}'
                   ],
"""
        );
    });
    return sb.toString();
}

return """<?php
// ${ProtectionStrategie.GENERATED_LINE}
namespace App\\Entity;

use App\\Repository\\UserRepository;
use Doctrine\\ORM\\Mapping as ORM;

/**
 * @ORM\\Entity(repositoryClass=${cName}Repository::class)
 */
class ${cName} {
${attributeDefinition()}

    public function __construct() {
    }
    
${attributeAccess()}
    public static function fromArray(\$data) {
        \$entity = new ${cName}();
        return \$entity->updateFromArray(\$data);
    }
    
    public function updateFromArray(\$data) {
        ${attrArrayReading("data")}
        return \$this;
    }

    public function toArray() {
        \$data = [${attrArrayFilling()}
        ];
        return \$data;
    }

    public static function META_INF() {
        return [
            'name' => '${cName}',
            'key'=>'id',
            'properties'=> [
${attrMetaInf()}
            ]
        ];
    }
}
"""
