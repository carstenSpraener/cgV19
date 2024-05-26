package de.spraener.nextgen.vpplugin.dslimport;

import java.util.ArrayList;
import java.util.List;

public class CartridgeDSL {
    private String cartridge;
    private List<Stereotype> stereotypes = new ArrayList<>();

    public String getCartridge() {
        return cartridge;
    }

    public void setCartridge(String cartridge) {
        this.cartridge = cartridge;
    }

    public List<Stereotype> getStereotypes() {
        return stereotypes;
    }

    public void setStereotypes(List<Stereotype> stereotypes) {
        this.stereotypes = stereotypes;
    }
}
