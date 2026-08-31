// Logging.groovy - External aspect script for logging functionality
// This script is evaluated by ct.evaluate('Logging.groovy')
// It has access to: ct (CodeTarget), mClass (MClass), modelElement

import de.spraener.nxtgen.target.java.JavaSections

ct.forAspect('external-logging') {
    // Add additional logging imports if needed
    to JavaSections.IMPORTS, "import java.util.logging.Level;"
    
    // Add debug logging method (METHODS section)
    to JavaSections.METHODS,
"""
    public void logDebug(String message) {
        LOGGER.log(Level.FINE, "DEBUG: " + message);
    }
    
    public void logInfo(String message) {
        LOGGER.log(Level.INFO, "INFO: " + message);
    }
"""
}
