package de.spraener.nxtgen.cartridge.rest.entity;

public enum JPALinkClassifier {
    MANY_TO_MANY("@ManyToMany"),
    MANY_TO_ONE("@ManyToOne"),
    ONE_TO_MANY("@OneToMany"),
    ONE_TO_ONE("@OneToOne");

    private final String jpaName;

    JPALinkClassifier(String jpaName) {
        this.jpaName = jpaName;
    }

    public String toJPA() {
        return this.jpaName;
    }
}
