// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.cartridge.rest;

public enum RESTStereotypes {
        CGV19PROJECT("cgV19Project"),
        SPRINGBOOTAPP("SpringBootApp"),
        RESSOURCE("Ressource"),
        ENTITY("Entity"),
        CONTROLLEDOPERATION("ControlledOperation"),
        INTERACTIVE("Interactive"),
        FLOW("Flow"),
        PERSISTENTFIELD("PersistentField"),
        PHPSYMFONYAPPLICATION("PhpSymfonyApplication"),
        LINK("Link"),
        RSRCLINK("RsrcLink"),
        DDL("DDL"),
        REPOSITORY("Repository"),
        RESTCONTROLLER("RESTController"),
        LOGIC("Logic"),
        REQUEST("Request"),
        JSONTYPE("JSONType"),
        TSTYPE("TSType"),
        ANGULARSERVICE("AngularService"),
        DBFIELD("DbField"),
        IMPL("Impl"),
        CONTROLLEDOPERATIONNODE("ControlledOperationNode")
    ;

    private String name;

    RESTStereotypes(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }
}
