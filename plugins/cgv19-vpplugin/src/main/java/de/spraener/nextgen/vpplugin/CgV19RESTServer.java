package de.spraener.nextgen.vpplugin;

import com.google.gson.Gson;
import de.spraener.nextgen.vpplugin.oom.OOMExporter;
import de.spraener.nextgen.vpplugin.oom.OOMImport;
import de.spraener.nextgen.vpplugin.dslimport.DSLImporter;
import de.spraener.nextgen.vpplugin.groovy.GroovyScriptExecutor;
import de.spraener.nextgen.vpplugin.groovy.OOMDiffService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class CgV19RESTServer {
    public static Javalin app = null;
    private static Gson gson = new Gson();

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

        // Phase 2: Neue Endpoints
        app.post("/groovy", this::handleGroovy);
        app.get("/diff/{packageName}", this::handleDiff);
        app.post("/save-baseline", this::handleSaveBaseline);

        CgV19Plugin.log("REST-Server initialized. Listening on port 7001.");
    }

    private void handleGroovy(Context ctx) {
        try {
            Map<String, String> body = gson.fromJson(ctx.body(), Map.class);
            String script = body.get("script");
            
            if (script == null) {
                ctx.result("{\"error\":\"Missing 'script' parameter\"}");
                return;
            }

            GroovyScriptExecutor executor = new GroovyScriptExecutor();
            String result = executor.execute(script);

            if (result.startsWith("Error")) {
                ctx.result("{\"error\":" + gson.toJson(result) + "}");
            } else {
                ctx.result("{\"result\":" + gson.toJson(result) + "}");
                // Update baseline after successful execution
                OOMDiffService.updateBaseline();
            }
        } catch (Exception e) {
            CgV19Plugin.log(e);
            ctx.result("{\"error\":" + gson.toJson("Failed to execute script: " + e.getMessage()) + "}");
        }
    }

    private void handleDiff(Context ctx) {
        try {
            String packageName = ctx.pathParam("packageName");
            OOMDiffService diffService = new OOMDiffService();
            String diff = diffService.getDiff(packageName);
            ctx.result("{\"diff\":" + gson.toJson(diff) + "}");
        } catch (Exception e) {
            CgV19Plugin.log(e);
            ctx.result("{\"error\":" + gson.toJson("Failed to compute diff: " + e.getMessage()) + "}");
        }
    }

    private void handleSaveBaseline(Context ctx) {
        try {
            new OOMDiffService().saveBaseline();
            ctx.result("{\"message\":\"Baseline saved.\"}");
        } catch (Exception e) {
            CgV19Plugin.log(e);
            ctx.result("{\"error\":" + gson.toJson("Failed to save baseline: " + e.getMessage()) + "}");
        }
    }
}
