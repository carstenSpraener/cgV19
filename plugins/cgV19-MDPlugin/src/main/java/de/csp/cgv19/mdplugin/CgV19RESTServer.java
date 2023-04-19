package de.csp.cgv19.mdplugin;

import de.csp.cgv19.mdplugin.oom.OOMExporter;
import de.csp.cgv19.mdplugin.oom.OOMImport;
import io.javalin.Javalin;

public class CgV19RESTServer {

    public void init() {
        Javalin app = Javalin.create()
                .start(7000);
        app.get("/:rootPackage", ctx -> {
            ctx.result(OOMExporter.exportByPackageName(ctx.pathParam("rootPackage")));
        });
        app.post("/", OOMImport::importOOM);
    }
}
