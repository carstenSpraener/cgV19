package de.spraener.nxtgen.symfony.php;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.oom.model.MAssociation;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.php.PhpHelper;
import de.spraener.nxtgen.target.CodeBlockSnippet;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SingleLineSnippet;

import static de.spraener.nxtgen.oom.cartridge.JavaHelper.firstToUpperCase;

public class PhpPlainClassCreator {
    private MustacheFactory mf = new DefaultMustacheFactory();

    public static final String PLAIN_CLASS = "php-plain-class";
    public static final String JSON_SERIALIZABLE = "php-json-serializable";
    public static final String ATTRIBUTES = "php-attribute";
    public static final String ASSOCIATIONS = "php-associations";
    public static final String GETTER = "php-getter";
    public static final String SETTER = "php-setter";
    public static final String ADDER = "php-add";
    public static final String REMOVER = "php-remove";
    public static final String IMPORT = "php-use";

    private MClass mc;
    private CodeTarget codeTarget;

    public PhpPlainClassCreator(MClass mc) {
        this.mc = mc;
        codeTarget = new PhpClassFrameTargetCreator(mc).createPhoClassTarget();
        codeTarget.inContext(PLAIN_CLASS, mc,
                this::handleAttributes,
                this::handleAssociations
        );
    }

    public CodeTarget getCodeTarget() {
        return codeTarget;
    }

    protected void handleAssociations(CodeTarget codeTarget) {
        for (MAssociation assoc : mc.getAssociations()) {
            if( "null".equals(assoc.getName()) ) {
                continue;
            }
            MClass assocTarget = (MClass) ModelHelper.findByFQName(mc.getModel(), assoc.getType(), ".");
            final String dataType = PhpCodeHelper.isToN(assoc) ? "Collection" : assocTarget.getName();
            codeTarget.inContext(ASSOCIATIONS, assoc,
                    t -> t.getSection(PhpSections.ATTRIBUTE_DECLARATIONS)
                            .add(new SingleLineSnippet("    private "+dataType+" $" + assoc.getName() + ";")),
                    t -> {
                        if (PhpCodeHelper.isDifferentNamespace(mc, assocTarget)) {
                            t.getSection(PhpSections.IMPORTS)
                                    .add(new SingleLineSnippet(PhpCodeHelper.toImportStatement(assocTarget)));
                        }
                    },
                    t -> createAssociationMethods(t, assoc)
            );
        }
    }

    public void handleAttributes(CodeTarget t) {
        for (MAttribute a : mc.getAttributes()) {
            String type = PhpCodeHelper.toPhpType(a);
            t.inContext(ATTRIBUTES, a,
                    aTarget -> aTarget.getSection(PhpSections.ATTRIBUTE_DECLARATIONS)
                            .add(new SingleLineSnippet("    private "+type+" $" + a.getName() + ";")),
                    aTarget -> this.createAccessMethods(aTarget, a),
                    aTarget -> {
                        if( "DateTime".equals(type)) {
                            aTarget.getSection(PhpSections.IMPORTS).add(new SingleLineSnippet("use \\DateTime;"));
                        }
                    }
            );
        }
    }

    private void createAccessMethods(CodeTarget ct, MAttribute a) {
        String aName = a.getName();
        ct.getSection(PhpSections.METHODS).add(getterMethod(GETTER, a, aName));
        ct.getSection(PhpSections.METHODS).add(setterMethod(SETTER, a, aName));
        if (!PhpHelper.isBaseType(a.getType())) {
            MClass targetClass = ((OOModel) a.getModel()).findClassByName(a.getType());
            ct.getSection(PhpSections.IMPORTS).add(
                    new CodeBlockSnippet(IMPORT, a, PhpCodeHelper.toImportStatement(targetClass))
            );
        }
    }

    private static CodeBlockSnippet setterMethod(Object aspect, ModelElement e, String aName) {
        return CodeBlockSnippet.fromMustacheTemplate(aspect, e,
                """
                    public function set{{accessName}}( $value ) {
                        $this->{{name}} = $value;
                    }
                """,
                "accessName="+firstToUpperCase(aName),
                "name="+aName
                );
    }

    private static CodeBlockSnippet getterMethod(Object aspect, ModelElement e, String aName) {
        return CodeBlockSnippet.fromMustacheTemplate(aspect, e,
                """
                    public function get{{accessName}}() {
                        return $this->{{name}};
                    }
                """,
                "accessName="+firstToUpperCase(aName),
                "name="+aName
        );
    }
    private void createAssociationMethods(CodeTarget ct, MAssociation assoc) {
        ct.getSection(PhpSections.METHODS)
                .add(getterMethod(GETTER, assoc, assoc.getName()))
                .add(setterMethod(SETTER, assoc, assoc.getName()))
        ;
        if (PhpCodeHelper.isToN(assoc)) {
            ct.getSection(PhpSections.METHODS)
                    .add(CodeBlockSnippet.fromMustacheTemplate(ADDER, assoc,
                            """
                                public function addTo{{accessName}}( $value ) {
                                    $this->{{name}}[] = $value;
                                }
                            """,
                            "accessName="+firstToUpperCase(assoc.getName()),
                            "name="+assoc.getName()
                            ))
                    .add(CodeBlockSnippet.fromMustacheTemplate(REMOVER, assoc,
                            """
                                public function removeFrom{{accessName}}( $value ) {
                                    $key = array_search($value, $this->{{name}});
                                    if( $key ) {
                                        unset($this->{{name}}[$key]);
                                        $this->{{name}} = array_values($this->{{name}});
                                    }
                                }
                            """,
                            "accessName="+firstToUpperCase(assoc.getName()),
                            "name="+assoc.getName()
                            ))
            ;
        }
    }

    PhpPlainClassCreator jsonSerializable() {
        this.codeTarget.inContext(JSON_SERIALIZABLE, this.mc,
                t -> t.getSection(PhpSections.IMPORTS).add(new SingleLineSnippet("use Symfony\\Component\\Serializer\\Encoder\\JsonEncoder;")),
                t -> t.getSection(PhpSections.IMPORTS).add(new SingleLineSnippet("use Symfony\\Component\\Serializer\\Normalizer\\AbstractNormalizer;")),
                t -> t.getSection(PhpSections.IMPORTS).add(new SingleLineSnippet("use Symfony\\Component\\Serializer\\Normalizer\\ObjectNormalizer;")),
                t -> t.getSection(PhpSections.IMPORTS).add(new SingleLineSnippet("use Symfony\\Component\\Serializer\\Serializer;")),
                t -> t.getSection(PhpSections.IMPLEMENTS).add(new CodeBlockSnippet("\\JsonSerializable")),
                t -> t.getSection(PhpSections.METHODS).add( new CodeBlockSnippet(
                        """
                            public function jsonSerialize(): mixed {
                                $encoders = [new JsonEncoder()];
                                $defaultContext = [
                                    AbstractNormalizer::CIRCULAR_REFERENCE_HANDLER => function (object $object, string $format, array $context): string {
                                        return $object->getId();
                                    },
                                ];
                                $normalizers = [new ObjectNormalizer(
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    $defaultContext)];
                        
                                $serializer = new Serializer($normalizers, $encoders);
                        
                                return $serializer->serialize($this, 'json');
                            }
                        """
                ))
        );
        return this;
    }
}
