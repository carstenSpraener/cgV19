import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.pojo.PoJoCodeTargetCreator
import de.spraener.nxtgen.target.java.JavaSections

// 1. Define mClass as a global variable (cast from modelElement)
def MClass mClass = (MClass) modelElement

// 2. Get CodeTarget from PoJo cartridge (reusable base with JavaSections)
def ct = new PoJoCodeTargetCreator(mClass).createPoJoTarget()

// 3. Set default model element for chainable DSL
ct.setDefaultModelElement(mClass)

// 4. Apply custom aspects using the chainable DSL
//    Note: Section IDs must match JavaSections enum values!

ct.forAspect('logging') {
    // Add Logger import (deduplicated by UNIQUE_LINES section)
    to JavaSections.IMPORTS, "import java.util.logging.Logger;"
    
    // Add Logger field to class body (CLASS_BLOCK_BEGIN is before attributes)
    to JavaSections.CLASS_BLOCK_BEGIN, 
        "    private static final Logger LOGGER = Logger.getLogger(${mClass.name}.class.getName());"
    
    // Add logging call before constructor close using beforeSnippet
    beforeSnippet JavaSections.CONSTRUCTORS, [aspect: 'clazz-default-constructor.close'], 
        "        LOGGER.trace(\"new Instance of ${mClass.name}.\");"
}

ct.forAspect('entity') {
    // Add JPA imports
    to JavaSections.IMPORTS, "import javax.persistence.*;"
    
    // Add @Entity and @Table annotations before class declaration
    def tableName = mClass.name.toLowerCase()
    beforeSnippet JavaSections.CLASS_DECLARATION, [aspect: 'clazz-frame'], 
        "@Entity \n@Table(\"${tableName}\")"
}

// 5. Evaluate external scripts for additional aspects
ct.evaluate('/Logging.groovy')   // Loads and executes Logging.groovy from classpath
ct.evaluate('/Entity.groovy')    // Loads and executes Entity.groovy from classpath

// 6. Return the CodeTarget (will be rendered by GroovyCodeBlockImpl)
return ct
