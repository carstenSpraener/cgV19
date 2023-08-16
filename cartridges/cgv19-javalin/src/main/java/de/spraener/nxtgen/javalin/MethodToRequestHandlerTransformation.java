package de.spraener.nxtgen.javalin;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.cartridge.JavaHelper;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.target.CodeSection;

import java.util.ArrayList;
import java.util.List;

public class MethodToRequestHandlerTransformation extends MethodToRequestHandlerTransformationBase {

    private static final String REQUEST_HANDLER_LIST = "REQUEST_HANDLER_LIST";

    @Override
    public void doTransformationIntern(MOperation me) {
        MClass mc = (MClass)me.getParent();

        MClass handler = mc.getPackage().createMClass(JavaHelper.firstToUpperCase(me.getName())+"Handler");
        handler.setModel(me.getModel());
        for( MParameter p : me.getParameters()) {
            MAttribute attr = new MAttribute(p.getName(), p.getType());
            handler.addAttribute(attr);
            attr.setParent(handler);
        }
        handler.getOperations().add(me);
        StereotypeImpl sType = new StereotypeImpl(JavaLinStereotypes.REQUESTHANDLER.getName());
        sType.setTaggedValue("Method", me.getTaggedValue(
                JavaLinStereotypes.REQUESTHANDLER.getName(), "Method"
        ));
        sType.setTaggedValue("Path", me.getTaggedValue(
                JavaLinStereotypes.REQUESTHANDLER.getName(), "Path"
        ));
        handler.addAttribute(new MAttribute("ctx", "io.javalin.http.Context"));
        handler.getStereotypes().add(sType);
        addHandlerClass(mc, handler);
    }

    private void addHandlerClass(MClass mc, MClass handler) {
        getRequestHandlerList(mc).add(handler);
    }

    public static List<MClass> getRequestHandlerList(MClass mc) {
        List<MClass> handlerList = (List<MClass>)mc.getObject(REQUEST_HANDLER_LIST);
        if( handlerList==null) {
            handlerList = new ArrayList<>();
            mc.putObject(REQUEST_HANDLER_LIST, handlerList);
        }
        return handlerList;
    }
}
