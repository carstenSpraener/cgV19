package de.spraener.nextgen.vpplugin.groovy;

import com.google.gson.Gson;
import de.spraener.nextgen.vpplugin.CgV19Plugin;
import de.spraener.nextgen.vpplugin.oom.OOMExporter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class OOMDiffService {

    private static final String BASELINE_DIR = ".cgv19-baseline";
    private static Gson gson = new Gson();

    /**
     * Speichert aktuellen OOM-Export als Baseline.
     */
    public void saveBaseline() {
        Path baselineDir = getBaselineDir();
        try {
            Files.createDirectories(baselineDir);
            // Exportiere alle Root-Packages als Baseline
            saveAllPackages(baselineDir);
            CgV19Plugin.log("Baseline saved to " + baselineDir);
        } catch (IOException e) {
            CgV19Plugin.log("Failed to save baseline: " + e.getMessage());
        }
    }

    /**
     * Gibt Text-Diff zwischen Baseline und aktuellem Modell.
     * 
     * @param packageName der Package-Namen zu diffen
     * @return Diff-Text (leer wenn keine Änderungen)
     */
    public String getDiff(String packageName) {
        try {
            String current = OOMExporter.exportByPackageName(packageName);
            Path baselineFile = getBaselineDir().resolve(sanitizeFileName(packageName) + ".oom");
            
            if (!Files.exists(baselineFile)) {
                return "No baseline found for package '" + packageName + "'. Run save_baseline first.";
            }
            
            String baseline = Files.readString(baselineFile);
            return computeDiff(baseline, current);
        } catch (Exception e) {
            CgV19Plugin.log(e);
            return "Error computing diff: " + e.getMessage();
        }
    }

    /**
     * Update Baseline nach erfolgreichem Groovy-Run.
     */
    public static void updateBaseline() {
        new OOMDiffService().saveBaseline();
    }

    private Path getBaselineDir() {
        return Path.of(System.getProperty("user.dir"), BASELINE_DIR);
    }

    private void saveAllPackages(Path baselineDir) {
        try {
            com.vp.plugin.ApplicationManager appMgr = com.vp.plugin.ApplicationManager.instance();
            if (appMgr == null) return;
            
            com.vp.plugin.model.IProject project = appMgr.getProjectManager().getProject();
            if (project == null) return;
            
            for (com.vp.plugin.model.IModelElement me : project.toModelElementArray()) {
                if (me.getModelType().equals(com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_PACKAGE)) {
                    String fqName = OOMExporter.getFQName(me);
                    savePackage(baselineDir, fqName);
                } else if (me.getModelType().equals(com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_MODEL)) {
                    String name = me.getName();
                    savePackage(baselineDir, name);
                }
            }
        } catch (Exception e) {
            CgV19Plugin.log("Failed to save all packages: " + e.getMessage());
        }
    }

    private void savePackage(Path baselineDir, String packageName) {
        try {
            // Use OOMExporter to export this package
            String exported = OOMExporter.exportByPackageName(packageName);
            Path targetFile = baselineDir.resolve(sanitizeFileName(packageName) + ".oom");
            Files.writeString(targetFile, exported);
        } catch (Exception e) {
            CgV19Plugin.log("Failed to save package " + packageName + ": " + e.getMessage());
        }
    }

    private String computeDiff(String baseline, String current) {
        List<String> baselineLines = new ArrayList<>(Arrays.asList(baseline.split("\n")));
        List<String> currentLines = new ArrayList<>(Arrays.asList(current.split("\n")));
        
        Set<String> baselineSet = new HashSet<>(baselineLines);
        Set<String> currentSet = new HashSet<>(currentLines);
        
        StringBuilder diff = new StringBuilder();
        
        for (String line : currentLines) {
            if (!baselineSet.contains(line.trim())) {
                diff.append("+ ").append(line).append("\n");
            }
        }
        
        for (String line : baselineLines) {
            if (!currentSet.contains(line.trim())) {
                diff.append("- ").append(line).append("\n");
            }
        }
        
        return diff.toString().trim();
    }

    private String sanitizeFileName(String packageName) {
        return packageName.replace('.', '_').replace('/', '_');
    }
}
