package entites

import de.spraener.nxtgen.cartridge.rest.RESTStereotypes
import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.oom.model.MAssociation
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel
import de.spraener.nxtgen.php.PhpHelper
import de.spraener.nxtgen.model.Stereotype
import de.spraener.nxtgen.cartridge.rest.RESTJavaHelper
import de.spraener.nxtgen.model.impl.ModelElementImpl
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.symfony.php.PhpClassFrameTargetCreator
import de.spraener.nxtgen.symfony.php.PhpCodeHelper
import de.spraener.nxtgen.symfony.php.PhpPlainClassCreator
import de.spraener.nxtgen.symfony.php.PhpSections
import de.spraener.nxtgen.target.CodeBlockSnippet
import de.spraener.nxtgen.target.CodeTarget
import de.spraener.nxtgen.target.CodeTargetCodeBlockAdapter
import de.spraener.nxtgen.target.SingleLineSnippet

def findStereotype(eStType, me = modelElement) {
    for (Stereotype sType : me.getStereotypes()) {
        if (sType.getName().equals(eStType.name)) {
            return sType;
        }
    }
    return null;
}

def mClass = (MClass)modelElement;
def cName = modelElement.name;
def model = modelElement.getModel();

String extractPhpTypeDefinition(em) {
    def dbFieldType = readDbFieldTaggedValue(em, "type");
    if( dbFieldType==null ) {
        Stereotype sType = findStereotype(RESTStereotypes.DBFIELD, em);
        if( sType!=null ) {
            dbFieldType = PhpHelper.toPhpORMType(sType.getTaggedValue('dbType'));
        }
    } else {
        dbFieldType = "type: \"${dbFieldType}\"";
    }
    def length=readDbFieldTaggedValue(em, "length");
    if( length != null ) {
        dbFieldType += ", length: ${length}";
    }
    def precision=readDbFieldTaggedValue(em, "precision");
    if( precision != null ) {
        dbFieldType += ", precision: ${precision}";
    }
    def scale=readDbFieldTaggedValue(em, "scale");
    if( scale != null ) {
        dbFieldType += ", scale: ${precision}";
    }
    def unique=readDbFieldTaggedValue(em, "unique");
    if( unique != null && "true".equalsIgnoreCase(unique)) {
        dbFieldType += ", unique: true";
    }
    def nullable=readDbFieldTaggedValue(em, "nullable");
    if( nullable != null && "true".equalsIgnoreCase(nullable)) {
        dbFieldType += ", nullable: true";
    }
    return dbFieldType;
}

void attributeDefinition(CodeTarget codeTarget) {
    modelElement.attributes.each({
        Stereotype sType = findStereotype(RESTStereotypes.DBFIELD, it);
        if (sType != null) {
            dbFieldName = sType.getTaggedValue('dbFieldName')
            dbFieldType = extractPhpTypeDefinition(it);
            columnDef = "";
            if ("true".equals(sType.getTaggedValue("isKey"))) {
                columnDef += "    #[ORM\\Id]\n";
                columnDef += "    #[ORM\\GeneratedValue]\n"
                dbFieldType = "type: \"integer\"";
            }
            columnDef += "    #[ORM\\Column(${dbFieldType})]\n";
        }
        codeTarget.getSection(PhpSections.ATTRIBUTE_DECLARATIONS)
                .getFirstSnippetForAspectAndModelElement(PhpPlainClassCreator.ATTRIBUTES, it)
                .insertBefore(new CodeBlockSnippet("Entity", it, columnDef))
    })
}

String generateORMMapping(OOModel model, MAssociation a) {
    MClass target = model.findClassByName(a.getType());
    String mappedBy = a.getOpositeAttribute();
    if( mappedBy == null || "null".equals(mappedBy) ) {
        return "    #[ORM\\${a.getAssociationType()}(targetEntity: ${target.getName()}::class)]\n"
    }
    String kind = "OneToOne";
    if( PhpCodeHelper.isOneToMany(a) ) {
        kind = "OneToMany"
        return"    #[ORM\\${kind}(targetEntity: ${target.getName()}::class, mappedBy: '${mappedBy}')]\n"
    }
    if( PhpCodeHelper.isManyToOne(a) ) {
        kind = "ManyToOne"
        return "    #[ORM\\${kind}(targetEntity: ${target.getName()}::class, inversedBy: '${mappedBy}')]\n"
    }
    if( PhpCodeHelper.isManyToMany(a)) {
        kind = "ManyToMany"
        mapKind = "from".equals(a.getProperty("direction")) ? "mappedBy" : "inversedBy"
        return"    #[ORM\\${kind}(targetEntity: ${target.getName()}::class, ${mapKind}: '${mappedBy}')]\n"
    }
    if( PhpCodeHelper.isOneToOne(a)) {
        kind = "OneToOne"
        return"    #[ORM\\${kind}(targetEntity: ${target.getName()}::class, mappedBy: '${mappedBy}')]\n"
    }
    return "";
}

void associationDefinitions( OOModel model, CodeTarget codeTarget) {
    ((MClass)modelElement).associations.each({
        if( !"null".equals(it.getName()) ) {
            codeTarget.getSection(PhpSections.ATTRIBUTE_DECLARATIONS)
                .getFirstSnippetForAspectAndModelElement(PhpPlainClassCreator.ASSOCIATIONS, it)
                .insertBefore(generateORMMapping(model, it))
        }
    })
}

String readDbFieldTaggedValue( em, value ) {
    try {
        return ((ModelElementImpl) em).getTaggedValue(RESTStereotypes.PERSISTENTFIELD.getName(), value);
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

String arrayBlock() {
    cName = modelElement.getName();
    return """
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
    }"""
}

String attrMetaInf() {
    cName = modelElement.getName();
    StringBuilder sb = new StringBuilder();
    modelElement.attributes.each({
        def notNull = "true";
        def listed = "true";
        def type = "string";
        def label = it.name;
        if (StereotypeHelper.hasStereotype(it, RESTStereotypes.PERSISTENTFIELD.getName()) ){
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

String metaInfBlock() {
    return """
    public static function META_INF() {
        return [
            'name' => '${cName}',
            'key'=>'id',
            'properties'=> [
${attrMetaInf()}
            ]
        ];
    }
"""
}

CodeTarget codeTarget = new PhpPlainClassCreator((MClass)modelElement)
        .jsonSerializable()
        .codeTarget;

replaceDefaultContructor(codeTarget, mClass)

codeTarget.inContext("ENTITY", modelElement,
        ct -> ct.getSection(PhpSections.IMPORTS)
                .add(new SingleLineSnippet("use App\\Repository\\${cName}Repository;")),
        ct -> ct.getSection(PhpSections.IMPORTS)
                .add(new SingleLineSnippet("use Doctrine\\Common\\Collections\\ArrayCollection;"))
                .add(new SingleLineSnippet("use Doctrine\\Common\\Collections\\Collection;\n")),
        ct -> ct.getSection(PhpSections.IMPORTS)
                .add(new SingleLineSnippet("use Doctrine\\ORM\\Mapping as ORM;\n")),

        ct -> ct.getSection(PhpSections.CLASS_DECLARATION)
                .getFirstSnippetForAspect(PhpClassFrameTargetCreator.CLAZZ_FRAME)
                .insertBefore(new CodeBlockSnippet(
"""#[ORM\\Entity(repositoryClass: ${cName}Repository::class)]
"""
                )),
        ct -> ct.getSection(PhpSections.METHODS)
            .add(new CodeBlockSnippet(arrayBlock())),
    ct -> ct.getSection(PhpSections.METHODS)
            .add(new CodeBlockSnippet(metaInfBlock()))
)

attributeDefinition(codeTarget);
associationDefinitions(model, codeTarget);

return new CodeTargetCodeBlockAdapter(codeTarget).toCode()

void replaceDefaultContructor(CodeTarget codeTarget, MClass mClass) {
   codeTarget.getSection(PhpSections.CONSTRUCTORS)
    .getFirstSnippetForAspectAndModelElement(PhpClassFrameTargetCreator.DEFAULT_CONSTRUCTOR, mClass)
    .replace(new CodeBlockSnippet(
"""
    public function __constructor() {
${initAllToNReferences(mClass)}    }
"""
    ))
}

String initAllToNReferences(MClass mClass) {
    StringBuilder sb = new StringBuilder();
    for( MAssociation a : mClass.getAssociations() ) {
        if( a.getName().equals("null") ) {
            continue;
        }
        if( PhpCodeHelper.isToN(a) ) {
            sb.append( "        \$this->${a.getName()} = new ArrayCollection();\n")
        }
    }
    return sb.toString();
}
