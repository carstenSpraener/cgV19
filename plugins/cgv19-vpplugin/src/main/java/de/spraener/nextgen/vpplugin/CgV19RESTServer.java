package de.spraener.nextgen.vpplugin;

import de.spraener.nextgen.vpplugin.oom.OOMExporter;
import de.spraener.nextgen.vpplugin.oom.OOMImport;
import de.spraener.nextgen.vpplugin.dslimport.DSLImporter;
import io.javalin.Javalin;

public class CgV19RESTServer {
    public static Javalin app = null;

    public void init() {
        app = Javalin.create()
                .start(7001);
       app.get("/{rootPackage}", ctx -> {
            ctx.result(OOMExporter.exportByPackageName(ctx.pathParam("rootPackage")));
        });

        app.get("/ping", ctx->{
            ctx.result("Pong!");
        } ) ;
        app.post("/", OOMImport::importOOM);
        app.post("/dsl", DSLImporter::handleRequest);

        CgV19Plugin.log("REST-Server initialized. Listening on port 7001.");
    }
}
