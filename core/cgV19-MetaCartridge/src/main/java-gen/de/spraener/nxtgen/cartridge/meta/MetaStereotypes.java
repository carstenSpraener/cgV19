// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.cartridge.meta;

public enum MetaStereotypes {
        CGV19CARTRIDGE("cgV19Cartridge"),
        CODEGENERATOR("CodeGenerator"),
        GROOVYTEMPLATE("GroovyTemplate"),
        MODELROOT("ModelRoot"),
        STEREOTYPE("Stereotype"),
        STEREOTYPEENUM("StereotypeEnum"),
        TRANSFORMATION("Transformation")
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
