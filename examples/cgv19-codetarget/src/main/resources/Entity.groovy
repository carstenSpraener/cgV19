// Entity.groovy - External aspect script for JPA entity functionality
// This script is evaluated by ct.evaluate('Entity.groovy')
// It has access to: ct (CodeTarget), mClass (MClass), modelElement

import de.spraener.nxtgen.target.java.JavaSections

ct.forAspect('external-entity') {
    // Add @Id and @GeneratedValue to first attribute if needed
    def hasId = false
    for (attr in mClass.attributes) {
        if (attr.name.toLowerCase().contains('id')) {
            hasId = true
            break
        }
    }
    
    if (!hasId) {
        // Add id field and annotation (ATTRIBUTE_DECLARATIONS section)
        to JavaSections.ATTRIBUTE_DECLARATIONS, """
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

"""
        // Add getter/setter for id (METHODS section)
        to JavaSections.METHODS, """
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

"""
    }
    
    // Add toString method for debugging (METHODS section)
    to JavaSections.METHODS,
"""
    @Override
    public String toString() {
        return "${mClass.name}{id=" + id + "}";
    }

"""
}
