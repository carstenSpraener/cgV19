package de.spraener.nxtgen.cartridge.rest;

public enum RESTStereotypes {
    RESSOURCE("Ressource"),
    ENTITY("Entity"),
    PERSISTENT_FIELD("PersistentField"),
    LINK("Link"),
    RSRC_LINK("RsrcLink"),
    DDL("DDL"),
    REPOSITORY("Repository"),
    CONTROLLER("RESTController"),
    LOGIC("RESTLogic"),
    REQUEST("Request"),
    JSONTYPE("JSONType"),
    TSTYPE("TSType"),
    ANGULAR_SERVICE("AngularService"),
    DBFIELD("DbField"),
    SPRING_BOOT_APP("SpringBootApp"),
    IMPL("Impl"),
    CONTROLLED_OPERATION("ControlledOperation"),
    CONTROLLED_OPERATION_NODE("ControlledOperationNode"),
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
