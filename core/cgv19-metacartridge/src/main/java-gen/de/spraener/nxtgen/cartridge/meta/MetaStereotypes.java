// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.cartridge.meta;

public enum MetaStereotypes {
        STEREOTYPEENUM("StereotypeEnum"),
        STEREOTYPE("Stereotype"),
        TRANSFORMATION("Transformation"),
        MODELROOT("ModelRoot"),
        CGV19CARTRIDGE("cgV19Cartridge"),
        CODEGENERATOR("CodeGenerator"),
        GROOVYTEMPLATE("GroovyTemplate"),
        TRANSFORMATIONBASE("TransformationBase"),
        STEREOTYPEDESCRIPTOR("StereotypeDescriptor")
    ;

    private String name;

    MetaStereotypes(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }
}
