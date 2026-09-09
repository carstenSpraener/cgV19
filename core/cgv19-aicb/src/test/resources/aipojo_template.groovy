import de.spraener.nxtgen.aicb.AiCodeBlock
import de.spraener.nxtgen.oom.model.MClass

MClass mc = modelElement

String className = mc.getName()
String packageName = mc.getPackage().getFQName()

def attributes = mc.getAttributes().collect { attr ->
  "  private ${attr.getType()} ${attr.getName()};"
}.join("\n")

def operations = mc.getOperations().collect { op ->
  "  // TODO: implement ${op.getName()}"
}.join("\n")

String prompt = """Generate a Java class with the following structure:
package ${packageName};

public class ${className} {
${attributes}

${operations}
}

Return only the complete Java class code, nothing else."""

AiCodeBlock block = AiCodeBlock
        .resolve(mc, prompt)
        .resolve(mc,"Füge eine equals- und eine hash-Methode basierend auf den Attributen hinzu", AiCodeBlock::removeJavaFence)

block.toCode();