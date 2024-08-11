package de.spraener.nxtgen.codetarget;

import de.spraener.nxtgen.annotations.CGV19Cartridge;

@CGV19Cartridge("CodeTarget")
public class CodeTarget extends CodeTargetBase{
    public static final String NAME = "CodeTarget";



    public CodeTarget() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }

}
