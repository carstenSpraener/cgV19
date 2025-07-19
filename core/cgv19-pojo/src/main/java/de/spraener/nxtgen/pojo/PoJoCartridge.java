package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.SimpleStringCodeBlock;
import de.spraener.nxtgen.cartridges.AnnotatedCartridgeImpl;
import de.spraener.nxtgen.annotations.CGV19Cartridge;
import de.spraener.nxtgen.cartridges.EvaluationRequest;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MActivity;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.java.JavaSections;

@CGV19Cartridge("PoJo-Cartridge")
public class PoJoCartridge extends AnnotatedCartridgeImpl {
    public static final String ST_POJO = "PoJo";
    public static final Stereotype POJO_STEREOTYPE = new StereotypeImpl(ST_POJO);
    public static final String ACTIVITY_ASPECT = "activityAsMethod";

}
