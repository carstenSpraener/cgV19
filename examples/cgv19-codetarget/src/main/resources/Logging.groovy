// Logging.groovy - External aspect script for logging functionality
// This script is evaluated by ct.evaluate('/Logging.groovy')
// It has access to: ct (CodeTarget), mClass (MClass), modelElement

import de.spraener.nxtgen.target.SimpleCodeSection
import de.spraener.nxtgen.target.java.JavaSections

ct.forAspect('external-logging') {
    // Add additional logging imports if needed
    to JavaSections.IMPORTS, "import java.util.logging.Level;"

    // Hierarchical sections: the methods live in a nested scope inside METHODS.
    // The renderer indents scope content, so the snippets carry no manual indentation.
    ct.getSection(JavaSections.METHODS).getOrCreateScope('logging', { new SimpleCodeSection() })

    // Address the nested scope via Unix-style path syntax (METHODS is an enum key,
    // resolved by its section id).
    to 'METHODS/logging', """
public void logDebug(String message) {
LOGGER.log(Level.FINE, "DEBUG: " + message);
}

public void logInfo(String message) {
LOGGER.log(Level.INFO, "INFO: " + message);
}
"""
}
