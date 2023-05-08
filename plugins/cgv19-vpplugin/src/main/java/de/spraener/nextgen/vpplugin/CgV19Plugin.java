package de.spraener.nextgen.vpplugin;

import de.spraener.nextgen.vpplugin.CgV19RESTServer;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class CgV19Plugin implements com.vp.plugin.VPPlugin {

    public static void log(Throwable t) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        t.printStackTrace(pw);
        pw.flush();
        pw.close();
        log("Error: "+new String(baos.toByteArray(), StandardCharsets.UTF_8));
    }

    public void loaded(com.vp.plugin.VPPluginInfo info) {
        new CgV19RESTServer().init();
        log("cgv19-Plugin started");
    }

    public void unloaded() {
    }

    public static void log(String message) {
        System.out.println(message);
        try {
            com.vp.plugin.ApplicationManager.instance().getViewManager().showMessage(message);
        } catch( Exception e ) {}
    }

    public static void main( String[] args) throws Exception {
        new CgV19RESTServer().init();
    }
}
