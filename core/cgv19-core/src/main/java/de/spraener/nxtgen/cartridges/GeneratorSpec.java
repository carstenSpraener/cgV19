package de.spraener.nxtgen.cartridges;

import de.spraener.nxtgen.annotations.*;
import de.spraener.nxtgen.model.ModelElement;

public class GeneratorSpec {
    private String requiredStereotype;
    private Class<? extends ModelElement> operatesOn;
    private OutputType outputType;
    private OutputTo outputTo;
    private String templateName;
    private ImplementationKind implementationKind;
    private String outputFile;

    private GeneratorSpec() {}

    public static GeneratorSpec from(CGV19Generator cgv19Generator) {
        if( cgv19Generator == null ) {
            return null;
        }
        GeneratorSpec genSpec = new GeneratorSpec();
        genSpec.requiredStereotype = cgv19Generator.requiredStereotype();
        genSpec.operatesOn = cgv19Generator.operatesOn();
        genSpec.outputType = cgv19Generator.outputType();
        genSpec.outputTo = cgv19Generator.outputTo();
        genSpec.templateName = cgv19Generator.templateName();
        genSpec.implementationKind = cgv19Generator.implementationKind();
        return genSpec;
    }


    public static GeneratorSpec from(CGV19MustacheGenerator cgv19MustacheGenerator) {
        if( cgv19MustacheGenerator == null ) {
            return null;
        }
        GeneratorSpec genSpec = new GeneratorSpec();
        genSpec.requiredStereotype = cgv19MustacheGenerator.requiredStereotype();
        genSpec.operatesOn = cgv19MustacheGenerator.operatesOn();
        genSpec.templateName = cgv19MustacheGenerator.templateResource();
        genSpec.implementationKind = ImplementationKind.MUSTACHE;
        genSpec.outputFile = cgv19MustacheGenerator.value();
        return genSpec;
    }

    public String getRequiredStereotype() {
        return requiredStereotype;
    }

    public Class<? extends ModelElement> getOperatesOn() {
        return operatesOn;
    }

    public OutputType getOutputType() {
        return outputType;
    }

    public OutputTo getOutputTo() {
        return outputTo;
    }

    public String getTemplateName() {
        return templateName;
    }

    public ImplementationKind getImplementationKind() {
        return implementationKind;
    }

    public String getOutputFile() {
        return outputFile;
    }
}
