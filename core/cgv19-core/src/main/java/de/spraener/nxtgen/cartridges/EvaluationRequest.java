package de.spraener.nxtgen.cartridges;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.target.CodeTarget;

/**
 * An EvaluationRequest is given from a generating cartridge to
 * cgV19. cgV19 will ask all known cartridges if they are able
 * to fulfill the request. If such a cartridge is found, the request
 * is given to that cartridge to generate a CodeBlock for the
 * calling cartridge. This way a cartridge can make use of generator logic
 * from another cartridge.
 */
public class EvaluationRequest {
    /**
     * The ModelElement that needs a sub evaluation
     */
    private ModelElement me;

    /**
     * A Stereotype that triggered the request
     */
    private Stereotype sType;

    /**
     * An aspect that needs to be evaluated. For example, a method
     * implementation for an FSM
     */
    private String aspect;

    /**
     * A sub aspect of the aspect. For example, required imports, fields
     * or concrete implementation.
     */
    private String subAspect;

    /**
     * A CodeTarget to take the implementation. This is optional. It can only
     * be taken from cartridges that support code target-based generation.
     * <p>
     * Note: If you set a CodeTarget on the request and the receiving cartridge can
     * handle it, the receiving cartridge is able to fulfill all necessary sections in
     * one request. It can add imports, attribute declarations, and method implementation
     * into the given CodeTarget.
     */
    private CodeTarget codeTarget;
    private String srcLanguage;

    public EvaluationRequest(ModelElement me, Stereotype sType, String aspect, String subAspect) {
        this("java", me, sType, aspect, subAspect);
    }

    public EvaluationRequest(ModelElement me, Stereotype sType, String aspect, CodeTarget codeTarget) {
        this("java", me, sType, aspect, null);
        this.codeTarget = codeTarget;
    }

    public EvaluationRequest(String srcLanguage, ModelElement me, Stereotype sType, String aspect, String subAspect) {
        this.srcLanguage = srcLanguage;
        this.me = me;
        this.sType = sType;
        this.aspect = aspect;
        this.subAspect = subAspect;
    }

    public EvaluationRequest withCodeTarget(CodeTarget codeTarget) {
        this.codeTarget = codeTarget;
        return this;
    }

    public ModelElement getMe() {
        return me;
    }

    public String getAspect() {
        return aspect;
    }

    public String getSubAspect() {
        return subAspect;
    }

    public CodeTarget getCodeTarget() {
        return codeTarget;
    }

    public Stereotype getStereotype() {
        return sType;
    }

    @Override
    public String toString() {
        return "EvaluationRequest{" +
                "me= <<"+sType.getName()+">> "+me+
                ", aspect.subAspect='" + aspect + '.' + subAspect + '\'' +
                ", codeTarget=" + codeTarget +
                '}';
    }

    public String getSourceLanguage() {
        return srcLanguage;
    }
}
